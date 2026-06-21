package com.teamzeu.velo.entities;

import com.teamzeu.velo.enums.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuth extends BaseEntity {

    private UUID userId;

    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String providerUserId;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime tokenExpiresAt;
}
