package com.fitplanpro.entity;

import com.fitplanpro.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Float height;

    @Column(name = "current_weight", nullable = false)
    private Float currentWeight;

    @Column(name = "target_weight")
    private Float targetWeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType goalType;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_experience", nullable = false)
    private TrainingExperience trainingExperience;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_location", nullable = false)
    private TrainingLocation trainingLocation;

    @Column(name = "days_per_week", nullable = false)
    private Integer daysPerWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "diet_preference", nullable = false)
    private DietPreference dietPreference;

    @Column(name = "meals_per_day", nullable = false)
    private Integer mealsPerDay;

    @Column(name = "snacks_per_day", nullable = false)
    private Integer snacksPerDay;

    @Column(name = "location_culture", nullable = false)
    private String locationCulture;

    @Column(name = "medical_conditions")
    private String medicalConditions;

    @Column(name = "food_allergies")
    private String foodAllergies;

    @Column(name = "time_per_workout")
    private Integer timePerWorkout;

    @Column(name = "equipment_available")
    private String equipmentAvailable;

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_constraints")
    private BudgetConstraint budgetConstraints;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_workout_time")
    private WorkoutTime preferredWorkoutTime;

    @Column(name = "profile_hash", nullable = false, unique = true)
    private String profileHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;


}