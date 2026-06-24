package com.teamzeu.velo.dto;

import com.teamzeu.velo.enums.OtpType;

public record RequestOtpRequest(
        String fullName,
        String email,
        OtpType purpose
) {}
