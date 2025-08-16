package com.fitplanpro.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for feedback
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {
    private Long id;
    private Long userId;
    private String feedbackType;
    private int rating;
    private String comment;
    private Map<String, Object> metadata;
    private String timestamp;
}
