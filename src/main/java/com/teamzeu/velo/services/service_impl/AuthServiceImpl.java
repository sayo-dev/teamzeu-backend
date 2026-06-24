package com.teamzeu.velo.services.service_impl;

import com.teamzeu.velo.exceptions.*;
import com.teamzeu.velo.dto.EmailRequest;
import com.teamzeu.velo.services.EmailService;
import com.teamzeu.velo.enums.OtpType;
import com.teamzeu.velo.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.dto.OtpRequest;
import com.teamzeu.velo.repositories.OtpRepository;
import com.teamzeu.velo.dto.LoginResponse;
import com.teamzeu.velo.dto.AuthResponseDto;
import com.teamzeu.velo.dto.LoginRequestDto;
import com.teamzeu.velo.dto.SignUpRequestDto;
import com.teamzeu.velo.mappers.UserMapper;
import com.teamzeu.velo.entities.User;
import com.teamzeu.velo.entities.Otp;
import com.teamzeu.velo.repositories.UserRepository;
import com.teamzeu.velo.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.teamzeu.velo.dto.AccessTokenRequest;
import com.teamzeu.velo.dto.RequestOtpRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public void signUp(SignUpRequestDto signUpRequestDto) {
        // Verify if user already exists
        if (userRepository.findByEmail(signUpRequestDto.email()).isPresent()) {
            throw new CustomConflictException("User with email " + signUpRequestDto.email() + " already exists");
        }

        // Create user entity
        User user = userMapper.toEntity(signUpRequestDto);

        // Encode password and save user
        user.setPassword(passwordEncoder.encode(signUpRequestDto.password()));
        userRepository.save(user);

        // Generate and send OTP email
        sendVerificationOtp(
                signUpRequestDto.email(),
                signUpRequestDto.fullName(),
                "Your OTP for Velo Registration",
                "email/otp-email.html");
    }

    @Transactional
    @Override
    public void verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto) {
        // Confirm if user exists
        User user = userRepository.findByEmail(otpVerificationRequestDto.email())
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        // Confirm if user is verified
        if (user.getIsEmailVerified() == true) {
            throw new CustomBadRequestException(
                    "User with this email " + otpVerificationRequestDto.email() + " is already verified");
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

    }

    @Override
    public LoginResponse login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.email();

        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomNotFoundException("User with the email " + email + " not found"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        loginRequestDto.password()));

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public LoginResponse getAccessToken(AccessTokenRequest accessTokenRequest) {
        if (!jwtService.isRefreshTokenValid(accessTokenRequest.refreshToken()))
            throw new CustomBadRequestException("Invalid refresh token");

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(jwtService.extractUsername(accessTokenRequest.refreshToken()));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        String newAccessToken = jwtService.generateAccessToken(authenticationToken);
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(accessTokenRequest.refreshToken())
                .build();
    }

    @Override
    public void resendOtp(RequestOtpRequest requestOtpRequest) {
        // Confirm if user exists
        userRepository.findByEmail(requestOtpRequest.email())
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        // Generate and send OTP email
        sendVerificationOtp(
                requestOtpRequest.email(),
                requestOtpRequest.fullName(),
                "OTP request for Velo Registration",
                "email/request-otp-email.html");

    }

    private void sendVerificationOtp(String email, String fullName, String subject, String templateName) {
        String plainOtp = String.valueOf(new Random().nextInt(900000) + 100000);

        OtpRequest otpRequest = OtpRequest.builder()
                .email(email)
                .otpCode(plainOtp)
                .purpose(OtpType.EMAIL_VERIFICATION)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();
        otpService.createOtp(otpRequest);

        HashMap<String, Object> otpTemplateModel = new HashMap<>();
        otpTemplateModel.put("fullName", fullName);
        otpTemplateModel.put("otp", plainOtp);
        otpTemplateModel.put("expiryMinutes", 5);

        EmailRequest otpEmail = EmailRequest.builder()
                .from("noreply@velo.com")
                .to(email)
                .subject(subject)
                .templateName(templateName)
                .templateModel(otpTemplateModel)
                .build();
        emailService.sendOtpEmail(otpEmail);
    }
}
