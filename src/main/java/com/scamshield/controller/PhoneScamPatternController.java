package com.scamshield.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.PhoneScamPatternResponse;
import com.scamshield.service.PhoneScamPatternService;

@RestController
@RequestMapping("/api/numbers")
public class PhoneScamPatternController {

    private final PhoneScamPatternService phoneScamPatternService;

    public PhoneScamPatternController(
            PhoneScamPatternService phoneScamPatternService) {

        this.phoneScamPatternService = phoneScamPatternService;
    }

    @GetMapping("/{phone}/analysis")
    public ResponseEntity<PhoneScamPatternResponse> analyzePhone(
            @PathVariable String phone) {

        PhoneScamPatternResponse response =
                phoneScamPatternService.analyzePhone(phone);

        return ResponseEntity.ok(response);
    }
}