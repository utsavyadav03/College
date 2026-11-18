
package com.scamshield.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.CreateScamStoryRequest;
import com.scamshield.dto.ScamStoryResponse;
import com.scamshield.service.ScamStoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stories")
public class ScamStoryController {

    private final ScamStoryService scamStoryService;

    public ScamStoryController(
            ScamStoryService scamStoryService) {

        this.scamStoryService = scamStoryService;
    }

    @PostMapping
    public ResponseEntity<ScamStoryResponse> createStory(
            @Valid @RequestBody CreateScamStoryRequest request,
            Authentication authentication) {

        ScamStoryResponse response =
                scamStoryService.createStory(
                        request,
                        authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ScamStoryResponse>> getPublicStories(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ScamStoryResponse> stories =
                scamStoryService.getPublicStories(
                        city, page, size);

        return ResponseEntity.ok(stories);
    }
}
