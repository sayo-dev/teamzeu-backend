package com.teamzeu.velo.user.mapper;

import com.teamzeu.velo.user.dto.AuthResponseDto;
import com.teamzeu.velo.user.dto.SignUpRequestDto;
import com.teamzeu.velo.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class DtoMappers {

    public User toUSer(SignUpRequestDto signUpRequestDto) {
        return User.builder()
                .email(signUpRequestDto.email())
                .fullName(signUpRequestDto.fullName())
                .bio(signUpRequestDto.bio())
                .role(signUpRequestDto.role())
                .isEmailVerified(false)
                .build();
    }

    public AuthResponseDto toAuthResponseDto(User user) {
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


