
package com.scamshield.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scamshield.entity.ScamStory;

public interface ScamStoryRepository
        extends JpaRepository<ScamStory, Long> {

    // Public stories: approved stories with pagination
    Page<ScamStory>
    findByStatusIgnoreCaseOrderBySubmittedAtDesc(
            String status,
            Pageable pageable);

    // Public stories: approved stories filtered by city
    Page<ScamStory>
    findByStatusIgnoreCaseAndIncidentCityIgnoreCaseOrderBySubmittedAtDesc(
            String status,
            String incidentCity,
            Pageable pageable);

    // Admin stories: all statuses with pagination
    Page<ScamStory> findAllByOrderBySubmittedAtDesc(
            Pageable pageable);

    // Existing non-paginated method, retained for compatibility
    List<ScamStory> findAllByOrderBySubmittedAtDesc();
}
