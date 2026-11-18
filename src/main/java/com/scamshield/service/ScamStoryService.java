
package com.scamshield.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.scamshield.dto.CreateScamStoryRequest;
import com.scamshield.dto.ScamStoryResponse;
import com.scamshield.entity.ScamStory;
import com.scamshield.entity.User;
import com.scamshield.repository.ScamStoryRepository;
import com.scamshield.repository.UserRepository;

@Service
public class ScamStoryService {

    private final ScamStoryRepository scamStoryRepository;
    private final UserRepository userRepository;

    public ScamStoryService(
            ScamStoryRepository scamStoryRepository,
            UserRepository userRepository) {

        this.scamStoryRepository = scamStoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ScamStoryResponse createStory(
            CreateScamStoryRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User account not found"));

        ScamStory story = new ScamStory();

        story.setTitle(request.getTitle().trim());
        story.setStory(request.getStory().trim());
        story.setScamType(request.getScamType().trim());
        story.setLesson(request.getLesson().trim());
        story.setReportedBy(user);
        story.setStatus("PENDING");

        if (StringUtils.hasText(request.getIncidentCity())) {
            story.setIncidentCity(
                    request.getIncidentCity().trim());
        }

        story.setFinancialLoss(request.getFinancialLoss());

        ScamStory savedStory = scamStoryRepository.save(story);

        return new ScamStoryResponse(savedStory);
    }

    @Transactional(readOnly = true)
    public Page<ScamStoryResponse> getPublicStories(
            String city,
            int page,
            int size) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<ScamStory> stories;

        if (StringUtils.hasText(city)) {
            stories = scamStoryRepository
                    .findByStatusIgnoreCaseAndIncidentCityIgnoreCaseOrderBySubmittedAtDesc(
                            "APPROVED",
                            city.trim(),
                            pageable);
        } else {
            stories = scamStoryRepository
                    .findByStatusIgnoreCaseOrderBySubmittedAtDesc(
                            "APPROVED",
                            pageable);
        }

        return stories.map(ScamStoryResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<ScamStoryResponse> getAllStoriesForAdmin(
            int page,
            int size) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<ScamStory> stories =
                scamStoryRepository
                        .findAllByOrderBySubmittedAtDesc(pageable);

        return stories.map(ScamStoryResponse::new);
    }

    private void validatePagination(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 50");
        }
    }

    @Transactional
    public ScamStoryResponse updateStoryStatus(
            Long id,
            String status) {

        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException(
                    "Story status is required");
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!normalizedStatus.equals("APPROVED")
                && !normalizedStatus.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Status must be APPROVED or REJECTED");
        }

        ScamStory story = scamStoryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Scam story not found with ID: " + id));

        story.setStatus(normalizedStatus);

        ScamStory updatedStory = scamStoryRepository.save(story);

        return new ScamStoryResponse(updatedStory);
    }
}
