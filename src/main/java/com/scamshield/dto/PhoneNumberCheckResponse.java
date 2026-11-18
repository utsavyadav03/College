package com.scamshield.dto;

public class PhoneNumberCheckResponse {

    private String phone;
    private long totalReports;
    private long uniqueReporters;
    private long verifiedReports;
    private long pendingReports;
    private int highestRiskScore;
    private String highestRiskLevel;
    private String riskAssessment;
    private String riskReason;
    private String message;

    public PhoneNumberCheckResponse(
            String phone,
            long totalReports,
            long uniqueReporters,
            long verifiedReports,
            long pendingReports,
            int highestRiskScore,
            String highestRiskLevel,
            String riskAssessment,
            String riskReason,
            String message) {

        this.phone = phone;
        this.totalReports = totalReports;
        this.uniqueReporters = uniqueReporters;
        this.verifiedReports = verifiedReports;
        this.pendingReports = pendingReports;
        this.highestRiskScore = highestRiskScore;
        this.highestRiskLevel = highestRiskLevel;
        this.riskAssessment = riskAssessment;
        this.riskReason = riskReason;
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public long getUniqueReporters() {
        return uniqueReporters;
    }

    public long getVerifiedReports() {
        return verifiedReports;
    }

    public long getPendingReports() {
        return pendingReports;
    }

    public int getHighestRiskScore() {
        return highestRiskScore;
    }

    public String getHighestRiskLevel() {
        return highestRiskLevel;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public String getRiskReason() {
        return riskReason;
    }

    public String getMessage() {
        return message;
    }
}