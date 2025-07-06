package io.petprojects.bookshelfs.conroller;

import io.petprojects.bookshelfs.exception.baseresponse.BaseResponseService;
import io.petprojects.bookshelfs.exception.baseresponse.ResponseWrapper;
import io.petprojects.bookshelfs.model.request.LoginRequest;
import io.petprojects.bookshelfs.model.request.RegisterRequest;
import io.petprojects.bookshelfs.model.response.JwtResponse;
import io.petprojects.bookshelfs.service.secure.AuthService;
import io.petprojects.bookshelfs.service.secure.JwtService;
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

    @Operation(summary = "Зарегистрировать нового пользователя")
    @PostMapping("/register")
    public ResponseWrapper<?> registerUser(
            @RequestBody @Valid RegisterRequest request,
            @RequestParam("g-recaptcha-response") @Valid String captchaResponse
    ) {
        return baseResponseService.wrapSuccessResponse(authService.registerUser(request, captchaResponse));
    }

    @Operation(summary = "Подтвердить почту пользователя")
    @GetMapping("/verify-email")
    public ResponseWrapper<?> verifyEmail(@RequestParam String token) {
        return baseResponseService.wrapSuccessResponse(authService.verifyEmail(token));
    }

    @Operation(summary = "Войти в систему")
    @PostMapping("/login")
    public ResponseWrapper<JwtResponse> authenticateUser(@RequestBody LoginRequest request) {
        return baseResponseService.wrapSuccessResponse(authService.authenticateUser(request));
    }

    @Operation(summary = "Выйти из системы",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping("/logout")
    public ResponseWrapper<?> logoutUser(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        String response = "Не получилось выйти из системы";
        if (token != null) {
            response = authService.logout(token);
        }
        SecurityContextHolder.clearContext();
        return baseResponseService.wrapSuccessResponse(response);
    }
}
