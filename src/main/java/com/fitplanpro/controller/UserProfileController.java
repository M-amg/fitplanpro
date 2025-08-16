package com.fitplanpro.controller;

import com.fitplanpro.dto.common.ApiResponseDto;
import com.fitplanpro.dto.common.ErrorResponseDto;
import com.fitplanpro.dto.profile.ProfileMetricsDto;
import com.fitplanpro.dto.profile.UserProfileCreateDto;
import com.fitplanpro.dto.profile.UserProfileDto;
import com.fitplanpro.dto.profile.UserProfileUpdateDto;
import com.fitplanpro.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for user profile operations
 */
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "User Profiles", description = "User profile management API")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Create a new user profile
     *
     * @param createDto the profile creation data
     * @return the created profile DTO
     */
    @PostMapping
    @Operation(summary = "Create profile", description = "Creates a new profile for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserProfileDto>> createProfile(
            @RequestBody @Valid UserProfileCreateDto createDto) {
        UserProfileDto createdProfile = userProfileService.createProfile(createDto);

        ApiResponseDto<UserProfileDto> response = ApiResponseDto.<UserProfileDto>builder()
                .success(true)
                .message("Profile created successfully")
                .data(createdProfile)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get current user's profile
     *
     * @return the user profile DTO
     */
    @GetMapping("/me")
    @Operation(summary = "Get current profile", description = "Retrieves the profile of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserProfileDto>> getCurrentUserProfile() {
        UserProfileDto profileDto = userProfileService.getCurrentUserProfile();

        ApiResponseDto<UserProfileDto> response = ApiResponseDto.<UserProfileDto>builder()
                .success(true)
                .message("Profile retrieved successfully")
                .data(profileDto)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Update user profile
     *
     * @param updateDto the profile update data
     * @return the updated profile DTO
     */
    @PutMapping("/me")
    @Operation(summary = "Update profile", description = "Updates the profile of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserProfileDto>> updateProfile(
            @RequestBody @Valid UserProfileUpdateDto updateDto) {
        UserProfileDto updatedProfile = userProfileService.updateProfile(updateDto);

        ApiResponseDto<UserProfileDto> response = ApiResponseDto.<UserProfileDto>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(updatedProfile)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get profile by ID
     *
     * @param id the profile ID
     * @return the profile DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get profile by ID", description = "Retrieves a profile by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserProfileDto>> getProfileById(
            @Parameter(description = "Profile ID", required = true) @PathVariable Long id) {
        UserProfileDto profileDto = userProfileService.getProfileById(id);

        ApiResponseDto<UserProfileDto> response = ApiResponseDto.<UserProfileDto>builder()
                .success(true)
                .message("Profile retrieved successfully")
                .data(profileDto)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get user's profile metrics
     *
     * @return the profile metrics DTO
     */
    @GetMapping("/me/metrics")
    @Operation(summary = "Get profile metrics", description = "Retrieves simplified metrics for the authenticated user's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<ProfileMetricsDto>> getProfileMetrics() {
        ProfileMetricsDto metricsDto = userProfileService.getProfileMetrics();

        ApiResponseDto<ProfileMetricsDto> response = ApiResponseDto.<ProfileMetricsDto>builder()
                .success(true)
                .message("Profile metrics retrieved successfully")
                .data(metricsDto)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Find similar profiles
     *
     * @param profileId the profile ID to find similar profiles for
     * @param limit the maximum number of similar profiles to return
     * @return list of similar profile DTOs
     */
    @GetMapping("/{profileId}/similar")
    @Operation(summary = "Find similar profiles", description = "Finds profiles similar to the specified profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Similar profiles found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<UserProfileDto>>> findSimilarProfiles(
            @Parameter(description = "Profile ID", required = true) @PathVariable Long profileId,
            @Parameter(description = "Maximum number of profiles to return") @RequestParam(defaultValue = "5") int limit) {
        List<UserProfileDto> similarProfiles = userProfileService.findSimilarProfiles(profileId, limit);

        ApiResponseDto<List<UserProfileDto>> response = ApiResponseDto.<List<UserProfileDto>>builder()
                .success(true)
                .message("Similar profiles found")
                .data(similarProfiles)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Check if user has a profile
     *
   * @return a ResponseEntity containing true if the user has a profile
     */
    @GetMapping("/check")
    @Operation(summary = "Check profile existence", description = "Checks if the authenticated user has a profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<Boolean>> hasProfile() {
        boolean hasProfile = userProfileService.hasProfile();

        ApiResponseDto<Boolean> response = ApiResponseDto.<Boolean>builder()
                .success(true)
                .message("Profile check completed")
                .data(hasProfile)
                .build();

        return ResponseEntity.ok(response);
    }
}