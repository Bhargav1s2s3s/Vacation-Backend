package com.vacation.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${password-reset.reset-url}")
    private String resetUrl;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = resetUrl + "/" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Vacation App — Reset Your Password");
        message.setText(
                "Hi,\n\n"
                + "You requested to reset your password.\n\n"
                + "Click the link below to reset your password:\n"
                + resetLink + "\n\n"
                + "This link will expire in 15 minutes.\n\n"
                + "If you didn't request this, please ignore this email.\n\n"
                + "— Vacation App Team"
        );

        mailSender.send(message);
        log.info("Password reset email sent to: {}", toEmail);
    }
}
