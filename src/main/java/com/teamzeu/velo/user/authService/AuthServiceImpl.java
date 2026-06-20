package com.teamzeu.velo.user.authService;

import com.teamzeu.velo.common.exceptions.*;
import com.teamzeu.velo.common.email.EmailModel;
import com.teamzeu.velo.common.email.EmailService;
import com.teamzeu.velo.common.enums.OtpType;
import com.teamzeu.velo.otp.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.otp.model.OtpRequest;
import com.teamzeu.velo.otp.repository.OtpRepository;
import com.teamzeu.velo.otp.service.OtpService;
import com.teamzeu.velo.user.dto.AuthLoginResponse;
import com.teamzeu.velo.user.dto.AuthResponseDto;
import com.teamzeu.velo.user.dto.LoginRequestDto;
import com.teamzeu.velo.user.dto.SignUpRequestDto;
import com.teamzeu.velo.user.mapper.DtoMappers;
import com.teamzeu.velo.user.model.User;
import com.teamzeu.velo.otp.model.Otp;
import com.teamzeu.velo.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthServiceInterface {
    private final UserRepository userRepository;
    private final DtoMappers dtoMappers;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final OtpService otpService;


    @Override
    public AuthResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        // Verify is user already exist
        if (userRepository.findByEmail(signUpRequestDto.email()).isPresent()) {
            throw new UserAlreadyExistException("User with email " + signUpRequestDto.email() + " already exists");
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

        // Send OTP to email using EmailModel
        HashMap<String, Object> otpTemplateModel = new HashMap<>();
        otpTemplateModel.put("fullName", signUpRequestDto.fullName());
        otpTemplateModel.put("otp", plainOtp);
        otpTemplateModel.put("expiryMinutes", 5);

        EmailModel otpEmail = EmailModel.builder()
                .from("noreply@teamzeu.com")
                .to(signUpRequestDto.email())
                .subject("Your OTP for Velo Registration")
                .templateName("email/otp-email.html")
                .templateModel(otpTemplateModel)
                .build();
        emailService.sendOtpEmail(otpEmail);

        // Return response DTO
        return dtoMappers.toAuthResponseDto(user);
    }

    @Transactional
    @Override
    public AuthResponseDto verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto) {
        //Confirm if user exists
        User user = userRepository.findByEmail(otpVerificationRequestDto.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        //Confirm is user is verified
        if(user.getIsEmailVerified() == true){
            throw  new UserAlreadyVerified("User with this email " + otpVerificationRequestDto.email() + " is already verified");
        }

        //Search for emails
        Otp otp = otpRepository.findByEmailAndPurpose(otpVerificationRequestDto.email(), OtpType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new InvalidOtpException("OTP does not exist"));

        // Check if OTP has expired
        if (LocalDateTime.now().isAfter(otp.getExpiryTime())) {
            throw new OtpExpiredException("OTP has expired");
        }

        // Confirm is otp is used
        if (otp.getUsed()) {
            throw new OtpAlreadyUsedException("OTP has already been used");
        }

        // Verify OTP
        if (!passwordEncoder.matches(otpVerificationRequestDto.otp(), otp.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // Mark OTP as used and verify user's status
        otp.setUsed(true);
        otpRepository.save(otp);
        user.setIsEmailVerified(true);
        userRepository.save(user);

        // Send welcome email using EmailModel
        HashMap<String, Object> welcomeModel = new HashMap<>();
        welcomeModel.put("fullName", user.getFullName());
        welcomeModel.put("appName", "Velo");
        welcomeModel.put("appUrl", "velo.com");

        EmailModel welcomeEmail = EmailModel.builder()
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
