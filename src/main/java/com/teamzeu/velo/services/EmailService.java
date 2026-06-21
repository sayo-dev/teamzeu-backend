package com.teamzeu.velo.services;

import com.teamzeu.velo.dto.EmailRequest;

public interface EmailService {
    void sendOtpEmail(EmailRequest emailRequest);
    void sendPasswordResetEmail(EmailRequest emailRequest);
    void sendWelcomeEmail(EmailRequest emailRequest);
}
