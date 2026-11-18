
package com.scamshield.service;

import org.springframework.stereotype.Service;

import com.scamshield.entity.ScamReport;

@Service
public class FraudRiskAnalysisService {

    public void analyzeReport(ScamReport report) {

        int score = 0;
        StringBuilder reasons = new StringBuilder();

        String text = "";

        if (report.getTitle() != null) {
            text += report.getTitle().toLowerCase() + " ";
        }

        if (report.getDescription() != null) {
            text += report.getDescription().toLowerCase();
        }

        // Critical indicator: requesting a PIN
        if (text.contains("upi pin")
                || text.contains("atm pin")
                || text.contains("pin number")) {

            score += 35;
            reasons.append("PIN requested; ");
        }

        // OTP-related indicator
        if (text.contains("otp")) {
            score += 30;
            reasons.append("OTP-related warning; ");
        }

        // UPI-related indicator
        if (text.contains("upi")) {
            score += 20;
            reasons.append("UPI-related report; ");
        }

        // Password-related indicator
        if (text.contains("password")) {
            score += 25;
            reasons.append("Password-related warning; ");
        }

        // Urgency or pressure
        if (text.contains("urgent")
                || text.contains("immediately")) {

            score += 15;
            reasons.append("Urgency or pressure mentioned; ");
        }

        // Suspicious or unknown contact
        if (text.contains("unknown person")
                || text.contains("suspicious")) {

            score += 10;
            reasons.append(
                    "Suspicious or unknown contact mentioned; ");
        }

        // Keep score within 0-100
        if (score > 100) {
            score = 100;
        }

        String riskLevel;

        if (score >= 60) {
            riskLevel = "HIGH";
        } else if (score >= 30) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }

        if (reasons.length() == 0) {
            reasons.append(
                    "No configured risk indicators detected.");
        }

        report.setRiskScore(score);
        report.setRiskLevel(riskLevel);
        report.setRiskReason(reasons.toString());
    }
}
