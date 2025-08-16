package com.fitplanpro.entity;

import com.fitplanpro.enums.AchievementCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievement_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AchievementCategory category;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

