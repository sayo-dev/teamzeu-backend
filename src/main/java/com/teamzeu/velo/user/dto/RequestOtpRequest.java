package com.teamzeu.velo.user.dto;

import com.teamzeu.velo.common.enums.OtpType;

public record RequestOtpRequest(
        String fullName,
        String email,
        OtpType purpose
) {
}
