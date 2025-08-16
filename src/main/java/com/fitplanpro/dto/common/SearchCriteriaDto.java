package com.fitplanpro.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for search criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDto {
    private String keyword;
    private Map<String, Object> filters;
    private String sortBy;
    private String sortDirection;
    private int page;
    private int size;
}
