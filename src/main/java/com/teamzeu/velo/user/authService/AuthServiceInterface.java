package com.teamzeu.velo.user.authService;

import com.teamzeu.velo.otp.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.user.dto.AuthLoginResponse;
import com.teamzeu.velo.user.dto.AuthResponseDto;
import com.teamzeu.velo.user.dto.LoginRequestDto;
import com.teamzeu.velo.user.dto.SignUpRequestDto;

public interface AuthServiceInterface {
    AuthResponseDto signUp(SignUpRequestDto signUpRequestDto);

    AuthResponseDto verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto);

    AuthLoginResponse login(LoginRequestDto loginRequestDto);

    AuthLoginResponse getAccessToken(String refreshToken);
}
