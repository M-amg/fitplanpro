package com.fitplanpro.controller;

import com.fitplanpro.dto.common.ApiResponseDto;
import com.fitplanpro.dto.common.ErrorResponseDto;
import com.fitplanpro.dto.tracking.*;
import com.fitplanpro.service.TrackingService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for tracking operations
 */
@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
@Tag(name = "Tracking", description = "Progress tracking API")
@SecurityRequirement(name = "bearerAuth")
public class TrackingController {

    private final TrackingService trackingService;

    /**
     * Save daily tracking data
     *
     * @param trackingDto the tracking data
     * @return the saved tracking data
     */
    @PostMapping("/daily")
    @Operation(summary = "Save daily tracking", description = "Saves daily tracking data for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tracking data saved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<DailyTrackingDto>> saveDailyTracking(
            @RequestBody @Valid DailyTrackingDto trackingDto) {
        DailyTrackingDto savedTracking = trackingService.saveDailyTracking(trackingDto);

        ApiResponseDto<DailyTrackingDto> response = ApiResponseDto.<DailyTrackingDto>builder()
                .success(true)
                .message("Tracking data saved successfully")
                .data(savedTracking)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get daily tracking data
     *
     * @param date the date
     * @return the tracking data
     */
    @GetMapping("/daily")
    @Operation(summary = "Get daily tracking", description = "Retrieves daily tracking data for the specified date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tracking data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Tracking data not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<DailyTrackingDto>> getDailyTracking(
            @Parameter(description = "Date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyTrackingDto trackingDto = trackingService.getDailyTracking(date);

        ApiResponseDto<DailyTrackingDto> response = ApiResponseDto.<DailyTrackingDto>builder()
                .success(true)
                .message("Tracking data retrieved successfully")
                .data(trackingDto)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get tracking data for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of tracking data
     */
    @GetMapping("/range")
    @Operation(summary = "Get tracking range", description = "Retrieves tracking data for a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tracking data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date range",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<DailyTrackingDto>>> getTrackingRange(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DailyTrackingDto> trackingList = trackingService.getTrackingRange(startDate, endDate);

        ApiResponseDto<List<DailyTrackingDto>> response = ApiResponseDto.<List<DailyTrackingDto>>builder()
                .success(true)
                .message("Tracking data retrieved successfully")
                .data(trackingList)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Save body measurements
     *
     * @param measurementDto the measurement data
     * @return the saved measurement data
     */
    @PostMapping("/measurements")
    @Operation(summary = "Save body measurements", description = "Saves body measurements for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Measurements saved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<BodyMeasurementDto>> saveBodyMeasurement(
            @RequestBody @Valid BodyMeasurementDto measurementDto) {
        BodyMeasurementDto savedMeasurement = trackingService.saveBodyMeasurement(measurementDto);

        ApiResponseDto<BodyMeasurementDto> response = ApiResponseDto.<BodyMeasurementDto>builder()
                .success(true)
                .message("Measurements saved successfully")
                .data(savedMeasurement)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get body measurements for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of body measurements
     */
    @GetMapping("/measurements")
    @Operation(summary = "Get body measurements", description = "Retrieves body measurements for a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date range",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<BodyMeasurementDto>>> getBodyMeasurements(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<BodyMeasurementDto> measurements = trackingService.getBodyMeasurements(startDate, endDate);

        ApiResponseDto<List<BodyMeasurementDto>> response = ApiResponseDto.<List<BodyMeasurementDto>>builder()
                .success(true)
                .message("Measurements retrieved successfully")
                .data(measurements)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Save progress photo
     *
     * @param photoDto the photo data
     * @return the saved photo data
     */
    @PostMapping("/photos")
    @Operation(summary = "Save progress photo", description = "Saves a progress photo for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo saved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<ProgressPhotoDto>> saveProgressPhoto(
            @RequestBody @Valid ProgressPhotoDto photoDto) {
        ProgressPhotoDto savedPhoto = trackingService.saveProgressPhoto(photoDto);

        ApiResponseDto<ProgressPhotoDto> response = ApiResponseDto.<ProgressPhotoDto>builder()
                .success(true)
                .message("Photo saved successfully")
                .data(savedPhoto)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get progress photos for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of progress photos
     */
    @GetMapping("/photos")
    @Operation(summary = "Get progress photos", description = "Retrieves progress photos for a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photos retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date range",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<ProgressPhotoDto>>> getProgressPhotos(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ProgressPhotoDto> photos = trackingService.getProgressPhotos(startDate, endDate);

        ApiResponseDto<List<ProgressPhotoDto>> response = ApiResponseDto.<List<ProgressPhotoDto>>builder()
                .success(true)
                .message("Photos retrieved successfully")
                .data(photos)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Save workout history
     *
     * @param workoutLogDto the workout log data
     * @return the saved workout log data
     */
    @PostMapping("/workouts")
    @Operation(summary = "Save workout", description = "Saves a workout for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout saved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<WorkoutLogDto>> saveWorkoutHistory(
            @RequestBody @Valid WorkoutLogDto workoutLogDto) {
        WorkoutLogDto savedWorkout = trackingService.saveWorkoutHistory(workoutLogDto);

        ApiResponseDto<WorkoutLogDto> response = ApiResponseDto.<WorkoutLogDto>builder()
                .success(true)
                .message("Workout saved successfully")
                .data(savedWorkout)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get workout history for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of workout logs
     */
    @GetMapping("/workouts")
    @Operation(summary = "Get workout history", description = "Retrieves workout history for a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date range",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<WorkoutLogDto>>> getWorkoutHistory(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkoutLogDto> workouts = trackingService.getWorkoutHistory(startDate, endDate);

        ApiResponseDto<List<WorkoutLogDto>> response = ApiResponseDto.<List<WorkoutLogDto>>builder()
                .success(true)
                .message("Workout history retrieved successfully")
                .data(workouts)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get tracking summary for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the tracking summary
     */
    @GetMapping("/summary")
    @Operation(summary = "Get tracking summary", description = "Retrieves a summary of tracking data for a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date range",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<TrackingSummaryDto>> getTrackingSummary(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        TrackingSummaryDto summary = trackingService.getTrackingSummary(startDate, endDate);

        ApiResponseDto<TrackingSummaryDto> response = ApiResponseDto.<TrackingSummaryDto>builder()
                .success(true)
                .message("Summary retrieved successfully")
                .data(summary)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get latest body measurements
     *
     * @return the latest body measurement
     */
    @GetMapping("/measurements/latest")
    @Operation(summary = "Get latest measurements", description = "Retrieves the latest body measurements for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No measurements found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<BodyMeasurementDto>> getLatestBodyMeasurement() {
        // Get current date
        LocalDate today = LocalDate.now();
        // Get measurements for the last 90 days
        LocalDate startDate = today.minusDays(90);

        List<BodyMeasurementDto> measurements = trackingService.getBodyMeasurements(startDate, today);

        if (measurements.isEmpty()) {
            ApiResponseDto<BodyMeasurementDto> response = ApiResponseDto.<BodyMeasurementDto>builder()
                    .success(false)
                    .message("No measurements found")
                    .build();

            return ResponseEntity.ok(response);
        }

        // Get the latest measurement
        BodyMeasurementDto latestMeasurement = measurements.getLast();

        ApiResponseDto<BodyMeasurementDto> response = ApiResponseDto.<BodyMeasurementDto>builder()
                .success(true)
                .message("Latest measurements retrieved successfully")
                .data(latestMeasurement)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get latest progress photo
     *
     * @param photoType the photo type (optional)
     * @return the latest progress photo
     */
    @GetMapping("/photos/latest")
    @Operation(summary = "Get latest photo", description = "Retrieves the latest progress photo for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No photos found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<ProgressPhotoDto>> getLatestProgressPhoto(
            @Parameter(description = "Photo type (FRONT, SIDE, BACK, etc.)")
            @RequestParam(required = false) String photoType) {
        // Get current date
        LocalDate today = LocalDate.now();
        // Get photos for the last 90 days
        LocalDate startDate = today.minusDays(90);

        List<ProgressPhotoDto> photos = trackingService.getProgressPhotos(startDate, today);

        if (photos.isEmpty()) {
            ApiResponseDto<ProgressPhotoDto> response = ApiResponseDto.<ProgressPhotoDto>builder()
                    .success(false)
                    .message("No photos found")
                    .build();

            return ResponseEntity.ok(response);
        }

        // Filter by photo type if specified
        if (photoType != null && !photoType.isEmpty()) {
            photos = photos.stream()
                    .filter(photo -> photoType.equals(photo.getPhotoType()))
                    .toList();

            if (photos.isEmpty()) {
                ApiResponseDto<ProgressPhotoDto> response = ApiResponseDto.<ProgressPhotoDto>builder()
                        .success(false)
                        .message("No photos found for type: " + photoType)
                        .build();

                return ResponseEntity.ok(response);
            }
        }

        // Get the latest photo
        ProgressPhotoDto latestPhoto = photos.getLast();

        ApiResponseDto<ProgressPhotoDto> response = ApiResponseDto.<ProgressPhotoDto>builder()
                .success(true)
                .message("Latest photo retrieved successfully")
                .data(latestPhoto)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get latest workout
     *
     * @return the latest workout
     */
    @GetMapping("/workouts/latest")
    @Operation(summary = "Get latest workout", description = "Retrieves the latest workout for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No workouts found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<WorkoutLogDto>> getLatestWorkout() {
        // Get current date
        LocalDate today = LocalDate.now();
        // Get workouts for the last 30 days
        LocalDate startDate = today.minusDays(30);

        List<WorkoutLogDto> workouts = trackingService.getWorkoutHistory(startDate, today);

        if (workouts.isEmpty()) {
            ApiResponseDto<WorkoutLogDto> response = ApiResponseDto.<WorkoutLogDto>builder()
                    .success(false)
                    .message("No workouts found")
                    .build();

            return ResponseEntity.ok(response);
        }

        // Get the latest workout
        WorkoutLogDto latestWorkout = workouts.getLast();

        ApiResponseDto<WorkoutLogDto> response = ApiResponseDto.<WorkoutLogDto>builder()
                .success(true)
                .message("Latest workout retrieved successfully")
                .data(latestWorkout)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get today's tracking data
     *
     * @return today's tracking data
     */
    @GetMapping("/today")
    @Operation(summary = "Get today's tracking", description = "Retrieves tracking data for today")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Today's tracking data retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No tracking data for today",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<DailyTrackingDto>> getTodayTracking() {
        try {
            // Get today's tracking data
            DailyTrackingDto todayTracking = trackingService.getDailyTracking(LocalDate.now());

            ApiResponseDto<DailyTrackingDto> response = ApiResponseDto.<DailyTrackingDto>builder()
                    .success(true)
                    .message("Today's tracking data retrieved successfully")
                    .data(todayTracking)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // If no tracking data exists for today, return an empty response
            ApiResponseDto<DailyTrackingDto> response = ApiResponseDto.<DailyTrackingDto>builder()
                    .success(false)
                    .message("No tracking data found for today")
                    .build();

            return ResponseEntity.ok(response);
        }
    }
}