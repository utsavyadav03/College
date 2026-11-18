
package com.scamshield.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scamshield.entity.ScamReport;

class FraudRiskAnalysisServiceTests {

    FraudRiskAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new FraudRiskAnalysisService();
    }

    @Test
    void noRiskIndicatorsShouldReturnLowRisk() {

        ScamReport report = new ScamReport();
        report.setTitle("General enquiry");
        report.setDescription("I received a normal message.");

        service.analyzeReport(report);

        assertEquals(0, report.getRiskScore());
        assertEquals("LOW", report.getRiskLevel());
        assertEquals(
                "No configured risk indicators detected.",
                report.getRiskReason());
    }

    @Test
    void upiKeywordShouldAddTwentyPoints() {

        ScamReport report = new ScamReport();
        report.setTitle("UPI report");
        report.setDescription("A transaction looked unusual.");

        service.analyzeReport(report);

        assertEquals(20, report.getRiskScore());
        assertEquals("LOW", report.getRiskLevel());
        assertTrue(report.getRiskReason()
                .contains("UPI-related report"));
    }

    @Test
    void otpKeywordShouldReturnMediumRisk() {

        ScamReport report = new ScamReport();
        report.setTitle("OTP warning");
        report.setDescription("Someone asked me for my OTP.");

        service.analyzeReport(report);

        assertEquals(30, report.getRiskScore());
        assertEquals("MEDIUM", report.getRiskLevel());
        assertTrue(report.getRiskReason()
                .contains("OTP-related warning"));
    }

    @Test
    void passwordAndUrgencyShouldAddFortyPoints() {

        ScamReport report = new ScamReport();
        report.setTitle("Urgent password request");
        report.setDescription(
                "An unknown message asked for my password immediately.");

        service.analyzeReport(report);

        assertEquals(40, report.getRiskScore());
        assertEquals("MEDIUM", report.getRiskLevel());
        assertTrue(report.getRiskReason()
                .contains("Password-related warning"));
        assertTrue(report.getRiskReason()
                .contains("Urgency or pressure mentioned"));
    }

    @Test
    void multipleIndicatorsShouldReturnHighRisk() {

        ScamReport report = new ScamReport();
        report.setTitle("Urgent UPI OTP password warning");
        report.setDescription(
                "A suspicious person asked for my OTP and password immediately.");

        service.analyzeReport(report);

        assertEquals(100, report.getRiskScore());
        assertEquals("HIGH", report.getRiskLevel());
        assertTrue(report.getRiskReason()
                .contains("UPI-related report"));
        assertTrue(report.getRiskReason()
                .contains("OTP-related warning"));
        assertTrue(report.getRiskReason()
                .contains("Password-related warning"));
        assertTrue(report.getRiskReason()
                .contains("Suspicious or unknown contact mentioned"));
    }

    @Test
    void nullTitleAndDescriptionShouldNotCrash() {

        ScamReport report = new ScamReport();
        report.setTitle(null);
        report.setDescription(null);

        service.analyzeReport(report);

        assertEquals(0, report.getRiskScore());
        assertEquals("LOW", report.getRiskLevel());
        assertEquals(
                "No configured risk indicators detected.",
                report.getRiskReason());
    }

    @Test
    void urgentKeywordShouldAddFifteenPoints() {

        ScamReport report = new ScamReport();
        report.setTitle("Urgent request");
        report.setDescription("Please respond immediately.");

        service.analyzeReport(report);

        assertEquals(15, report.getRiskScore());
        assertEquals("LOW", report.getRiskLevel());
    }

    @Test
    void suspiciousKeywordShouldAddTenPoints() {

        ScamReport report = new ScamReport();
        report.setTitle("Suspicious contact");
        report.setDescription("The message looked suspicious.");

        service.analyzeReport(report);

        assertEquals(10, report.getRiskScore());
        assertEquals("LOW", report.getRiskLevel());
    }

    @Test
    void upiPinRequestShouldBeHighRisk() {

        ScamReport report = new ScamReport();
        report.setTitle("Suspicious UPI call");
        report.setDescription(
                "An unknown caller asked me for my UPI PIN "
                + "and threatened to block my bank account immediately.");

        service.analyzeReport(report);

        assertEquals(80, report.getRiskScore());
        assertEquals("HIGH", report.getRiskLevel());
        assertTrue(report.getRiskReason()
                .contains("PIN requested"));
    }

    @Test
    void atmPinRequestShouldAddCriticalIndicator() {

        ScamReport report = new ScamReport();
        report.setTitle("ATM PIN request");
        report.setDescription("Someone asked me to share my ATM PIN.");

        service.analyzeReport(report);

        assertEquals(35, report.getRiskScore());
        assertEquals("MEDIUM", report.getRiskLevel());
        assertTrue(report.getRiskReason()
                .contains("PIN requested"));
    }
}
