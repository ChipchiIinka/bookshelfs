package io.petprojects.bookshelfs.conroller.mvc;

import io.petprojects.bookshelfs.model.request.LoginRequest;
import io.petprojects.bookshelfs.model.request.RegisterRequest;
import io.petprojects.bookshelfs.model.response.JwtResponse;
import io.petprojects.bookshelfs.service.secure.AuthService;
import io.petprojects.bookshelfs.service.secure.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MvcAuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Operation(hidden = true)
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerRequest", RegisterRequest.builder().build());
        return "auth/register";
    }

    @Operation(hidden = true)
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute @Validated RegisterRequest registerRequest,
            BindingResult bindingResult,
            @RequestParam("h-captcha-response") String captchaResponse,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Пожалуйста, исправьте ошибки в форме.");
            return "auth/register";
        }
        try {
            Object response = authService.registerUser(registerRequest, captchaResponse);
            model.addAttribute("success", "Регистрация успешна! Проверьте почту для подтверждения.");
            return "auth/register";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при регистрации: " + e.getMessage());
            return "auth/register";
        }
    }

    @Operation(hidden = true)
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, Model model) {
        try {
            Object response = authService.verifyEmail(token);
            model.addAttribute("success", "Почта успешно подтверждена!");
            return "auth/verify-email";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при подтверждении почты: " + e.getMessage());
            return "auth/verify-email";
        }
    }

    @Operation(hidden = true)
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", LoginRequest.builder().build());
        return "auth/login";
    }

    @Operation(hidden = true)
    @PostMapping("/login")
    public String authenticateUser(
            @ModelAttribute @Validated LoginRequest loginRequest,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Пожалуйста, исправьте ошибки в форме.");
            return "auth/login";
        }
        try {
            JwtResponse response = authService.authenticateUser(loginRequest);
            model.addAttribute("success", "Вход успешен! Токен: " + response.getToken());
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при входе: " + e.getMessage());
            return "auth/login";
        }
    }

    @Operation(hidden = true)
    @PostMapping("/logout")
    public String logoutUser(HttpServletRequest request, Model model) {
        String token = jwtService.getTokenFromRequest(request);
        String response = "Не получилось выйти из системы";
        if (token != null) {
            response = authService.logout(token);
        }
        SecurityContextHolder.clearContext();
        model.addAttribute("success", response);
        return "redirect:/auth/login";
    }
}
