
package com.scamshield.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateScamStoryRequest {

    @NotBlank(message = "Story title is required")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Story details are required")
    @Size(max = 5000)
    private String story;

    @NotBlank(message = "Scam type is required")
    @Size(max = 100)
    private String scamType;

    @Size(max = 100)
    private String incidentCity;

    @NotBlank(message = "Lesson is required")
    @Size(max = 2000)
    private String lesson;

    @DecimalMin(value = "0.00",
            message = "Financial loss cannot be negative")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal financialLoss;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getScamType() {
        return scamType;
    }

    public void setScamType(String scamType) {
        this.scamType = scamType;
    }

    public String getIncidentCity() {
        return incidentCity;
    }

    public void setIncidentCity(String incidentCity) {
        this.incidentCity = incidentCity;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public BigDecimal getFinancialLoss() {
        return financialLoss;
    }

    public void setFinancialLoss(BigDecimal financialLoss) {
        this.financialLoss = financialLoss;
    }
}
