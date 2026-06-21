package com.teamzeu.velo.otp.repository;

import com.teamzeu.velo.common.enums.OtpType;
import com.teamzeu.velo.otp.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Optional<Otp> findByEmailAndPurpose(String email, OtpType purpose);
    void deleteAllByEmailAndPurpose(String email, OtpType purpose);
}


