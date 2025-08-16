package com.fitplanpro.service;

import com.fitplanpro.dto.tracking.*;
import com.fitplanpro.entity.*;
import com.fitplanpro.exception.TrackingNotFoundException;
import com.fitplanpro.mapper.TrackingMapper;
import com.fitplanpro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for tracking operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final TrackingRepository trackingDataRepository;
    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final ProgressPhotoRepository progressPhotoRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final TrackingMapper trackingMapper;
    private final UserService userService;
    private final AchievementService achievementService;

    /**
     * Save daily tracking data
     *
     * @param trackingDto the tracking data
     * @return the saved tracking data
     */
    @Transactional
    public DailyTrackingDto saveDailyTracking(DailyTrackingDto trackingDto) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Check if tracking data already exists for this date
        Optional<Tracking> existingData = trackingDataRepository.findByUserAndTrackingDate(
                currentUser, trackingDto.getTrackingDate());

        Tracking trackingData;

        if (existingData.isPresent()) {
            // Update existing data
            trackingData = existingData.get();
            trackingMapper.updateEntity(trackingDto, trackingData);
        } else {
            // Create new data
            trackingData = trackingMapper.toEntity(trackingDto, currentUser);
        }

        // Save tracking data
        Tracking savedData = trackingDataRepository.save(trackingData);

        // Check for achievements
        checkTrackingAchievements(currentUser, trackingDto);

        return trackingMapper.toDto(savedData);
    }

    /**
     * Get daily tracking data for a date
     *
     * @param date the date
     * @return the tracking data
     */
    @Transactional(readOnly = true)
    public DailyTrackingDto getDailyTracking(LocalDate date) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get tracking data
        Tracking trackingData = trackingDataRepository.findByUserAndTrackingDate(currentUser, date)
                .orElseThrow(() -> new TrackingNotFoundException("No tracking data found for date: " + date));

        return trackingMapper.toDto(trackingData);
    }

    /**
     * Get tracking data for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of tracking data
     */
    @Transactional(readOnly = true)
    public List<DailyTrackingDto> getTrackingRange(LocalDate startDate, LocalDate endDate) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get tracking data
        List<Tracking> trackingDataList = trackingDataRepository
                .findByUserAndTrackingDateBetweenOrderByTrackingDate(currentUser, startDate, endDate);

        return trackingMapper.toDtoList(trackingDataList);
    }

    /**
     * Save body measurements
     *
     * @param measurementDto the measurement data
     * @return the saved measurement data
     */
    @Transactional
    public BodyMeasurementDto saveBodyMeasurement(BodyMeasurementDto measurementDto) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Check if measurement already exists for this date
        Optional<BodyMeasurement> existingMeasurement = bodyMeasurementRepository
                .findByUserAndMeasurementDate(currentUser, measurementDto.getMeasurementDate());

        BodyMeasurement bodyMeasurement;

        if (existingMeasurement.isPresent()) {
            // Update existing measurement
            bodyMeasurement = existingMeasurement.get();
            // Copy properties manually or with mapper
            if (measurementDto.getChest() != null) bodyMeasurement.setChest(measurementDto.getChest());
            if (measurementDto.getWaist() != null) bodyMeasurement.setWaist(measurementDto.getWaist());
            if (measurementDto.getHips() != null) bodyMeasurement.setHips(measurementDto.getHips());
            if (measurementDto.getArms() != null) bodyMeasurement.setArms(measurementDto.getArms());
            if (measurementDto.getThighs() != null) bodyMeasurement.setThighs(measurementDto.getThighs());
        } else {
            // Create new measurement
            bodyMeasurement = trackingMapper.toBodyMeasurementEntity(measurementDto, currentUser);
        }

        // Save measurement
        BodyMeasurement savedMeasurement = bodyMeasurementRepository.save(bodyMeasurement);

        return trackingMapper.toBodyMeasurementDto(savedMeasurement);
    }

    /**
     * Get body measurements for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of body measurements
     */
    @Transactional(readOnly = true)
    public List<BodyMeasurementDto> getBodyMeasurements(LocalDate startDate, LocalDate endDate) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get measurements
        List<BodyMeasurement> measurements = bodyMeasurementRepository
                .findByUserAndMeasurementDateBetweenOrderByMeasurementDate(currentUser, startDate, endDate);

        return trackingMapper.toBodyMeasurementDtoList(measurements);
    }

    /**
     * Save progress photo
     *
     * @param photoDto the photo data
     * @return the saved photo data
     */
    @Transactional
    public ProgressPhotoDto saveProgressPhoto(ProgressPhotoDto photoDto) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Create new photo
        ProgressPhoto photo = trackingMapper.toProgressPhotoEntity(photoDto, currentUser);

        // Save photo
        ProgressPhoto savedPhoto = progressPhotoRepository.save(photo);

        return trackingMapper.toProgressPhotoDto(savedPhoto);
    }

    /**
     * Get progress photos for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of progress photos
     */
    @Transactional(readOnly = true)
    public List<ProgressPhotoDto> getProgressPhotos(LocalDate startDate, LocalDate endDate) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get photos
        List<ProgressPhoto> photos = progressPhotoRepository
                .findByUserAndPhotoDateBetweenOrderByPhotoDate(currentUser, startDate, endDate);

        return trackingMapper.toProgressPhotoDtoList(photos);
    }

    /**
     * Save workout history
     *
     * @param workoutLogDto the workout log data
     * @return the saved workout log data
     */
    @Transactional
    public WorkoutLogDto saveWorkoutHistory(WorkoutLogDto workoutLogDto) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Create new workout history
        WorkoutHistory workoutHistory = trackingMapper.toWorkoutHistoryEntity(workoutLogDto, currentUser);

        // Set workout date to today if not specified
        if (workoutHistory.getWorkoutDate() == null) {
            workoutHistory.setWorkoutDate(LocalDate.now());
        }

        // Save workout history
        WorkoutHistory savedWorkoutHistory = workoutHistoryRepository.save(workoutHistory);

        // Process achievement
        checkWorkoutAchievements(currentUser);

        // Convert to DTO
        return trackingMapper.toWorkoutLogDto(savedWorkoutHistory);
    }

    /**
     * Get workout history for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of workout logs
     */
    @Transactional(readOnly = true)
    public List<WorkoutLogDto> getWorkoutHistory(LocalDate startDate, LocalDate endDate) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get workout history
        List<WorkoutHistory> workoutHistoryList = workoutHistoryRepository
                .findByUserAndWorkoutDateBetweenOrderByWorkoutDate(currentUser, startDate, endDate);

        // Convert to DTOs
        return trackingMapper.toWorkoutLogDtoList(workoutHistoryList);
    }

    /**
     * Get tracking summary for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the tracking summary
     */
    @Transactional(readOnly = true)
    public TrackingSummaryDto getTrackingSummary(LocalDate startDate, LocalDate endDate) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get tracking data
        List<Tracking> trackingDataList = trackingDataRepository
                .findByUserAndTrackingDateBetweenOrderByTrackingDate(currentUser, startDate, endDate);

        // Get workout history
        List<WorkoutHistory> workoutHistoryList = workoutHistoryRepository
                .findByUserAndWorkoutDateBetweenOrderByWorkoutDate(currentUser, startDate, endDate);

        // Get body measurements
        List<BodyMeasurement> bodyMeasurementList = bodyMeasurementRepository
                .findByUserAndMeasurementDateBetweenOrderByMeasurementDate(currentUser, startDate, endDate);

        // Calculate summary metrics
        int daysTracked = trackingDataList.size();

        // Calculate weight change
        Float startWeight = null;
        Float currentWeight = null;

        if (!trackingDataList.isEmpty()) {
            // Find first tracked weight
            for (Tracking data : trackingDataList) {
                if (data.getWeight() != null) {
                    startWeight = data.getWeight();
                    break;
                }
            }

            // Find last tracked weight
            for (int i = trackingDataList.size() - 1; i >= 0; i--) {
                if (trackingDataList.get(i).getWeight() != null) {
                    currentWeight = trackingDataList.get(i).getWeight();
                    break;
                }
            }
        }

        // Calculate weight change
        Float weightChange = null;
        if (startWeight != null && currentWeight != null) {
            weightChange = currentWeight - startWeight;
        }

        // Calculate workout metrics
        int workoutsCompleted = workoutHistoryList.size();

        // Calculate total workout minutes
        Integer totalWorkoutMinutes = workoutHistoryList.stream()
                .mapToInt(WorkoutHistory::getDurationMinutes)
                .sum();

        // Calculate average workout duration
        Double averageWorkoutDuration = workoutHistoryList.isEmpty() ? null :
                workoutHistoryList.stream()
                        .mapToInt(WorkoutHistory::getDurationMinutes)
                        .average()
                        .orElse(0.0);

        // Calculate adherence percentage
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        Double adherencePercentage = (double) daysTracked / totalDays * 100;

        // Calculate measurement changes
        Map<String, Double> measurementChanges = new HashMap<>();

        if (bodyMeasurementList.size() >= 2) {
            BodyMeasurement firstMeasurement = bodyMeasurementList.get(0);
            BodyMeasurement lastMeasurement = bodyMeasurementList.get(bodyMeasurementList.size() - 1);

            if (firstMeasurement.getChest() != null && lastMeasurement.getChest() != null) {
                measurementChanges.put("chest", (double) (lastMeasurement.getChest() - firstMeasurement.getChest()));
            }

            if (firstMeasurement.getWaist() != null && lastMeasurement.getWaist() != null) {
                measurementChanges.put("waist", (double) (lastMeasurement.getWaist() - firstMeasurement.getWaist()));
            }

            if (firstMeasurement.getHips() != null && lastMeasurement.getHips() != null) {
                measurementChanges.put("hips", (double) (lastMeasurement.getHips() - firstMeasurement.getHips()));
            }

            if (firstMeasurement.getArms() != null && lastMeasurement.getArms() != null) {
                measurementChanges.put("arms", (double) (lastMeasurement.getArms() - firstMeasurement.getArms()));
            }

            if (firstMeasurement.getThighs() != null && lastMeasurement.getThighs() != null) {
                measurementChanges.put("thighs", (double) (lastMeasurement.getThighs() - firstMeasurement.getThighs()));
            }
        }

        // Build summary
        return TrackingSummaryDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .daysTracked(daysTracked)
                .weightChange(weightChange)
                .startWeight(startWeight)
                .currentWeight(currentWeight)
                .workoutsCompleted(workoutsCompleted)
                .totalWorkoutMinutes(totalWorkoutMinutes)
                .averageWorkoutDuration(averageWorkoutDuration)
                .adherencePercentage(adherencePercentage)
                .measurementChanges(measurementChanges)
                .build();
    }

    /**
     * Check for tracking achievements
     *
     * @param user the user
     * @param trackingDto the tracking data
     */
    private void checkTrackingAchievements(User user, DailyTrackingDto trackingDto) {
        // Check for consistent tracking
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // Last 7 days

        boolean hasTrackedConsistently = trackingDataRepository.hasTrackedConsistently(user, startDate, endDate);

        if (hasTrackedConsistently) {
            // Award achievement for consistent tracking
            achievementService.awardAchievement(user.getId(), "Consistent Tracker", 100);
        }

        // Check for water intake achievement
        if (trackingDto.getWaterIntake() != null && trackingDto.getWaterIntake() >= 2000) {
            // Award achievement for water intake
            achievementService.awardAchievement(user.getId(), "Water Champion", 100);
        }

        // Check for weight milestone
        if (trackingDto.getWeight() != null) {
            // Get initial weight
            Optional<Tracking> firstTracking = trackingDataRepository.findByUser(user).stream()
                    .filter(td -> td.getWeight() != null)
                    .min((td1, td2) -> td1.getTrackingDate().compareTo(td2.getTrackingDate()));

            if (firstTracking.isPresent()) {
                float initialWeight = firstTracking.get().getWeight();
                float currentWeight = trackingDto.getWeight();
                float weightLoss = initialWeight - currentWeight;

                if (weightLoss >= 5.0f) {
                    // Award achievement for weight loss milestone
                    achievementService.awardAchievement(user.getId(), "First Milestone", 100);
                }
            }
        }
    }

    /**
     * Check for workout achievements
     *
     * @param user the user
     */
    private void checkWorkoutAchievements(User user) {
        // Check for first workout
        long workoutCount = workoutHistoryRepository.countByUser(user);

        if (workoutCount == 1) {
            // Award achievement for first workout
            achievementService.awardAchievement(user.getId(), "First Workout", 100);
        }

        // Check for weekly warrior (all scheduled workouts in a week)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // Last 7 days

        long weeklyWorkouts = workoutHistoryRepository.countByUserAndWorkoutDateBetween(user, startDate, endDate);

        if (weeklyWorkouts >= 3) { // Assuming 3+ workouts per week is the goal
            // Award achievement for weekly warrior
            achievementService.awardAchievement(user.getId(), "Weekly Warrior", 100);
        }
    }
}