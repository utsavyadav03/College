
package com.scamshield.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.CityScamAlertResponse;
import com.scamshield.service.CityScamAlertService;

@RestController
@RequestMapping("/api/alerts")
public class CityScamAlertController {

    private final CityScamAlertService cityScamAlertService;

    public CityScamAlertController(
            CityScamAlertService cityScamAlertService) {

        this.cityScamAlertService = cityScamAlertService;
    }

    @GetMapping
    public ResponseEntity<List<CityScamAlertResponse>> getAlerts(
            @RequestParam String city) {

        List<CityScamAlertResponse> alerts =
                cityScamAlertService.getAlertsByCity(city);

        return ResponseEntity.ok(alerts);
    }
}
