package com.scamshield.dto;

import java.util.Map;

public class PhoneScamPatternResponse {

    private String phone;
    private long totalReports;
    private long uniqueReporters;
    private long verifiedReports;
    private long pendingReports;

    private Map<String, Long> scamTypeCounts;
    private Map<String, Long> cityCounts;
    private Map<String, Long> repeatedPatterns;

    private String message;

    public PhoneScamPatternResponse(
            String phone,
            long totalReports,
            long uniqueReporters,
            long verifiedReports,
            long pendingReports,
            Map<String, Long> scamTypeCounts,
            Map<String, Long> cityCounts,
            Map<String, Long> repeatedPatterns,
            String message) {

        this.phone = phone;
        this.totalReports = totalReports;
        this.uniqueReporters = uniqueReporters;
        this.verifiedReports = verifiedReports;
        this.pendingReports = pendingReports;
        this.scamTypeCounts = scamTypeCounts;
        this.cityCounts = cityCounts;
        this.repeatedPatterns = repeatedPatterns;
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

    public Map<String, Long> getScamTypeCounts() {
        return scamTypeCounts;
    }

    public Map<String, Long> getCityCounts() {
        return cityCounts;
    }

    public Map<String, Long> getRepeatedPatterns() {
        return repeatedPatterns;
    }

    public String getMessage() {
        return message;
    }
}