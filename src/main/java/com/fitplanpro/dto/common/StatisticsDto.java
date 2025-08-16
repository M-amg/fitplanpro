package com.fitplanpro.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    private Map<String, Object> metrics;
    private Map<LocalDate, Map<String, Object>> timeSeriesData;
    private Map<String, Double> distributions;
}
