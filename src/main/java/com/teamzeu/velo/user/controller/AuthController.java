package com.teamzeu.velo.user.controller;

import com.teamzeu.velo.common.ResponseHandler;
import com.teamzeu.velo.otp.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.user.authService.AuthServiceImpl;
import com.teamzeu.velo.user.authService.AuthServiceInterface;
import com.teamzeu.velo.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authService;
    private final ResponseHandler responseHandler;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        AuthResponseDto response = authService.signUp(signUpRequestDto);
        return responseHandler.responseHandler("user registered successfully", HttpStatus.CREATED, response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthLoginResponse response = authService.login(loginRequestDto);
        return responseHandler.responseHandler("user logged in successfully", HttpStatus.OK, response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerificationRequestDto otpVerificationRequestDto) {
        AuthResponseDto response = authService.verifyOtp(otpVerificationRequestDto);
        return responseHandler.responseHandler("OTP verified successfully", HttpStatus.OK, response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> requestOtp(@Valid @RequestBody RequestOtpRequest otpRequest) {
        AuthResponseDto response = authService.requestOtp(otpRequest);
        return responseHandler.responseHandler("OTP sent successfully", HttpStatus.OK, response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> requestAccessToken(@Valid @RequestBody AccessTokenRequest accessTokenRequest) {
        AuthLoginResponse response = authService.getAccessToken(accessTokenRequest);
        return responseHandler.responseHandler("Access token sent successfully", HttpStatus.OK, response);
    }
}

