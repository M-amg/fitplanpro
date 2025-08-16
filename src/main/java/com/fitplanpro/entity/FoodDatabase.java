package com.fitplanpro.entity;

import com.fitplanpro.enums.FoodGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "food_database")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "culture_region", nullable = false)
    private String cultureRegion;

    @Column(name = "calories_per_100g", nullable = false)
    private Float caloriesPer100g;

    @Column(name = "protein_per_100g", nullable = false)
    private Float proteinPer100g;

    @Column(name = "carbs_per_100g", nullable = false)
    private Float carbsPer100g;

    @Column(name = "fat_per_100g", nullable = false)
    private Float fatPer100g;

    @Column(name = "food_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodGroup foodGroup;

    @Column(name = "seasonal_availability")
    private String seasonalAvailability;

    @Column(name = "is_traditional", nullable = false)
    private Boolean isTraditional;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}