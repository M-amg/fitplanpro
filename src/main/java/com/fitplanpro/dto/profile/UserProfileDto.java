package com.fitplanpro.dto.profile;

import com.fitplanpro.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning user profile data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private Long userId;
    private Gender gender;
    private Integer age;
    private Float height;
    private Float currentWeight;
    private Float targetWeight;
    private GoalType goalType;
    private TrainingExperience trainingExperience;
    private TrainingLocation trainingLocation;
    private Integer daysPerWeek;
    private DietPreference dietPreference;
    private Integer mealsPerDay;
    private Integer snacksPerDay;
    private String locationCulture;
    private String medicalConditions;
    private String foodAllergies;
    private Integer timePerWorkout;
    private String equipmentAvailable;
    private BudgetConstraint budgetConstraints;
    private WorkoutTime preferredWorkoutTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
