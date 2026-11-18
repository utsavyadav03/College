
package com.scamshield.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.ScamStoryResponse;
import com.scamshield.service.ScamStoryService;

@RestController
@RequestMapping("/api/admin/stories")
public class AdminScamStoryController {

    private final ScamStoryService scamStoryService;

    public AdminScamStoryController(
            ScamStoryService scamStoryService) {

        this.scamStoryService = scamStoryService;
    }

    @GetMapping
    public ResponseEntity<Page<ScamStoryResponse>> getAllStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                scamStoryService.getAllStoriesForAdmin(page, size));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ScamStoryResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        ScamStoryResponse response =
                scamStoryService.updateStoryStatus(id, status);

        return ResponseEntity.ok(response);
    }
}
