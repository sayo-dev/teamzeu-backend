package com.teamzeu.velo.user.dto;

import com.teamzeu.velo.common.enums.VELO_ROLE;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private String profileImageUrl;
    private Boolean isEmailVerified;
    private String bio;
    private VELO_ROLE role;

}

