package com.teamzeu.velo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResponse {
    private String accessToken;
    private String refreshToken;
}
