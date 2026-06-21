package com.teamzeu.velo.dto;

import com.teamzeu.velo.enums.OtpType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OtpRequest(
        String email,
        String otpCode,
        OtpType purpose,
        LocalDateTime expiryTime
) {
}
