package com.fitplanpro.dto.user;

import com.fitplanpro.enums.LanguagePreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning user data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String phone;
    private LanguagePreference languagePreference;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
