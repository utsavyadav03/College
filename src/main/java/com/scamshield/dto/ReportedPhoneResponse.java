
package com.scamshield.dto;

public class ReportedPhoneResponse {

    private String phone;
    private long totalReports;
    private long uniqueReporters;
    private long verifiedReports;
    private String riskLabel;

    public ReportedPhoneResponse(
            String phone,
            long totalReports,
            long uniqueReporters,
            long verifiedReports,
            String riskLabel) {

        this.phone = phone;
        this.totalReports = totalReports;
        this.uniqueReporters = uniqueReporters;
        this.verifiedReports = verifiedReports;
        this.riskLabel = riskLabel;
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

    public String getRiskLabel() {
        return riskLabel;
    }
}
