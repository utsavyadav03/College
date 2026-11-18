
package com.scamshield.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.scamshield.dto.CityScamAlertResponse;
import com.scamshield.repository.ScamReportRepository;

@Service
public class CityScamAlertService {

    private final ScamReportRepository scamReportRepository;

    public CityScamAlertService(
            ScamReportRepository scamReportRepository) {

        this.scamReportRepository = scamReportRepository;
    }

    public List<CityScamAlertResponse> getAlertsByCity(
            String city) {

        if (!StringUtils.hasText(city)) {
            throw new IllegalArgumentException(
                    "City must not be empty");
        }

        List<Object[]> results =
                scamReportRepository.findVerifiedScamAlertsByCity(
                        city.trim());

        List<CityScamAlertResponse> alerts =
                new ArrayList<>();

        for (Object[] result : results) {

            String reportedCity = (String) result[0];
            String scamType = (String) result[1];
            long count = ((Number) result[2]).longValue();

            CityScamAlertResponse response =
                    new CityScamAlertResponse(
                            reportedCity,
                            scamType,
                            count,
                            "Based on admin-verified community reports."
                    );

            alerts.add(response);
        }

        return alerts;
    }
}
