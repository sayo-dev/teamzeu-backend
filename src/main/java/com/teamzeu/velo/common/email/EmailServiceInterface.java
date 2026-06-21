package com.teamzeu.velo.common.email;

public interface EmailServiceInterface {
    void sendOtpEmail(EmailModel emailModel);
    void sendPasswordResetEmail(EmailModel emailModel);
    void sendWelcomeEmail(EmailModel emailModel);
}

