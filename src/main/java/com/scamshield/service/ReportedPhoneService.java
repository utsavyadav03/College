
package com.scamshield.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.scamshield.dto.ReportedPhoneResponse;
import com.scamshield.repository.ScamReportRepository;

@Service
public class ReportedPhoneService {

    private final ScamReportRepository scamReportRepository;
    private final PhoneIntelligenceService phoneIntelligenceService;

    @Value("${scamshield.phone.minimum-reporters:7}")
    private long minimumReporters;

    public ReportedPhoneService(
            ScamReportRepository scamReportRepository,
            PhoneIntelligenceService phoneIntelligenceService) {

        this.scamReportRepository = scamReportRepository;
        this.phoneIntelligenceService = phoneIntelligenceService;
    }

    public List<ReportedPhoneResponse> getReportedPhones() {

        List<String> phones =
                scamReportRepository.findPubliclyReportedPhones(
                        minimumReporters);

        List<ReportedPhoneResponse> responses = new ArrayList<>();

        for (String phone : phones) {

            var check =
                    phoneIntelligenceService.checkPhoneNumber(phone);

            responses.add(
                    new ReportedPhoneResponse(
                            check.getPhone(),
                            check.getTotalReports(),
                            check.getUniqueReporters(),
                            check.getVerifiedReports(),
                            "COMMUNITY_REPORTED"
                    )
            );
        }

        return responses;
    }
}
