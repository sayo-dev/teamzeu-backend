package com.teamzeu.velo.controllers;

import com.teamzeu.velo.common.ApiResponse;
import com.teamzeu.velo.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.services.AuthService;
import com.teamzeu.velo.dto.AuthLoginResponse;
import com.teamzeu.velo.dto.AuthResponseDto;
import com.teamzeu.velo.dto.LoginRequestDto;
import com.teamzeu.velo.dto.SignUpRequestDto;
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
        AuthLoginResponse response = authService.login(loginRequestDto);
        return ResponseEntity.ok(ApiResponse.success("user logged in successfully", response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerificationRequestDto otpVerificationRequestDto) {
        AuthResponseDto response = authService.verifyOtp(otpVerificationRequestDto);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", response));
    }
}
