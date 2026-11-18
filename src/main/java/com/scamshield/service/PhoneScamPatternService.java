package com.scamshield.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.scamshield.dto.PhoneScamPatternResponse;
import com.scamshield.entity.ScamReport;
import com.scamshield.repository.ScamReportRepository;

@Service
public class PhoneScamPatternService {

    private final ScamReportRepository scamReportRepository;

    public PhoneScamPatternService(
            ScamReportRepository scamReportRepository) {

        this.scamReportRepository = scamReportRepository;
    }

    public PhoneScamPatternResponse analyzePhone(String phone) {

        if (!StringUtils.hasText(phone)) {
            throw new IllegalArgumentException(
                    "Phone number must not be empty");
        }

        phone = phone.trim();

        List<ScamReport> reports =
                scamReportRepository
                        .findBySuspiciousPhoneAndStatusNotOrderByReportedAtDesc(
                                phone, "DELETED");

        Map<String, Long> scamTypeCounts = new HashMap<>();
        Map<String, Long> cityCounts = new HashMap<>();
        Map<String, Long> patternCounts = new HashMap<>();

        Set<Long> reporterIds = new HashSet<>();

        long verifiedReports = 0;
        long pendingReports = 0;

        for (ScamReport report : reports) {

            if (report.getReportedBy() != null
                    && report.getReportedBy().getId() != null) {

                reporterIds.add(report.getReportedBy().getId());
            }

            if ("VERIFIED".equalsIgnoreCase(report.getStatus())) {
                verifiedReports++;
            }

            if ("PENDING".equalsIgnoreCase(report.getStatus())) {
                pendingReports++;
            }

            if (StringUtils.hasText(report.getScamType())) {

                String scamType = report.getScamType().trim();

                scamTypeCounts.merge(scamType, 1L, Long::sum);
            }

            if (StringUtils.hasText(report.getIncidentCity())) {

                String city = report.getIncidentCity().trim();

                cityCounts.merge(city, 1L, Long::sum);
            }

            if (StringUtils.hasText(report.getDescription())) {

                String pattern = normalizeDescription(
                        report.getDescription());

                if (!pattern.isEmpty()) {
                    patternCounts.merge(pattern, 1L, Long::sum);
                }
            }
        }

        Map<String, Long> repeatedPatterns =
                patternCounts.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() > 1)
                        .sorted(Map.Entry.<String, Long>comparingByValue()
                                .reversed())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (first, second) -> first,
                                LinkedHashMap::new
                        ));

        Map<String, Long> sortedScamTypes =
                sortByCount(scamTypeCounts);

        Map<String, Long> sortedCities =
                sortByCount(cityCounts);

        String message;

        if (reports.isEmpty()) {

            message =
                    "No active community reports found for this number.";

        } else {

            message =
                    "Analysis is based on community-submitted reports. "
                    + "Reported patterns do not independently prove fraud.";
        }

        return new PhoneScamPatternResponse(
                phone,
                reports.size(),
                reporterIds.size(),
                verifiedReports,
                pendingReports,
                sortedScamTypes,
                sortedCities,
                repeatedPatterns,
                message
        );
    }

    private String normalizeDescription(String description) {

        return description
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private Map<String, Long> sortByCount(
            Map<String, Long> counts) {

        return counts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue()
                        .reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
    }
}