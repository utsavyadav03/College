
package com.scamshield.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scamshield.dto.AdminDashboardResponse;
import com.scamshield.dto.CreateScamReportRequest;
import com.scamshield.dto.ScamReportResponse;
import com.scamshield.dto.ScamReportSubmissionResponse;
import com.scamshield.entity.AdminAuditLog;
import com.scamshield.entity.ScamReport;
import com.scamshield.entity.User;
import com.scamshield.exception.ResourceNotFoundException;
import com.scamshield.exception.ReportAccessDeniedException;
import com.scamshield.repository.AdminAuditLogRepository;
import com.scamshield.repository.ScamReportRepository;
import com.scamshield.repository.UserRepository;

@Service
public class ScamReportService {

    private final ScamReportRepository scamReportRepository;
    private final UserRepository userRepository;
    private final FraudRiskAnalysisService fraudRiskAnalysisService;
    private final AdminAuditLogRepository adminAuditLogRepository;

    public ScamReportService(
            ScamReportRepository scamReportRepository,
            UserRepository userRepository,
            FraudRiskAnalysisService fraudRiskAnalysisService,
            AdminAuditLogRepository adminAuditLogRepository) {

        this.scamReportRepository = scamReportRepository;
        this.userRepository = userRepository;
        this.fraudRiskAnalysisService = fraudRiskAnalysisService;
        this.adminAuditLogRepository = adminAuditLogRepository;
    }

    @Transactional
    public ScamReportSubmissionResponse createReport(
            CreateScamReportRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        String phone = request.getSuspiciousPhone();

        // Get existing statistics before saving the new report.
        long previousReports = 0;
        long uniqueReporters = 0;
        long verifiedReports = 0;

        if (phone != null && !phone.isBlank()) {
            phone = phone.trim();

            previousReports =
                    scamReportRepository.countActiveReportsByPhone(phone);

            uniqueReporters =
                    scamReportRepository.countUniqueReportersByPhone(phone);

            verifiedReports =
                    scamReportRepository.countVerifiedReportsByPhone(phone);
        }

        ScamReport report = new ScamReport();

        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setScamType(request.getScamType());
        report.setSuspiciousPhone(phone);
        report.setIncidentCity(request.getIncidentCity());
        report.setStatus("PENDING");
        report.setReportedAt(LocalDateTime.now());
        report.setReportedBy(user);

        // Analyze the current report.
        fraudRiskAnalysisService.analyzeReport(report);

        // Save the current report.
        ScamReport savedReport = scamReportRepository.save(report);

        ScamReportResponse reportResponse =
                new ScamReportResponse(savedReport);

        return new ScamReportSubmissionResponse(
                reportResponse,
                previousReports,
                uniqueReporters,
                verifiedReports);
    }

    @Transactional(readOnly = true)
    public List<ScamReportResponse> getMyReports(String email) {

        List<ScamReport> reports =
                scamReportRepository
                        .findByReportedBy_EmailAndStatusNotOrderByReportedAtDesc(
                                email, "DELETED");

        return reports.stream()
                .map(ScamReportResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScamReportResponse getMyReportById(
            Long reportId, String email) {

        ScamReport report = scamReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Report not found"));

        if (!report.getReportedBy().getEmail().equals(email)) {
            throw new ReportAccessDeniedException(
                    "You cannot access this report");
        }

        if (report.getStatus().equals("DELETED")) {
            throw new RuntimeException("Report has been deleted");
        }

        return new ScamReportResponse(report);
    }

    @Transactional
    public ScamReportResponse updateMyReport(
            Long reportId,
            CreateScamReportRequest request,
            String email) {

        ScamReport report = scamReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Report not found"));

        if (!report.getReportedBy().getEmail().equals(email)) {
            throw new ReportAccessDeniedException(
                    "You cannot update this report");
        }

        if (report.getStatus().equals("DELETED")) {
            throw new RuntimeException(
                    "Deleted report cannot be updated");
        }

        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setScamType(request.getScamType());
        report.setSuspiciousPhone(
                request.getSuspiciousPhone() == null
                        ? null : request.getSuspiciousPhone().trim());
        report.setIncidentCity(request.getIncidentCity());

        fraudRiskAnalysisService.analyzeReport(report);

        ScamReport updatedReport = scamReportRepository.save(report);

        return new ScamReportResponse(updatedReport);
    }

    @Transactional
    public void deleteMyReport(Long reportId, String email) {

        ScamReport report = scamReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Report not found"));

        if (!report.getReportedBy().getEmail().equals(email)) {
            throw new ReportAccessDeniedException(
                    "You cannot delete this report");
        }

        if (report.getStatus().equals("DELETED")) {
            throw new RuntimeException("Report is already deleted");
        }

        report.setStatus("DELETED");

        scamReportRepository.save(report);
    }

    @Transactional
    public ScamReportResponse updateReportStatus(
            Long reportId, String status, String adminEmail) {

        ScamReport report = scamReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Report not found"));

        if (!status.equals("VERIFIED") && !status.equals("REJECTED")) {
            throw new RuntimeException(
                    "Status must be VERIFIED or REJECTED");
        }

        if (!report.getStatus().equals("PENDING")) {
            throw new RuntimeException(
                    "Only PENDING reports can be reviewed");
        }

        String oldStatus = report.getStatus();

        report.setStatus(status);

        ScamReport updatedReport = scamReportRepository.save(report);

        AdminAuditLog auditLog = new AdminAuditLog();
        auditLog.setReportId(reportId);
        auditLog.setAdminEmail(adminEmail);
        auditLog.setOldStatus(oldStatus);
        auditLog.setNewStatus(status);

        adminAuditLogRepository.save(auditLog);

        return new ScamReportResponse(updatedReport);
    }

    @Transactional
    public int recalculateMissingRiskData() {

        List<ScamReport> reports = scamReportRepository.findAll();

        int updatedCount = 0;

        for (ScamReport report : reports) {

            if (report.getRiskScore() == null
                    || report.getRiskLevel() == null
                    || report.getRiskReason() == null) {

                fraudRiskAnalysisService.analyzeReport(report);
                updatedCount++;
            }
        }

        scamReportRepository.saveAll(reports);

        return updatedCount;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardStatistics() {

        long totalReports = scamReportRepository.count();
        long pendingReports = scamReportRepository.countByStatus("PENDING");
        long verifiedReports = scamReportRepository.countByStatus("VERIFIED");
        long rejectedReports = scamReportRepository.countByStatus("REJECTED");
        long deletedReports = scamReportRepository.countByStatus("DELETED");

        long highRiskReports = scamReportRepository.countByRiskLevel("HIGH");
        long mediumRiskReports = scamReportRepository.countByRiskLevel("MEDIUM");
        long lowRiskReports = scamReportRepository.countByRiskLevel("LOW");

        return new AdminDashboardResponse(
                totalReports,
                pendingReports,
                verifiedReports,
                rejectedReports,
                deletedReports,
                highRiskReports,
                mediumRiskReports,
                lowRiskReports);
    }
}
