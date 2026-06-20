package com.teamzeu.velo.otp.service;

import com.teamzeu.velo.common.enums.OtpType;
import com.teamzeu.velo.otp.model.Otp;
import com.teamzeu.velo.otp.model.OtpRequest;
import com.teamzeu.velo.otp.repository.OtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createOtp(OtpRequest otpRequest) {
        otpRepository.deleteAllByEmailAndPurpose(
                otpRequest.email(),
                otpRequest.purpose()
        );
        Otp otp = Otp.builder()
                .email(otpRequest.email())
                .otp(passwordEncoder.encode(otpRequest.otpCode()))
                .used(false)
                .expiryTime(otpRequest.expiryTime())
                .purpose(otpRequest.purpose())
                .build();

        otpRepository.save(otp);
    }

    public Optional<Otp> findByEmailAndPurpose(String email, OtpType purpose) {
        return otpRepository.findByEmailAndPurpose(email, purpose);
    }
}
