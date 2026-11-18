
package com.scamshield.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.scamshield.dto.LoginRequest;
import com.scamshield.dto.RegisterRequest;
import com.scamshield.entity.User;
import com.scamshield.repository.UserRepository;
import com.scamshield.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginRateLimiter loginRateLimiter;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            LoginRateLimiter loginRateLimiter) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginRateLimiter = loginRateLimiter;
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("RESIDENT");

        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request, String ipAddress) {

        if (loginRateLimiter.isBlocked(ipAddress)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failed login attempts. Try again after 15 minutes.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            loginRateLimiter.recordFailedAttempt(ipAddress);

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            loginRateLimiter.recordFailedAttempt(ipAddress);

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        loginRateLimiter.resetAttempts(ipAddress);

        return jwtService.generateToken(user.getEmail());
    }
}
