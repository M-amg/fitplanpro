package com.fitplanpro.dto.profile;

import com.fitplanpro.enums.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a user profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateDto {

    @Min(value = 30, message = "Current weight must be at least 30 kg")
    @Max(value = 300, message = "Current weight must be at most 300 kg")
    private Float currentWeight;

    @Min(value = 30, message = "Target weight must be at least 30 kg")
    @Max(value = 300, message = "Target weight must be at most 300 kg")
    private Float targetWeight;

    private GoalType goalType;

    private TrainingExperience trainingExperience;

    private TrainingLocation trainingLocation;

    @Min(value = 1, message = "Days per week must be at least 1")
    @Max(value = 7, message = "Days per week must be at most 7")
    private Integer daysPerWeek;

    private DietPreference dietPreference;

    @Min(value = 1, message = "Meals per day must be at least 1")
    @Max(value = 6, message = "Meals per day must be at most 6")
    private Integer mealsPerDay;

    @Min(value = 0, message = "Snacks per day must be at least 0")
    @Max(value = 5, message = "Snacks per day must be at most 5")
    private Integer snacksPerDay;

    private String medicalConditions;

    private String foodAllergies;

    @Min(value = 15, message = "Time per workout must be at least 15 minutes")
    @Max(value = 180, message = "Time per workout must be at most 180 minutes")
    private Integer timePerWorkout;

    private String equipmentAvailable;

    private BudgetConstraint budgetConstraints;

    private WorkoutTime preferredWorkoutTime;
}
