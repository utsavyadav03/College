git remote add origin https://github.com/utsavyadav03/ScamShield.git
package com.scamshield.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.scamshield.dto.PhoneNumberCheckResponse;
import com.scamshield.entity.ScamReport;
import com.scamshield.repository.ScamReportRepository;

@Service
public class PhoneIntelligenceService {

    private final ScamReportRepository scamReportRepository;

    public PhoneIntelligenceService(
            ScamReportRepository scamReportRepository) {

        this.scamReportRepository = scamReportRepository;
    }

    public PhoneNumberCheckResponse checkPhoneNumber(String phone) {

        if (!StringUtils.hasText(phone)) {
            throw new IllegalArgumentException(
                    "Phone number must not be empty");
        }

        phone = phone.trim();

        long totalReports =
                scamReportRepository.countActiveReportsByPhone(phone);

        long uniqueReporters =
                scamReportRepository.countUniqueReportersByPhone(phone);

        long verifiedReports =
                scamReportRepository.countVerifiedReportsByPhone(phone);

        List<ScamReport> reports =
                scamReportRepository
                        .findBySuspiciousPhoneOrderByReportedAtDesc(phone);

        int highestRiskScore = 0;
        String highestRiskLevel = "NO_DATA";
        String riskReason = "No risk indicators available.";
        long pendingReports = 0;

        for (ScamReport report : reports) {

            if ("DELETED".equalsIgnoreCase(report.getStatus())) {
                continue;
            }

            if ("PENDING".equalsIgnoreCase(report.getStatus())) {
                pendingReports++;
            }

            int score = report.getRiskScore();

            if (score > highestRiskScore) {

                highestRiskScore = score;

                if (StringUtils.hasText(report.getRiskLevel())) {
                    highestRiskLevel = report.getRiskLevel();
                }

                if (StringUtils.hasText(report.getRiskReason())) {
                    riskReason = report.getRiskReason();
                }
            }
        }

        String riskAssessment;
        String message;

        if (totalReports == 0) {

            riskAssessment = "NO_PREVIOUS_REPORTS";
            highestRiskScore = 0;
            highestRiskLevel = "NO_DATA";
            riskReason = "No reports were found for this number.";

            message =
                    "No previous community reports found for this number.";

        } else if (uniqueReporters >= 7) {

            riskAssessment = "HIGH_COMMUNITY_CONCERN";

            message =
                    "This number has been reported by at least 7 "
                    + "different users. This is a community warning, "
                    + "not proof of fraud.";

        } else if (uniqueReporters >= 3) {

            riskAssessment = "MULTIPLE_COMMUNITY_REPORTS";

            message =
                    "Multiple different users have reported this number. "
                    + "Review the risk indicators and verification status.";

        } else {

            riskAssessment = "LIMITED_COMMUNITY_REPORTS";

            message =
                    "Some community reports exist for this number. "
                    + "The available reports alone do not prove fraud.";
        }

        return new PhoneNumberCheckResponse(
                phone,
                totalReports,
                uniqueReporters,
                verifiedReports,
                pendingReports,
                highestRiskScore,
                highestRiskLevel,
                riskAssessment,
                riskReason,
                message
        );
    }
}
