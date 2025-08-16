package com.fitplanpro.dto.fooddatabase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for food recommendation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecommendationDto {
    private FoodDatabaseDto food;
    private String recommendationReason;
    private Float matchScore;
    private String dietaryMatch;
    private String culturalRelevance;
    private String nutritionalHighlight;
}
