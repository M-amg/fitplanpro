package com.fitplanpro.controller;

import com.fitplanpro.dto.common.ApiResponseDto;
import com.fitplanpro.dto.common.ErrorResponseDto;
import com.fitplanpro.dto.common.PageDto;
import com.fitplanpro.dto.fooddatabase.*;
import com.fitplanpro.dto.plan.ExerciseDto;
import com.fitplanpro.dto.profile.ProfileMetricsDto;
import com.fitplanpro.enums.ExerciseDifficulty;
import com.fitplanpro.enums.MuscleGroup;
import com.fitplanpro.service.FoodExerciseDatabaseService;
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
 * Controller for food and exercise database operations
 */
@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
@Tag(name = "Database", description = "Food and exercise database API")
@SecurityRequirement(name = "bearerAuth")
public class FoodExerciseDatabaseController {

    private final FoodExerciseDatabaseService foodExerciseDatabaseService;
    private final UserProfileService userProfileService;

    /**
     * Search foods by name
     *
     * @param query the search query
     * @param cultureRegion the culture/region filter (optional)
     * @param page the page number
     * @param size the page size
     * @return page of food search results
     */
    @GetMapping("/foods/search")
    @Operation(summary = "Search foods", description = "Searches for foods by name and culture/region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<PageDto<FoodSearchResultDto>>> searchFoods(
            @Parameter(description = "Search query", required = true) @RequestParam String query,
            @Parameter(description = "Culture/region filter") @RequestParam(required = false) String cultureRegion,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        PageDto<FoodSearchResultDto> results = foodExerciseDatabaseService.searchFoods(query, cultureRegion, page, size);

        ApiResponseDto<PageDto<FoodSearchResultDto>> response = ApiResponseDto.<PageDto<FoodSearchResultDto>>builder()
                .success(true)
                .message("Foods found")
                .data(results)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get food details by ID
     *
     * @param id the food ID
     * @return the food database DTO
     */
    @GetMapping("/foods/{id}")
    @Operation(summary = "Get food details", description = "Retrieves details for a specific food")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Food not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<FoodDatabaseDto>> getFoodById(
            @Parameter(description = "Food ID", required = true) @PathVariable Long id) {
        FoodDatabaseDto food = foodExerciseDatabaseService.getFoodById(id);

        ApiResponseDto<FoodDatabaseDto> response = ApiResponseDto.<FoodDatabaseDto>builder()
                .success(true)
                .message("Food retrieved successfully")
                .data(food)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Create a new food
     *
     * @param createDto the food creation DTO
     * @return the created food database DTO
     */
    @PostMapping("/foods")
    @Operation(summary = "Create food", description = "Creates a new food in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Food created",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<FoodDatabaseDto>> createFood(
            @Parameter(description = "Food data", required = true) @Valid @RequestBody FoodDatabaseCreateDto createDto) {
        FoodDatabaseDto createdFood = foodExerciseDatabaseService.createFood(createDto);

        ApiResponseDto<FoodDatabaseDto> response = ApiResponseDto.<FoodDatabaseDto>builder()
                .success(true)
                .message("Food created successfully")
                .data(createdFood)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get foods by culture/region
     *
     * @param cultureRegion the culture/region
     * @return list of food database DTOs
     */
    @GetMapping("/foods/culture/{cultureRegion}")
    @Operation(summary = "Get foods by culture", description = "Retrieves foods specific to a culture/region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foods found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<FoodDatabaseDto>>> getFoodsByCultureRegion(
            @Parameter(description = "Culture/region", required = true) @PathVariable String cultureRegion) {
        List<FoodDatabaseDto> foods = foodExerciseDatabaseService.getFoodsByCultureRegion(cultureRegion);

        ApiResponseDto<List<FoodDatabaseDto>> response = ApiResponseDto.<List<FoodDatabaseDto>>builder()
                .success(true)
                .message("Foods retrieved successfully")
                .data(foods)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get traditional foods by culture/region
     *
     * @param cultureRegion the culture/region
     * @return list of food database DTOs
     */
    @GetMapping("/foods/traditional/{cultureRegion}")
    @Operation(summary = "Get traditional foods", description = "Retrieves traditional foods for a specific culture/region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foods found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<FoodDatabaseDto>>> getTraditionalFoods(
            @Parameter(description = "Culture/region", required = true) @PathVariable String cultureRegion) {
        List<FoodDatabaseDto> foods = foodExerciseDatabaseService.getTraditionalFoods(cultureRegion);

        ApiResponseDto<List<FoodDatabaseDto>> response = ApiResponseDto.<List<FoodDatabaseDto>>builder()
                .success(true)
                .message("Traditional foods retrieved successfully")
                .data(foods)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get foods suitable for a specific diet
     *
     * @param dietType the diet type
     * @param cultureRegion the culture/region
     * @return list of food database DTOs
     */
    @GetMapping("/foods/diet")
    @Operation(summary = "Get foods for diet", description = "Retrieves foods suitable for a specific diet and culture/region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foods found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<FoodDatabaseDto>>> getFoodsForDiet(
            @Parameter(description = "Diet type", required = true) @RequestParam String dietType,
            @Parameter(description = "Culture/region", required = true) @RequestParam String cultureRegion) {
        List<FoodDatabaseDto> foods = foodExerciseDatabaseService.getFoodsForDiet(dietType, cultureRegion);

        ApiResponseDto<List<FoodDatabaseDto>> response = ApiResponseDto.<List<FoodDatabaseDto>>builder()
                .success(true)
                .message("Diet-specific foods retrieved successfully")
                .data(foods)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get high protein foods
     *
     * @param cultureRegion the culture/region
     * @param minProtein the minimum protein content
     * @return list of food database DTOs
     */
    @GetMapping("/foods/high-protein")
    @Operation(summary = "Get high protein foods", description = "Retrieves high protein foods for a specific culture/region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foods found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<FoodDatabaseDto>>> getHighProteinFoods(
            @Parameter(description = "Culture/region", required = true) @RequestParam String cultureRegion,
            @Parameter(description = "Minimum protein content per 100g") @RequestParam(defaultValue = "15.0") float minProtein) {
        List<FoodDatabaseDto> foods = foodExerciseDatabaseService.getHighProteinFoods(cultureRegion, minProtein);

        ApiResponseDto<List<FoodDatabaseDto>> response = ApiResponseDto.<List<FoodDatabaseDto>>builder()
                .success(true)
                .message("High protein foods retrieved successfully")
                .data(foods)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Search exercises by name
     *
     * @param query the search query
     * @return list of exercise search results
     */
    @GetMapping("/exercises/search")
    @Operation(summary = "Search exercises", description = "Searches for exercises by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<ExerciseSearchResultDto>>> searchExercises(
            @Parameter(description = "Search query", required = true) @RequestParam String query) {
        List<ExerciseSearchResultDto> results = foodExerciseDatabaseService.searchExercises(query);

        ApiResponseDto<List<ExerciseSearchResultDto>> response = ApiResponseDto.<List<ExerciseSearchResultDto>>builder()
                .success(true)
                .message("Exercises found")
                .data(results)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get exercise details by ID
     *
     * @param id the exercise ID
     * @return the exercise database DTO
     */
    @GetMapping("/exercises/{id}")
    @Operation(summary = "Get exercise details", description = "Retrieves details for a specific exercise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<ExerciseDto>> getExerciseById(
            @Parameter(description = "Exercise ID", required = true) @PathVariable Long id) {
        ExerciseDto exercise = foodExerciseDatabaseService.getExerciseById(id);

        ApiResponseDto<ExerciseDto> response = ApiResponseDto.<ExerciseDto>builder()
                .success(true)
                .message("Exercise retrieved successfully")
                .data(exercise)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Create a new exercise
     *
     * @param createDto the creation exercise DTO
     * @return the created exercise database DTO
     */
    @PostMapping("/exercises")
    @Operation(summary = "Create exercise", description = "Creates a new exercise in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Exercise created",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<ExerciseDto>> createExercise(
            @Parameter(description = "Exercise data", required = true) @Valid @RequestBody ExerciseCreateDto createDto) {
        ExerciseDto createdExercise = foodExerciseDatabaseService.createExercise(createDto);

        ApiResponseDto<ExerciseDto> response = ApiResponseDto.<ExerciseDto>builder()
                .success(true)
                .message("Exercise created successfully")
                .data(createdExercise)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get exercises by muscle group
     *
     * @param muscleGroup the muscle group
     * @return list of exercise database DTOs
     */
    @GetMapping("/exercises/muscle-group/{muscleGroup}")
    @Operation(summary = "Get exercises by muscle group", description = "Retrieves exercises for a specific muscle group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<ExerciseDto>>> getExercisesByMuscleGroup(
            @Parameter(description = "Muscle group", required = true) @PathVariable MuscleGroup muscleGroup) {
        List<ExerciseDto> exercises = foodExerciseDatabaseService.getExercisesByMuscleGroup(muscleGroup);

        ApiResponseDto<List<ExerciseDto>> response = ApiResponseDto.<List<ExerciseDto>>builder()
                .success(true)
                .message("Exercises retrieved successfully")
                .data(exercises)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get exercises by difficulty
     *
     * @param difficulty the difficulty level
     * @return list of exercise database DTOs
     */
    @GetMapping("/exercises/difficulty/{difficulty}")
    @Operation(summary = "Get exercises by difficulty", description = "Retrieves exercises for a specific difficulty level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<ExerciseDto>>> getExercisesByDifficulty(
            @Parameter(description = "Difficulty level", required = true) @PathVariable ExerciseDifficulty difficulty) {
        List<ExerciseDto> exercises = foodExerciseDatabaseService.getExercisesByDifficulty(difficulty);

        ApiResponseDto<List<ExerciseDto>> response = ApiResponseDto.<List<ExerciseDto>>builder()
                .success(true)
                .message("Exercises retrieved successfully")
                .data(exercises)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get exercises by muscle group and difficulty
     *
     * @param muscleGroup the muscle group
     * @param difficulty the difficulty level
     * @return list of exercise database DTOs
     */
    @GetMapping("/exercises/filter")
    @Operation(summary = "Get exercises by muscle group and difficulty",
            description = "Retrieves exercises for a specific muscle group and difficulty level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<ExerciseDto>>> getExercisesByMuscleGroupAndDifficulty(
            @Parameter(description = "Muscle group", required = true) @RequestParam MuscleGroup muscleGroup,
            @Parameter(description = "Difficulty level", required = true) @RequestParam ExerciseDifficulty difficulty) {
        List<ExerciseDto> exercises = foodExerciseDatabaseService.getExercisesByMuscleGroupAndDifficulty(muscleGroup, difficulty);

        ApiResponseDto<List<ExerciseDto>> response = ApiResponseDto.<List<ExerciseDto>>builder()
                .success(true)
                .message("Exercises retrieved successfully")
                .data(exercises)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get body weight exercises
     *
     * @return list of exercise database DTOs
     */
    @GetMapping("/exercises/bodyweight")
    @Operation(summary = "Get bodyweight exercises", description = "Retrieves all bodyweight exercises")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<ExerciseDto>>> getBodyweightExercises() {
        List<ExerciseDto> exercises = foodExerciseDatabaseService.getBodyweightExercises();

        ApiResponseDto<List<ExerciseDto>> response = ApiResponseDto.<List<ExerciseDto>>builder()
                .success(true)
                .message("Bodyweight exercises retrieved successfully")
                .data(exercises)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get exercises by equipment availability
     *
     * @param equipmentList comma-separated list of available equipment
     * @param difficulties list of acceptable difficulty levels
     * @param muscleGroups list of target muscle groups
     * @return list of exercise database DTOs
     */
    @GetMapping("/exercises/equipment")
    @Operation(summary = "Get exercises by equipment",
            description = "Retrieves exercises based on available equipment, difficulties, and muscle groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<ExerciseDto>>> getExercisesByEquipment(
            @Parameter(description = "Comma-separated list of available equipment", required = true)
            @RequestParam String equipmentList,
            @Parameter(description = "List of acceptable difficulty levels")
            @RequestParam(required = false) List<String> difficulties,
            @Parameter(description = "List of target muscle groups")
            @RequestParam(required = false) List<String> muscleGroups) {
        List<ExerciseDto> exercises = foodExerciseDatabaseService.getExercisesByEquipment(equipmentList, difficulties, muscleGroups);

        ApiResponseDto<List<ExerciseDto>> response = ApiResponseDto.<List<ExerciseDto>>builder()
                .success(true)
                .message("Exercises retrieved successfully")
                .data(exercises)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get food recommendations based on user profile
     *
     * @param count the number of recommendations to return
     * @return list of food recommendation DTOs
     */
    @GetMapping("/recommendations/foods")
    @Operation(summary = "Get food recommendations",
            description = "Retrieves food recommendations based on user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<FoodRecommendationDto>>> getFoodRecommendations(
            @Parameter(description = "Number of recommendations") @RequestParam(defaultValue = "10") int count) {
        // Get user profile metrics
        ProfileMetricsDto profileMetrics = userProfileService.getProfileMetrics();

        // Get food recommendations
        List<FoodRecommendationDto> recommendations = foodExerciseDatabaseService.getFoodRecommendations(profileMetrics, count);

        ApiResponseDto<List<FoodRecommendationDto>> response = ApiResponseDto.<List<FoodRecommendationDto>>builder()
                .success(true)
                .message("Food recommendations retrieved successfully")
                .data(recommendations)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get exercise recommendations based on user profile
     *
     * @param count the number of recommendations to return
     * @return list of exercise recommendation DTOs
     */
    @GetMapping("/recommendations/exercises")
    @Operation(summary = "Get exercise recommendations",
            description = "Retrieves exercise recommendations based on user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<ExerciseRecommendationDto>>> getExerciseRecommendations(
            @Parameter(description = "Number of recommendations") @RequestParam(defaultValue = "10") int count) {
        // Get user profile metrics
        ProfileMetricsDto profileMetrics = userProfileService.getProfileMetrics();

        // Get exercise recommendations
        List<ExerciseRecommendationDto> recommendations = foodExerciseDatabaseService.getExerciseRecommendations(profileMetrics, count);

        ApiResponseDto<List<ExerciseRecommendationDto>> response = ApiResponseDto.<List<ExerciseRecommendationDto>>builder()
                .success(true)
                .message("Exercise recommendations retrieved successfully")
                .data(recommendations)
                .build();

        return ResponseEntity.ok(response);
    }
}