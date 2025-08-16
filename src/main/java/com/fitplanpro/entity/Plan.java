package com.fitplanpro.entity;


import com.fitplanpro.enums.PlanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_hash", nullable = false)
    private String profileHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;

    @Column(name = "plan_data", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> planData;

    @Column(name = "ai_model_used")
    private String aiModelUsed;

    @Column(name = "generation_time", nullable = false)
    private LocalDateTime generationTime;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "similarity_score")
    private Float similarityScore;

    @PrePersist
    protected void onCreate() {
        generationTime = LocalDateTime.now();
    }
}