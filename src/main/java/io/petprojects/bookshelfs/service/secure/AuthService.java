package io.petprojects.bookshelfs.service.secure;

import io.petprojects.bookshelfs.entity.JwtBlacklistEntity;
import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import io.petprojects.bookshelfs.model.response.JwtResponse;
import io.petprojects.bookshelfs.model.request.LoginRequest;
import io.petprojects.bookshelfs.model.request.RegisterRequest;
import io.petprojects.bookshelfs.repository.JwtBlacklistRepository;
import io.petprojects.bookshelfs.repository.ReaderRepository;
import io.petprojects.bookshelfs.service.mapper.ReaderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private static final int DAYS_OF_VERIFICATION_CODE_EXPIRATION = 1;
    private static final String EMAIL_SENT_MESSAGE = "Письмо с подтверждением отправлено на %s";
    private static final String EMAIL_SUCCESSFULLY_VERIFIED = "Email успешно подтверждён. Аккаунт активирован.";
    private static final String SUCCESSFULLY_LOGOUT = "Вы успешно вышли из системы.";

    private final ReaderRepository userRepository;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final ReaderMapper readerMapper;

    public String registerUser(RegisterRequest request, String captchaResponse) {
        if(!captchaService.verifyCaptcha(captchaResponse)){
            throw new BookshelfsException(ErrorType.CLIENT_ERROR,
                    "Подтвердите что вы не робот и продолжите регистрацию");
        }
        validateUserNotExists(request.getUsername(), request.getEmail());

        String verificationCode = UUID.randomUUID().toString();
        LocalDateTime verificationCodeExpiry = LocalDateTime.now().plusDays(DAYS_OF_VERIFICATION_CODE_EXPIRATION);
        ReaderEntity user = readerMapper.toEntity(request, verificationCode, verificationCodeExpiry, false);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBookCount(0);
        userRepository.save(user);
        emailService.sendVerificationEmail(request.getEmail(), verificationCode);
        return String.format(EMAIL_SENT_MESSAGE, request.getEmail());
    }

    public String verifyEmail(String token) {
        ReaderEntity user = userRepository.findByVerificationCode(token)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND, "Неверный токен подтверждения"));
        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "Срок действия ссылки истёк");
        }
        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepository.save(user);
        return EMAIL_SUCCESSFULLY_VERIFIED;
    }

    public JwtResponse authenticateUser(LoginRequest request) {
        ReaderEntity user = userRepository.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new BookshelfsException(ErrorType.CLIENT_ERROR, "Пользователь не найден"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "Неверные учетные данные");
        }
        if (!user.isEnabled()) {
            throw new BookshelfsException(ErrorType.CLIENT_ERROR,
                    "Аккаунт не активирован. Проверьте email для подтверждения.");
        }
        String jwt = jwtService.generateToken(user.getUsername());
        return JwtResponse.builder()
                .token(jwt)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public String logout(String token) {
        JwtBlacklistEntity blacklistedToken = new JwtBlacklistEntity();
        blacklistedToken.setId(token);
        jwtBlacklistRepository.save(blacklistedToken);
        return SUCCESSFULLY_LOGOUT;
    }

    public boolean isTokenValid(String token) {
        return jwtService.validateToken(token) && !jwtBlacklistRepository.existsById(token);
    }

    private void validateUserNotExists(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "Логин уже занят");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "Email уже занят");
        }
    }
}
