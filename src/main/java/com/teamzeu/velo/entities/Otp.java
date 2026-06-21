package com.teamzeu.velo.entities;

import com.teamzeu.velo.enums.OtpType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
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
