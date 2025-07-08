package io.petprojects.bookshelfs.service.secure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(String email, String verificationToken) {
        String verificationUrl = baseUrl + "/auth/verify-email?token=" + verificationToken;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Подтверждение регистрации");
        mail.setText("Для активации аккаунта перейдите по ссылке: " + verificationUrl);
        mailSender.send(mail);
    }

    public void sendNotifyEmail(String email, String subject, String body) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(subject);
        mail.setText(body);
        mailSender.send(mail);
    }
}
