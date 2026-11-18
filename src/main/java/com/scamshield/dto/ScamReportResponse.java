
package com.scamshield.dto;

import java.time.LocalDateTime;

import com.scamshield.entity.ScamReport;

public class ScamReportResponse {

    private Long id;
    private String title;
    private String description;
    private String scamType;
    private String status;
    private LocalDateTime reportedAt;

    private Integer riskScore;
    private String riskLevel;
    private String riskReason;

    private String suspiciousPhone;
    private String incidentCity;

    public ScamReportResponse(ScamReport report) {
        this.id = report.getId();
        this.title = report.getTitle();
        this.description = report.getDescription();
        this.scamType = report.getScamType();
        this.status = report.getStatus();
        this.reportedAt = report.getReportedAt();

        this.riskScore = report.getRiskScore();
        this.riskLevel = report.getRiskLevel();
        this.riskReason = report.getRiskReason();

        this.suspiciousPhone = report.getSuspiciousPhone();
        this.incidentCity = report.getIncidentCity();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getScamType() {
        return scamType;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRiskReason() {
        return riskReason;
    }

    public String getSuspiciousPhone() {
        return suspiciousPhone;
    }

    public String getIncidentCity() {
        return incidentCity;
    }
}
