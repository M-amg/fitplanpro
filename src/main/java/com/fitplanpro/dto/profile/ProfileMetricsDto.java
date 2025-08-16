package com.fitplanpro.dto.profile;

import com.fitplanpro.enums.DietPreference;
import com.fitplanpro.enums.Gender;
import com.fitplanpro.enums.GoalType;
import com.fitplanpro.enums.TrainingExperience;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for profile metrics (simplified profile data)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileMetricsDto {
    private Gender gender;
    private Integer age;
    private Float height;
    private Float currentWeight;
    private Float targetWeight;
    private GoalType goalType;
    private TrainingExperience trainingExperience;
    private Integer daysPerWeek;
    private DietPreference dietPreference;
}
