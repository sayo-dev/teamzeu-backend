package com.teamzeu.velo.mappers;

import com.teamzeu.velo.dto.AuthResponseDto;
import com.teamzeu.velo.dto.SignUpRequestDto;
import com.teamzeu.velo.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(SignUpRequestDto signUpRequestDto) {
        return User.builder()
                .email(signUpRequestDto.email())
                .fullName(signUpRequestDto.fullName())
                .bio(signUpRequestDto.bio())
                .role(signUpRequestDto.role())
                .isEmailVerified(false)
                .build();
    }

    public AuthResponseDto toResponse(User user) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .profileImageUrl(user.getAvatar_url())
                .isEmailVerified(user.getIsEmailVerified())
                .bio(user.getBio())
                .role(user.getRole())
                .build();
    }
}
