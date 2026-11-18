
package com.scamshield.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.AdminAuditLogResponse;
import com.scamshield.entity.AdminAuditLog;
import com.scamshield.repository.AdminAuditLogRepository;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditLogController {

    private final AdminAuditLogRepository adminAuditLogRepository;

    public AdminAuditLogController(
            AdminAuditLogRepository adminAuditLogRepository) {
        this.adminAuditLogRepository = adminAuditLogRepository;
    }

    @GetMapping
    public ResponseEntity<List<AdminAuditLogResponse>> getAllAuditLogs() {

        List<AdminAuditLog> logs =
                adminAuditLogRepository.findAllByOrderByActionAtDesc();

        List<AdminAuditLogResponse> response = logs.stream()
                .map(AdminAuditLogResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/{reportId}")
    public ResponseEntity<List<AdminAuditLogResponse>> getAuditLogsByReport(
            @PathVariable Long reportId) {

        List<AdminAuditLog> logs =
                adminAuditLogRepository
                        .findByReportIdOrderByActionAtDesc(reportId);

        List<AdminAuditLogResponse> response = logs.stream()
                .map(AdminAuditLogResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }
}
