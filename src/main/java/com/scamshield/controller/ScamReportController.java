package com.scamshield.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.CreateScamReportRequest;
import com.scamshield.dto.ScamReportResponse;
import com.scamshield.dto.ScamReportSubmissionResponse;
import com.scamshield.service.ScamReportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ScamReportController {

    private final ScamReportService scamReportService;

    public ScamReportController(ScamReportService scamReportService) {
        this.scamReportService = scamReportService;
    }

    @PostMapping
    public ResponseEntity<ScamReportSubmissionResponse> createReport(
            @Valid @RequestBody CreateScamReportRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        ScamReportSubmissionResponse response =
                scamReportService.createReport(request, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ScamReportResponse>> getMyReports(
            Authentication authentication) {

        String email = authentication.getName();

        List<ScamReportResponse> reports =
                scamReportService.getMyReports(email);

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScamReportResponse> getMyReportById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        ScamReportResponse response =
                scamReportService.getMyReportById(id, email);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScamReportResponse> updateMyReport(
            @PathVariable Long id,
            @Valid @RequestBody CreateScamReportRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        ScamReportResponse response =
                scamReportService.updateMyReport(id, request, email);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMyReport(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        scamReportService.deleteMyReport(id, email);

        return ResponseEntity.ok("Scam report deleted successfully");
    }
}