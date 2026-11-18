
package com.scamshield.dto;

import java.time.LocalDateTime;

import com.scamshield.entity.AdminAuditLog;

public class AdminAuditLogResponse {

    private Long id;
    private Long reportId;
    private String adminEmail;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime actionAt;

    public AdminAuditLogResponse(AdminAuditLog auditLog) {
        this.id = auditLog.getId();
        this.reportId = auditLog.getReportId();
        this.adminEmail = auditLog.getAdminEmail();
        this.oldStatus = auditLog.getOldStatus();
        this.newStatus = auditLog.getNewStatus();
        this.actionAt = auditLog.getActionAt();
    }

    public Long getId() {
        return id;
    }

    public Long getReportId() {
        return reportId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getActionAt() {
        return actionAt;
    }
}
