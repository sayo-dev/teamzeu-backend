package com.teamzeu.velo.controllers;

import com.teamzeu.velo.common.ApiResponse;
import com.teamzeu.velo.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.services.AuthService;
import com.teamzeu.velo.dto.LoginResponse;
import com.teamzeu.velo.dto.LoginRequestDto;
import com.teamzeu.velo.dto.SignUpRequestDto;
import com.teamzeu.velo.dto.AccessTokenRequest;
import com.teamzeu.velo.dto.RequestOtpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("user registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponse response = authService.login(loginRequestDto);
        return ResponseEntity.ok(ApiResponse.success("user logged in successfully", response));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody OtpVerificationRequestDto otpVerificationRequestDto) {
        authService.verifyOtp(otpVerificationRequestDto);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", null));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@Valid @RequestBody RequestOtpRequest otpRequest) {
        authService.resendOtp(otpRequest);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody AccessTokenRequest accessTokenRequest) {
        LoginResponse response = authService.getAccessToken(accessTokenRequest);
        return ResponseEntity.ok(ApiResponse.success("Access token sent successfully", response));
    }
}
