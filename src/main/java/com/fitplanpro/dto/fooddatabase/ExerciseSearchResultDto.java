package com.fitplanpro.dto.fooddatabase;

import com.fitplanpro.enums.ExerciseDifficulty;
import com.fitplanpro.enums.MuscleGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for exercise search result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSearchResultDto {
    private Long id;
    private String name;
    private MuscleGroup muscleGroup;
    private ExerciseDifficulty difficulty;
    private String equipmentSummary;
    private Boolean hasVideo;
}
