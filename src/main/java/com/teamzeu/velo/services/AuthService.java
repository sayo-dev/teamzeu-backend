package com.teamzeu.velo.services;

import com.teamzeu.velo.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.dto.LoginResponse;
import com.teamzeu.velo.dto.LoginRequestDto;
import com.teamzeu.velo.dto.SignUpRequestDto;
import com.teamzeu.velo.dto.AccessTokenRequest;
import com.teamzeu.velo.dto.RequestOtpRequest;

public interface AuthService {
    void signUp(SignUpRequestDto signUpRequestDto);

    void verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto);

    LoginResponse login(LoginRequestDto loginRequestDto);

    LoginResponse getAccessToken(AccessTokenRequest accessTokenRequest);

    void resendOtp(RequestOtpRequest requestOtpRequest);
}
