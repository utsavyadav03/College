
package com.scamshield.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.ReportedPhoneResponse;
import com.scamshield.service.ReportedPhoneService;

@RestController
@RequestMapping("/api/numbers")
public class ReportedPhoneController {

    private final ReportedPhoneService reportedPhoneService;

    public ReportedPhoneController(
            ReportedPhoneService reportedPhoneService) {

        this.reportedPhoneService = reportedPhoneService;
    }

    @GetMapping("/reported")
    public ResponseEntity<List<ReportedPhoneResponse>> getReportedPhones() {

        List<ReportedPhoneResponse> phones =
                reportedPhoneService.getReportedPhones();

        return ResponseEntity.ok(phones);
    }
}
