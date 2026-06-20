package com.teamzeu.velo.otp.model;

import com.teamzeu.velo.common.enums.OtpType;
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
