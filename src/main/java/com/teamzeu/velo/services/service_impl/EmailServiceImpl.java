package com.teamzeu.velo.services.service_impl;

import com.teamzeu.velo.dto.EmailRequest;
import com.teamzeu.velo.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendOtpEmail(EmailRequest emailRequest) {
        Context context = new Context();
        context.setVariables(emailRequest.getTemplateModel());
        sendEmail(emailRequest, emailRequest.getSubject(), context);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(EmailRequest emailRequest) {
        Context context = new Context();
        context.setVariable("fullName", emailRequest.getTemplateModel().get("fullName"));
        context.setVariable("resetToken", emailRequest.getTemplateModel().get("resetToken"));
        context.setVariable("resetLink", "https://velo/reset-password?token=" + emailRequest.getTemplateModel().get("resetToken"));
        context.setVariable("expiryMinutes", 30);
        sendEmail(emailRequest, "Reset Your Velo Password", context);
    }

    @Override
    @Async
    public void sendWelcomeEmail(EmailRequest emailRequest) {
        Context context = new Context();
        context.setVariable("fullName", emailRequest.getTemplateModel().get("fullName"));
        context.setVariable("appName", "Velo");
        context.setVariable("appUrl", "https://velo.com");
        sendEmail(emailRequest, "Welcome to Velo!", context);
    }

    private void sendEmail(EmailRequest emailRequest, String subject, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = templateEngine.process(emailRequest.getTemplateName(), context);

            String from = emailRequest.getFrom() != null ? emailRequest.getFrom() : "noreply@teamzeu.com";
            helper.setFrom(from);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + emailRequest.getTo(), e);
        }
    }
}
