package com.fitplanpro.controller;

import com.fitplanpro.dto.achievement.*;
import com.fitplanpro.dto.common.ApiResponseDto;
import com.fitplanpro.dto.common.ErrorResponseDto;
import com.fitplanpro.enums.AchievementCategory;
import com.fitplanpro.service.AchievementService;
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
 * Controller for achievement operations
 */
@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
@Tag(name = "Achievements", description = "Achievement management API")
@SecurityRequirement(name = "bearerAuth")
public class AchievementController {

    private final AchievementService achievementService;

    /**
     * Get all achievement types
     *
     * @return list of achievement type DTOs
     */
    @GetMapping("/types")
    @Operation(summary = "Get all achievement types", description = "Retrieves all available achievement types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Achievement types retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<AchievementTypeDto>>> getAllAchievementTypes() {
        List<AchievementTypeDto> achievementTypes = achievementService.getAllAchievementTypes();

        ApiResponseDto<List<AchievementTypeDto>> response = ApiResponseDto.<List<AchievementTypeDto>>builder()
                .success(true)
                .message("Achievement types retrieved successfully")
                .data(achievementTypes)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get achievement types by category
     *
     * @param category the achievement category
     * @return list of achievement type DTOs
     */
    @GetMapping("/types/category/{category}")
    @Operation(summary = "Get achievement types by category", description = "Retrieves achievement types for a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Achievement types retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<AchievementTypeDto>>> getAchievementTypesByCategory(
            @Parameter(description = "Achievement category", required = true)
            @PathVariable AchievementCategory category) {
        List<AchievementTypeDto> achievementTypes = achievementService.getAchievementTypesByCategory(category);

        ApiResponseDto<List<AchievementTypeDto>> response = ApiResponseDto.<List<AchievementTypeDto>>builder()
                .success(true)
                .message("Achievement types retrieved successfully")
                .data(achievementTypes)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get user's achievements
     *
     * @return list of user achievement DTOs
     */
    @GetMapping
    @Operation(summary = "Get user achievements", description = "Retrieves achievements for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Achievements retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<UserAchievementDto>>> getUserAchievements() {
        List<UserAchievementDto> userAchievements = achievementService.getUserAchievements();

        ApiResponseDto<List<UserAchievementDto>> response = ApiResponseDto.<List<UserAchievementDto>>builder()
                .success(true)
                .message("Achievements retrieved successfully")
                .data(userAchievements)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get user's achievements by category
     *
     * @param category the achievement category
     * @return list of user achievement DTOs
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Get user achievements by category", description = "Retrieves user achievements for a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Achievements retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<UserAchievementDto>>> getUserAchievementsByCategory(
            @Parameter(description = "Achievement category", required = true)
            @PathVariable AchievementCategory category) {
        List<UserAchievementDto> userAchievements = achievementService.getUserAchievementsByCategory(category);

        ApiResponseDto<List<UserAchievementDto>> response = ApiResponseDto.<List<UserAchievementDto>>builder()
                .success(true)
                .message("Achievements retrieved successfully")
                .data(userAchievements)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get user's achievement summary
     *
     * @return the user achievement summary DTO
     */
    @GetMapping("/summary")
    @Operation(summary = "Get achievement summary", description = "Retrieves achievement summary for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Achievement summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserAchievementSummaryDto>> getUserAchievementSummary() {
        UserAchievementSummaryDto summary = achievementService.getUserAchievementSummary();

        ApiResponseDto<UserAchievementSummaryDto> response = ApiResponseDto.<UserAchievementSummaryDto>builder()
                .success(true)
                .message("Achievement summary retrieved successfully")
                .data(summary)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Grant an achievement to a user
     *
     * @param grantDto the grant achievement DTO
     * @return the user achievement DTO
     */
    @PostMapping("/grant")
    @Operation(summary = "Grant achievement", description = "Grants an achievement to a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Achievement granted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User or achievement not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserAchievementDto>> grantAchievement(
            @RequestBody @Valid GrantAchievementDto grantDto) {
        UserAchievementDto userAchievement = achievementService.grantAchievement(grantDto);

        ApiResponseDto<UserAchievementDto> response = ApiResponseDto.<UserAchievementDto>builder()
                .success(true)
                .message("Achievement granted successfully")
                .data(userAchievement)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Update achievement progress
     *
     * @param updateDto the update progress DTO
     * @return the updated user achievement DTO
     */
    @PutMapping("/progress")
    @Operation(summary = "Update achievement progress", description = "Updates progress for a user achievement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Achievement progress updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Achievement not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserAchievementDto>> updateAchievementProgress(
            @RequestBody @Valid UpdateAchievementProgressDto updateDto) {
        UserAchievementDto updatedAchievement = achievementService.updateAchievementProgress(updateDto);

        ApiResponseDto<UserAchievementDto> response = ApiResponseDto.<UserAchievementDto>builder()
                .success(true)
                .message("Achievement progress updated successfully")
                .data(updatedAchievement)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Create a new achievement type (admin only)
     *
     * @param createDto the creation achievement type DTO
     * @return the created achievement type DTO
     */
    @PostMapping("/types")
    @Operation(summary = "Create achievement type", description = "Creates a new achievement type (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Achievement type created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<AchievementTypeDto>> createAchievementType(
            @RequestBody @Valid AchievementTypeCreateDto createDto) {
        AchievementTypeDto createdType = achievementService.createAchievementType(createDto);

        ApiResponseDto<AchievementTypeDto> response = ApiResponseDto.<AchievementTypeDto>builder()
                .success(true)
                .message("Achievement type created successfully")
                .data(createdType)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}