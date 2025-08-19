package com.fitplanpro.controller;

import com.fitplanpro.dto.common.ApiResponseDto;
import com.fitplanpro.dto.common.ErrorResponseDto;
import com.fitplanpro.dto.plan.*;
import com.fitplanpro.service.PlanService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for plan operations
 */
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
@Tag(name = "Plans", description = "Plan management API")
@SecurityRequirement(name = "bearerAuth")
public class PlanController {

    private final PlanService planService;

    /**
     * Generate a new plan
     *
     * @param requestDto the plan generation request
     * @return the generation result DTO
     */
    @PostMapping("/generate")
    @Operation(summary = "Generate plan", description = "Generates a new plan based on the user's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan generated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<PlanGenerationResultDto>> generatePlan(
            @RequestBody @Valid PlanGenerationRequestDto requestDto) {
        PlanGenerationResultDto result = planService.generatePlan(requestDto);

        ApiResponseDto<PlanGenerationResultDto> response = ApiResponseDto.<PlanGenerationResultDto>builder()
                .success(true)
                .message(result.getFromCache() ? "Plan retrieved from cache" : "Plan generated successfully")
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get plan summary
     *
     * @param id the plan ID
     * @return the plan summary DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get plan summary", description = "Retrieves a summary of the specified plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Plan not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<PlanSummaryDto>> getPlanSummary(
            @Parameter(description = "Plan ID", required = true) @PathVariable Long id) {
        PlanSummaryDto planSummary = planService.getPlanSummary(id);

        ApiResponseDto<PlanSummaryDto> response = ApiResponseDto.<PlanSummaryDto>builder()
                .success(true)
                .message("Plan found")
                .data(planSummary)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get meal plan
     *
     * @param id the plan ID
     * @return the meal plan DTO
     */
    @GetMapping("/{id}/meal")
    @Operation(summary = "Get meal plan", description = "Retrieves the meal plan with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meal plan found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Meal plan not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<MealPlanDto>> getMealPlan(
            @Parameter(description = "Plan ID", required = true) @PathVariable Long id) {
        MealPlanDto mealPlan = planService.getMealPlan(id);

        ApiResponseDto<MealPlanDto> response = ApiResponseDto.<MealPlanDto>builder()
                .success(true)
                .message("Meal plan found")
                .data(mealPlan)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get workout plan
     *
     * @param id the plan ID
     * @return the workout plan DTO
     */
    @GetMapping("/{id}/workout")
    @Operation(summary = "Get workout plan", description = "Retrieves the workout plan with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout plan found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Workout plan not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<WorkoutPlanDto>> getWorkoutPlan(
            @Parameter(description = "Plan ID", required = true) @PathVariable Long id) {
        WorkoutPlanDto workoutPlan = planService.getWorkoutPlan(id);

        ApiResponseDto<WorkoutPlanDto> response = ApiResponseDto.<WorkoutPlanDto>builder()
                .success(true)
                .message("Workout plan found")
                .data(workoutPlan)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get combined plan
     *
     * @param id the plan ID
     * @return the combined plan DTO
     */
    @GetMapping("/{id}/combined")
    @Operation(summary = "Get combined plan", description = "Retrieves the combined plan with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Combined plan found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Combined plan not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<CombinedPlanDto>> getCombinedPlan(
            @Parameter(description = "Plan ID", required = true) @PathVariable Long id) {
        CombinedPlanDto combinedPlan = planService.getCombinedPlan(id);

        ApiResponseDto<CombinedPlanDto> response = ApiResponseDto.<CombinedPlanDto>builder()
                .success(true)
                .message("Combined plan found")
                .data(combinedPlan)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get user's active plans
     *
     * @return list of plan summary DTOs
     */
    @GetMapping("/active")
    @Operation(summary = "Get active plans", description = "Retrieves the active plans for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active plans retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<PlanSummaryDto>>> getUserActivePlans() {
        List<PlanSummaryDto> activePlans = planService.getUserActivePlans();

        ApiResponseDto<List<PlanSummaryDto>> response = ApiResponseDto.<List<PlanSummaryDto>>builder()
                .success(true)
                .message("Active plans retrieved")
                .data(activePlans)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Delete plan
     *
     * @param id the plan ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete plan", description = "Deletes the plan with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Plan not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<Void>> deletePlan(
            @Parameter(description = "Plan ID", required = true) @PathVariable Long id) {
        boolean success = planService.deletePlan(id);

        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(success)
                .message("Plan deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}