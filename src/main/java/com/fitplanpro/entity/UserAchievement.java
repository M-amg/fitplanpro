package com.fitplanpro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_type_id", nullable = false)
    private AchievementType achievementType;

    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;

    @Column(nullable = false)
    private Integer progress;

    @PrePersist
    protected void onCreate() {
        achievedAt = LocalDateTime.now();
        if (progress == null) {
            progress = 100;
        }
    }
}
