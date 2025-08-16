package com.fitplanpro.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for macro nutrient split
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MacroSplitDto {
    private Integer proteinPercentage;
    private Integer carbsPercentage;
    private Integer fatsPercentage;
    private Integer proteinGrams;
    private Integer carbsGrams;
    private Integer fatsGrams;
}
