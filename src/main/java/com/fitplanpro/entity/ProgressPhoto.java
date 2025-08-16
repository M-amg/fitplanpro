package com.fitplanpro.entity;

import com.fitplanpro.enums.PhotoType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_photos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "photo_date", nullable = false)
    private LocalDate photoDate;

    @Column(name = "photo_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PhotoType photoType;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
