package com.fitplanpro.dto.tracking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for progress photo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressPhotoDto {

    @NotNull(message = "Photo date is required")
    @PastOrPresent(message = "Photo date cannot be in the future")
    private LocalDate photoDate;

    @NotNull(message = "Photo type is required")
    private String photoType;

    @NotNull(message = "Photo URL is required")
    private String photoUrl;
}
