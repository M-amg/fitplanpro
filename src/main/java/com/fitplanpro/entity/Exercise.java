package com.fitplanpro.entity;

import com.fitplanpro.enums.ExerciseDifficulty;
import com.fitplanpro.enums.MuscleGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercise")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "muscle_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private MuscleGroup muscleGroup;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExerciseDifficulty difficulty;

    @Column(name = "equipment_required", nullable = false)
    private String equipmentRequired;

    @Column(nullable = false, columnDefinition = "text")
    private String instructions;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}