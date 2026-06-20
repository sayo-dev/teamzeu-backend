package com.teamzeu.velo.user.model;

import com.teamzeu.velo.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_verification")
public class EmailModel extends BaseEntity {
    @Column(name = "userID", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false, updatable = false)
    private LocalDateTime usedAt;

    
}
