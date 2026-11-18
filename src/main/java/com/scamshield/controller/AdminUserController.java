package com.scamshield.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scamshield.dto.AdminUserResponse;
import com.scamshield.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all registered users
    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {

        List<AdminUserResponse> users = userRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(AdminUserResponse::new)
                .toList();

        return ResponseEntity.ok(users);
    }

    // Get total registered users count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalUsersCount() {

        long totalUsers = userRepository.count();

        return ResponseEntity.ok(
                Map.of("totalUsers", totalUsers)
        );
    }
}