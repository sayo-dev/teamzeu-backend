package com.teamzeu.velo.services;

import com.teamzeu.velo.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.dto.AuthLoginResponse;
import com.teamzeu.velo.dto.AuthResponseDto;
import com.teamzeu.velo.dto.LoginRequestDto;
import com.teamzeu.velo.dto.SignUpRequestDto;

public interface AuthService {
    void signUp(SignUpRequestDto signUpRequestDto);

    AuthResponseDto verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto);

    AuthLoginResponse login(LoginRequestDto loginRequestDto);

    AuthLoginResponse getAccessToken(String refreshToken);
}
