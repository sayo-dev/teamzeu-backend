package com.teamzeu.velo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification extends BaseEntity {
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false, updatable = false)
    private LocalDateTime usedAt;
}
