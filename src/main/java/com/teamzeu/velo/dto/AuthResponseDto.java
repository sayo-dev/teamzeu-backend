package com.teamzeu.velo.dto;

import com.teamzeu.velo.enums.Roles;
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
    private Roles role;
}
