package com.teamzeu.velo.otp.model;

import com.teamzeu.velo.common.BaseEntity;
import com.teamzeu.velo.common.enums.OtpType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "otp")
@Builder
public class Otp extends BaseEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpType purpose;

    @Column(nullable = false)
    private Boolean used = false;

    @Column
    private LocalDateTime expiryTime;

}


