package com.fitplanpro.dto.profile;

import com.fitplanpro.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * DTO for creating a user profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCreateDto {

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Age is required")
    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 100, message = "Age must be at most 100")
    private Integer age;

    @NotNull(message = "Height is required")
    @Min(value = 100, message = "Height must be at least 100 cm")
    @Max(value = 250, message = "Height must be at most 250 cm")
    private Float height;

    @NotNull(message = "Current weight is required")
    @Min(value = 30, message = "Weight must be at least 30 kg")
    @Max(value = 300, message = "Weight must be at most 300 kg")
    private Float currentWeight;

    @Min(value = 30, message = "Target weight must be at least 30 kg")
    @Max(value = 300, message = "Target weight must be at most 300 kg")
    private Float targetWeight;

    @NotNull(message = "Goal type is required")
    private GoalType goalType;

    @NotNull(message = "Training experience is required")
    private TrainingExperience trainingExperience;

    @NotNull(message = "Training location is required")
    private TrainingLocation trainingLocation;

    @NotNull(message = "Days per week is required")
    @Min(value = 1, message = "Days per week must be at least 1")
    @Max(value = 7, message = "Days per week must be at most 7")
    private Integer daysPerWeek;

    @NotNull(message = "Diet preference is required")
    private DietPreference dietPreference;

    @NotNull(message = "Meals per day is required")
    @Min(value = 1, message = "Meals per day must be at least 1")
    @Max(value = 6, message = "Meals per day must be at most 6")
    private Integer mealsPerDay;

    @NotNull(message = "Snacks per day is required")
    @Min(value = 0, message = "Snacks per day must be at least 0")
    @Max(value = 5, message = "Snacks per day must be at most 5")
    private Integer snacksPerDay;

    @NotBlank(message = "Location/culture is required")
    private String locationCulture;

    private String medicalConditions;

    private String foodAllergies;

    @Min(value = 15, message = "Time per workout must be at least 15 minutes")
    @Max(value = 180, message = "Time per workout must be at most 180 minutes")
    private Integer timePerWorkout;

    private String equipmentAvailable;

    private BudgetConstraint budgetConstraints;

    private WorkoutTime preferredWorkoutTime;
}

