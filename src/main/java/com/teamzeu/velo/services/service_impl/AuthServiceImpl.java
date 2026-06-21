package com.teamzeu.velo.services.service_impl;

import com.teamzeu.velo.exceptions.*;
import com.teamzeu.velo.dto.EmailRequest;
import com.teamzeu.velo.services.EmailService;
import com.teamzeu.velo.enums.OtpType;
import com.teamzeu.velo.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.dto.OtpRequest;
import com.teamzeu.velo.repositories.OtpRepository;
import com.teamzeu.velo.dto.AuthLoginResponse;
import com.teamzeu.velo.dto.AuthResponseDto;
import com.teamzeu.velo.dto.LoginRequestDto;
import com.teamzeu.velo.dto.SignUpRequestDto;
import com.teamzeu.velo.mappers.DtoMappers;
import com.teamzeu.velo.entities.User;
import com.teamzeu.velo.entities.Otp;
import com.teamzeu.velo.repositories.UserRepository;
import com.teamzeu.velo.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final DtoMappers dtoMappers;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final OtpService otpService;

    @Override
    public void signUp(SignUpRequestDto signUpRequestDto) {
        // Verify if user already exists
        if (userRepository.findByEmail(signUpRequestDto.email()).isPresent()) {
            throw new CustomConflictException("User with email " + signUpRequestDto.email() + " already exists");
        }

        // Generate plain OTP
        String plainOtp = String.valueOf(new Random().nextInt(900000) + 100000);

        // Request Otp
        OtpRequest otpRequest = OtpRequest.builder()
                .email(signUpRequestDto.email())
                .otpCode(plainOtp)
                .purpose(OtpType.EMAIL_VERIFICATION)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        // Request Otp
        otpService.createOtp(otpRequest);

        // Create user entity
        User user = dtoMappers.toUSer(signUpRequestDto);

        // Encode password and save user
        user.setPassword(passwordEncoder.encode(signUpRequestDto.password()));
        userRepository.save(user);

        // Send OTP to email using EmailRequest
        HashMap<String, Object> otpTemplateModel = new HashMap<>();
        otpTemplateModel.put("fullName", signUpRequestDto.fullName());
        otpTemplateModel.put("otp", plainOtp);
        otpTemplateModel.put("expiryMinutes", 5);

        EmailRequest otpEmail = EmailRequest.builder()
                .from("noreply@velo.com")
                .to(signUpRequestDto.email())
                .subject("Your OTP for Velo Registration")
                .templateName("email/otp-email.html")
                .templateModel(otpTemplateModel)
                .build();
        emailService.sendOtpEmail(otpEmail);

    }

    @Transactional
    @Override
    public AuthResponseDto verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto) {
        // Confirm if user exists
        User user = userRepository.findByEmail(otpVerificationRequestDto.email())
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        // Confirm if user is verified
        if (user.getIsEmailVerified() == true) {
            throw new CustomBadRequestException("User with this email " + otpVerificationRequestDto.email() + " is already verified");
        }

        // Search for emails
        Otp otp = otpRepository.findByEmailAndPurpose(otpVerificationRequestDto.email(), OtpType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new CustomNotFoundException("OTP does not exist"));

        // Check if OTP has expired
        if (LocalDateTime.now().isAfter(otp.getExpiryTime())) {
            throw new CustomBadRequestException("OTP has expired");
        }

        // Confirm if otp is used
        if (otp.getUsed()) {
            throw new CustomBadRequestException("OTP has already been used");
        }

        // Verify OTP
        if (!passwordEncoder.matches(otpVerificationRequestDto.otp(), otp.getOtp())) {
            throw new CustomBadRequestException("Invalid OTP");
        }

        // Mark OTP as used and verify user's status
        otp.setUsed(true);
        otpRepository.save(otp);
        user.setIsEmailVerified(true);
        userRepository.save(user);

        // Send welcome email using EmailRequest
        HashMap<String, Object> welcomeModel = new HashMap<>();
        welcomeModel.put("fullName", user.getFullName());
        welcomeModel.put("appName", "Velo");
        welcomeModel.put("appUrl", "velo.com");

        EmailRequest welcomeEmail = EmailRequest.builder()
                .to(user.getEmail())
                .subject("Welcome to Velo!")
                .templateName("email/welcome-email.html")
                .templateModel(welcomeModel)
                .build();
        emailService.sendWelcomeEmail(welcomeEmail);

        return dtoMappers.toAuthResponseDto(user);
    }

    @Override
    public AuthLoginResponse login(LoginRequestDto loginRequestDto) {
        return null;
    }

    @Override
    public AuthLoginResponse getAccessToken(String refreshToken) {
        return null;
    }
}
