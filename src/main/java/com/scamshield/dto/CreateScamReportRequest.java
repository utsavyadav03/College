
package com.scamshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateScamReportRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotBlank(message = "Scam type is required")
    @Pattern(
        regexp = "UPI_FRAUD|SMS_FRAUD|PHISHING|JOB_FRAUD|IDENTITY_THEFT|OTHER",
        message = "Invalid scam type. Allowed values: UPI_FRAUD, SMS_FRAUD, PHISHING, JOB_FRAUD, IDENTITY_THEFT, OTHER"
    )
    private String scamType;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String suspiciousPhone;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String incidentCity;

    public CreateScamReportRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScamType() {
        return scamType;
    }

    public void setScamType(String scamType) {
        this.scamType = scamType;
    }

    public String getSuspiciousPhone() {
        return suspiciousPhone;
    }

    public void setSuspiciousPhone(String suspiciousPhone) {
        this.suspiciousPhone = suspiciousPhone;
    }

    public String getIncidentCity() {
        return incidentCity;
    }

    public void setIncidentCity(String incidentCity) {
        this.incidentCity = incidentCity;
    }
}
