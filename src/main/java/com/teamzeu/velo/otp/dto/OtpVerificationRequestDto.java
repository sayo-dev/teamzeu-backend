package com.teamzeu.velo.otp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpVerificationRequestDto(
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "OTP cannot be blank")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        String otp
) {
}

