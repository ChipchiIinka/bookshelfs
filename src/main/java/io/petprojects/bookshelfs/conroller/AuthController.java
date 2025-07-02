package io.petprojects.bookshelfs.conroller;

import io.petprojects.bookshelfs.exception.baseresponse.BaseResponseService;
import io.petprojects.bookshelfs.exception.baseresponse.ResponseWrapper;
import io.petprojects.bookshelfs.model.JwtResponse;
import io.petprojects.bookshelfs.model.LoginRequest;
import io.petprojects.bookshelfs.model.RegisterRequest;
import io.petprojects.bookshelfs.service.AuthService;
import io.petprojects.bookshelfs.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для регистрации, входа и выхода")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final BaseResponseService baseResponseService;

    @PostMapping("/register")
    public ResponseWrapper<String> registerUser(
            @RequestBody @Valid RegisterRequest request,
            @RequestParam("g-recaptcha-response") @Valid String captchaResponse
    ) {
        return baseResponseService.wrapSuccessResponse(authService.registerUser(request, captchaResponse));
    }

    @GetMapping("/verify-email")
    public ResponseWrapper<String> verifyEmail(@RequestParam String token) {
        return baseResponseService.wrapSuccessResponse(authService.verifyEmail(token));
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    public ResponseWrapper<JwtResponse> authenticateUser(@RequestBody LoginRequest request) {
        return baseResponseService.wrapSuccessResponse(authService.authenticateUser(request));
    }

    @Operation(summary = "Выход из системы",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping("/logout")
    public ResponseWrapper<String> logoutUser(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        String response = "Не получилось выйти из системы";
        if (token != null) {
            response = authService.logout(token);
        }
        SecurityContextHolder.clearContext();
        return baseResponseService.wrapSuccessResponse(response);
    }
}
