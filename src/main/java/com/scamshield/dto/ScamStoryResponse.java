
package com.scamshield.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.scamshield.entity.ScamStory;

public class ScamStoryResponse {

    private Long id;
    private String title;
    private String story;
    private String scamType;
    private String incidentCity;
    private String lesson;
    private BigDecimal financialLoss;
    private String status;
    private LocalDateTime submittedAt;

    public ScamStoryResponse(ScamStory story) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.story = story.getStory();
        this.scamType = story.getScamType();
        this.incidentCity = story.getIncidentCity();
        this.lesson = story.getLesson();
        this.financialLoss = story.getFinancialLoss();
        this.status = story.getStatus();
        this.submittedAt = story.getSubmittedAt();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStory() {
        return story;
    }

    public String getScamType() {
        return scamType;
    }

    public String getIncidentCity() {
        return incidentCity;
    }

    public String getLesson() {
        return lesson;
    }

    public BigDecimal getFinancialLoss() {
        return financialLoss;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
