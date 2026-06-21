package com.teamzeu.velo.common.email;

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
public class EmailService implements EmailServiceInterface {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Override
    @Async
    public void sendOtpEmail(EmailModel emailModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariables(emailModel.getTemplateModel());


            // Process template
            String htmlContent = templateEngine.process(emailModel.getTemplateName(), context);

            // Set email details
            helper.setFrom(emailModel.getFrom());
            helper.setTo(emailModel.getTo());
            helper.setSubject(emailModel.getSubject());
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email to " + emailModel.getTo(), e);
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(EmailModel emailModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("fullName", emailModel.getTemplateModel().get("fullName"));
            context.setVariable("resetToken", emailModel.getTemplateModel().get("resetToken"));
            context.setVariable("resetLink", "https://velo/reset-password?token=" + emailModel.getTemplateModel().get("resetToken"));
            context.setVariable("expiryMinutes", 30);

            // Process template
            String htmlContent = templateEngine.process(emailModel.getTemplateName(), context);

            // Set email details
            helper.setFrom(emailModel.getFrom());
            helper.setTo(emailModel.getTo());
            helper.setSubject("Reset Your Velo Password");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email to " + emailModel.getTo(), e);
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(EmailModel emailModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("fullName", emailModel.getTemplateModel().get("fullName"));
            context.setVariable("appName", "Velo");
            context.setVariable("appUrl", "https://velo.com");

            // Process template
            String htmlContent = templateEngine.process(emailModel.getTemplateName(), context);

            // Set email details
            helper.setFrom(emailModel.getFrom());
            helper.setTo(emailModel.getTo());
            helper.setSubject("Welcome to Velo!");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send welcome email to " + emailModel.getTo(), e);
        }
    }
}



