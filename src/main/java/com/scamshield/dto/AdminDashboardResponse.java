
package com.scamshield.dto;

public class AdminDashboardResponse {

    private long totalReports;
    private long pendingReports;
    private long verifiedReports;
    private long rejectedReports;
    private long deletedReports;

    private long highRiskReports;
    private long mediumRiskReports;
    private long lowRiskReports;

    public AdminDashboardResponse() {
    }

    public AdminDashboardResponse(
            long totalReports,
            long pendingReports,
            long verifiedReports,
            long rejectedReports,
            long deletedReports,
            long highRiskReports,
            long mediumRiskReports,
            long lowRiskReports) {

        this.totalReports = totalReports;
        this.pendingReports = pendingReports;
        this.verifiedReports = verifiedReports;
        this.rejectedReports = rejectedReports;
        this.deletedReports = deletedReports;
        this.highRiskReports = highRiskReports;
        this.mediumRiskReports = mediumRiskReports;
        this.lowRiskReports = lowRiskReports;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(long totalReports) {
        this.totalReports = totalReports;
    }

    public long getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(long pendingReports) {
        this.pendingReports = pendingReports;
    }

    public long getVerifiedReports() {
        return verifiedReports;
    }

    public void setVerifiedReports(long verifiedReports) {
        this.verifiedReports = verifiedReports;
    }

    public long getRejectedReports() {
        return rejectedReports;
    }

    public void setRejectedReports(long rejectedReports) {
        this.rejectedReports = rejectedReports;
    }

    public long getDeletedReports() {
        return deletedReports;
    }

    public void setDeletedReports(long deletedReports) {
        this.deletedReports = deletedReports;
    }

    public long getHighRiskReports() {
        return highRiskReports;
    }

    public void setHighRiskReports(long highRiskReports) {
        this.highRiskReports = highRiskReports;
    }

    public long getMediumRiskReports() {
        return mediumRiskReports;
    }

    public void setMediumRiskReports(long mediumRiskReports) {
        this.mediumRiskReports = mediumRiskReports;
    }

    public long getLowRiskReports() {
        return lowRiskReports;
    }

    public void setLowRiskReports(long lowRiskReports) {
        this.lowRiskReports = lowRiskReports;
    }
}
