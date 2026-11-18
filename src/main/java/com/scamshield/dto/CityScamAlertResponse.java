
package com.scamshield.dto;

public class CityScamAlertResponse {

    private String city;
    private String scamType;
    private long verifiedReports;
    private String message;

    public CityScamAlertResponse(
            String city,
            String scamType,
            long verifiedReports,
            String message) {

        this.city = city;
        this.scamType = scamType;
        this.verifiedReports = verifiedReports;
        this.message = message;
    }

    public String getCity() {
        return city;
    }

    public String getScamType() {
        return scamType;
    }

    public long getVerifiedReports() {
        return verifiedReports;
    }

    public String getMessage() {
        return message;
    }
}
