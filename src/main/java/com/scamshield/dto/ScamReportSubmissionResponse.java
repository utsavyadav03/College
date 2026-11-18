
package com.scamshield.dto;

public class ScamReportSubmissionResponse {

    private ScamReportResponse report;
    private long previousReports;
    private long uniqueReporters;
    private long verifiedReports;

    public ScamReportSubmissionResponse(
            ScamReportResponse report,
            long previousReports,
            long uniqueReporters,
            long verifiedReports) {

        this.report = report;
        this.previousReports = previousReports;
        this.uniqueReporters = uniqueReporters;
        this.verifiedReports = verifiedReports;
    }

    public ScamReportResponse getReport() {
        return report;
    }

    public long getPreviousReports() {
        return previousReports;
    }

    public long getUniqueReporters() {
        return uniqueReporters;
    }

    public long getVerifiedReports() {
        return verifiedReports;
    }
}
