
package com.scamshield.controller;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.AdminDashboardResponse;
import com.scamshield.dto.ScamReportResponse;
import com.scamshield.entity.ScamReport;
import com.scamshield.exception.ResourceNotFoundException;
import com.scamshield.repository.ScamReportRepository;
import com.scamshield.service.ScamReportService;

@RestController
@RequestMapping("/api/admin")
public class AdminReportController {

    private final ScamReportRepository scamReportRepository;
    private final ScamReportService scamReportService;

    public AdminReportController(
            ScamReportRepository scamReportRepository,
            ScamReportService scamReportService) {

        this.scamReportRepository = scamReportRepository;
        this.scamReportService = scamReportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboardStatistics() {

        AdminDashboardResponse response =
                scamReportService.getDashboardStatistics();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reportedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest()
                    .body("Page must be >= 0 and size must be between 1 and 100.");
        }

        List<String> allowedSortFields =
                List.of("reportedAt", "id", "title", "riskScore", "riskLevel", "status");

        if (!allowedSortFields.contains(sortBy)) {
            return ResponseEntity.badRequest()
                    .body("Invalid sortBy field. Allowed fields: " + allowedSortFields);
        }

        Sort.Direction sortDirection;

        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        } else {
            return ResponseEntity.badRequest()
                    .body("Direction must be asc or desc.");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy));

        Page<ScamReport> reports;

        boolean hasStatus = status != null && !status.isBlank();
        boolean hasRiskLevel = riskLevel != null && !riskLevel.isBlank();

        if (hasStatus && hasRiskLevel) {
            reports = scamReportRepository.findByStatusAndRiskLevel(
                    status.trim().toUpperCase(),
                    riskLevel.trim().toUpperCase(),
                    pageable);

        } else if (hasStatus) {
            reports = scamReportRepository.findByStatus(
                    status.trim().toUpperCase(),
                    pageable);

        } else if (hasRiskLevel) {
            reports = scamReportRepository.findByRiskLevel(
                    riskLevel.trim().toUpperCase(),
                    pageable);

        } else {
            reports = scamReportRepository.findAll(pageable);
        }

        List<ScamReportResponse> reportResponses = reports.getContent()
                .stream()
                .map(ScamReportResponse::new)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("reports", reportResponses);
        response.put("currentPage", reports.getNumber());
        response.put("pageSize", reports.getSize());
        response.put("totalItems", reports.getTotalElements());
        response.put("totalPages", reports.getTotalPages());
        response.put("hasNext", reports.hasNext());
        response.put("hasPrevious", reports.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<ScamReportResponse> getReportById(
            @PathVariable Long id) {

        ScamReport report = scamReportRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Report not found"));

        return ResponseEntity.ok(new ScamReportResponse(report));
    }

    @PatchMapping("/reports/{id}/status")
    public ResponseEntity<ScamReportResponse> updateReportStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {

        String adminEmail = authentication.getName();

        ScamReportResponse response =
                scamReportService.updateReportStatus(
                        id,
                        status.trim().toUpperCase(),
                        adminEmail);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reports/recalculate-risk")
    public ResponseEntity<Map<String, Object>> recalculateMissingRiskData() {

        int updatedCount = scamReportService.recalculateMissingRiskData();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Missing risk data recalculation completed.");
        response.put("updatedReports", updatedCount);

        return ResponseEntity.ok(response);
    }
}
