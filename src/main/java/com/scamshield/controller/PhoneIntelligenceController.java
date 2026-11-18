
package com.scamshield.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.PhoneNumberCheckResponse;
import com.scamshield.service.PhoneIntelligenceService;

@RestController
@RequestMapping("/api/numbers")
public class PhoneIntelligenceController {

    private final PhoneIntelligenceService phoneIntelligenceService;

    public PhoneIntelligenceController(
            PhoneIntelligenceService phoneIntelligenceService) {

        this.phoneIntelligenceService = phoneIntelligenceService;
    }

    @GetMapping("/check")
    public ResponseEntity<PhoneNumberCheckResponse> checkPhoneNumber(
            @RequestParam String phone) {

        PhoneNumberCheckResponse response =
                phoneIntelligenceService.checkPhoneNumber(phone);

        return ResponseEntity.ok(response);
    }
}
