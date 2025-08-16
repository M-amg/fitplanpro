package com.fitplanpro.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for app settings
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppSettingsDto {
    private boolean notificationsEnabled;
    private boolean reminderNotifications;
    private boolean achievementNotifications;
    private boolean emailNotifications;
    private String measurementUnit; // METRIC or IMPERIAL
    private String theme;
    private boolean darkMode;
    private Map<String, Object> additionalSettings;
}
