package com.teamzeu.velo.user.model;


import com.teamzeu.velo.common.BaseEntity;
import com.teamzeu.velo.common.enums.PROVIDER;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "oauth_acc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OauthModel extends BaseEntity {

    @Column(name = "userID")
    private UUID user_Id;

    @Column(nullable = false)
    private PROVIDER provider;

    @Column(nullable = false)
    private String providerUserId;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime tokenExpiresAt;


}
