package com.fitplanpro.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for date range
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeDto {
    private LocalDate startDate;
    private LocalDate endDate;
}
