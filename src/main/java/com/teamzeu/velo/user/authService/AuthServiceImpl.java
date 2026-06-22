package com.teamzeu.velo.user.authService;

import com.teamzeu.velo.common.exceptions.*;
import com.teamzeu.velo.common.email.EmailModel;
import com.teamzeu.velo.common.email.EmailService;
import com.teamzeu.velo.common.enums.OtpType;
import com.teamzeu.velo.jwt.CustomUserDetailsService;
import com.teamzeu.velo.jwt.JWTService;
import com.teamzeu.velo.otp.dto.OtpVerificationRequestDto;
import com.teamzeu.velo.otp.model.OtpRequest;
import com.teamzeu.velo.otp.repository.OtpRepository;
import com.teamzeu.velo.otp.service.OtpService;
import com.teamzeu.velo.user.dto.*;
import com.teamzeu.velo.user.mapper.DtoMappers;
import com.teamzeu.velo.user.model.User;
import com.teamzeu.velo.otp.model.Otp;
import com.teamzeu.velo.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final CustomUserDetailsService myUserDetailsService;


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

        //Search for otp
        Otp otp = otpService.findByEmailAndPurpose(
                otpVerificationRequestDto.email(),
                OtpType.EMAIL_VERIFICATION
        ).orElseThrow(() -> new InvalidOtpException("OTP does not exist"));

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
                .from("noreply@velo.com")
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

        String email = loginRequestDto.email();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User with the email " + loginRequestDto.email() + " not found")));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(),
                        loginRequestDto.password()
                )
        );

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return AuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthLoginResponse getAccessToken(AccessTokenRequest accessTokenRequest) {
        if (!jwtService.isRefreshTokenValid(accessTokenRequest.refreshToken()))
            throw new RuntimeException("Invalid refresh token");

        UserDetails userDetails = myUserDetailsService.loadUserByUsername(jwtService.extractUsername(accessTokenRequest.refreshToken()));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        String newAccessToken = jwtService.generateAccessToken(authenticationToken);
        return new AuthLoginResponse(newAccessToken, accessTokenRequest.refreshToken());
    }

    @Override
    public AuthResponseDto requestOtp(RequestOtpRequest requestOtpRequest) {
        //Confirm if user exists
        User user = userRepository.findByEmail(requestOtpRequest.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Generate plain OTP
        String plainOtp = String.valueOf(new Random().nextInt(900000) + 100000);

        // Request Otp
        OtpRequest otpRequest = OtpRequest.builder()
                .email(requestOtpRequest.email())
                .otpCode(plainOtp)
                .purpose(OtpType.EMAIL_VERIFICATION)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        // Request Otp
        otpService.createOtp(otpRequest);


        // Send OTP to email using EmailModel
        HashMap<String, Object> otpTemplateModel = new HashMap<>();
        otpTemplateModel.put("fullName", requestOtpRequest.fullName());
        otpTemplateModel.put("otp", plainOtp);
        otpTemplateModel.put("expiryMinutes", 5);

        EmailModel otpEmail = EmailModel.builder()
                .from("noreply@teamzeu.com")
                .to(requestOtpRequest.email())
                .subject("OTP request for Velo Registration")
                .templateName("email/request-otp-email.html")
                .templateModel(otpTemplateModel)
                .build();
        emailService.sendOtpEmail(otpEmail);


        return dtoMappers.toAuthResponseDto(user);

    }


}
