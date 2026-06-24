package com.teamzeu.velo.dto;

import jakarta.validation.constraints.NotBlank;

public record AccessTokenRequest(
        @NotBlank
        String refreshToken
) {}
