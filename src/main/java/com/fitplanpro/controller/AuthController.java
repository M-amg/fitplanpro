package com.fitplanpro.controller;

import com.fitplanpro.dto.common.ApiResponseDto;
import com.fitplanpro.dto.user.AuthResponseDto;
import com.fitplanpro.dto.user.UserLoginDto;
import com.fitplanpro.dto.user.UserRegistrationDto;
import com.fitplanpro.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     *
     * @param registrationDto the registration data
     * @return authentication response with token and user data
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication token")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        AuthResponseDto authResponse = authService.register(registrationDto);

        ApiResponseDto<AuthResponseDto> response = ApiResponseDto.<AuthResponseDto>builder()
                .success(true)
                .message("User registered successfully")
                .data(authResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login a user
     *
     * @param loginDto the login data
     * @return authentication response with token and user data
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns authentication token")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> login(@Valid @RequestBody UserLoginDto loginDto) {
        AuthResponseDto authResponse = authService.login(loginDto);

        ApiResponseDto<AuthResponseDto> response = ApiResponseDto.<AuthResponseDto>builder()
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Check authentication status
     *
     * @return success response if authenticated
     */
    @GetMapping("/check")
    @Operation(summary = "Check authentication", description = "Verifies if the user is authenticated")
    public ResponseEntity<ApiResponseDto<Void>> checkAuth() {
        // This endpoint is protected by JWT authentication
        // If we get here, the user is authenticated

        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(true)
                .message("Authenticated")
                .build();

        return ResponseEntity.ok(response);
    }
}