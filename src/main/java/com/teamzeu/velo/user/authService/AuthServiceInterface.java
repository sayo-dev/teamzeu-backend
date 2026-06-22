package com.teamzeu.velo.user.authService;

import com.teamzeu.velo.otp.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.user.dto.*;

public interface AuthServiceInterface {
    AuthResponseDto signUp(SignUpRequestDto signUpRequestDto);

    AuthResponseDto verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto);

    AuthLoginResponse login(LoginRequestDto loginRequestDto);

    AuthLoginResponse getAccessToken(AccessTokenRequest accessTokenRequest);

    AuthResponseDto requestOtp(RequestOtpRequest requestOtpRequest);
}
