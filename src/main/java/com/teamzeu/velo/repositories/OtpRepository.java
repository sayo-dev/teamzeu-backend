package com.teamzeu.velo.repositories;

import com.teamzeu.velo.enums.OtpType;
import com.teamzeu.velo.entities.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Optional<Otp> findByEmailAndPurpose(String email, OtpType purpose);
    void deleteAllByEmailAndPurpose(String email, OtpType purpose);
}
