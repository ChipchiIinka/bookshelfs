package io.petprojects.bookshelfs.utill;

import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import io.petprojects.bookshelfs.service.secure.AuthService;
import io.petprojects.bookshelfs.service.secure.ReaderDetailsService;
import io.petprojects.bookshelfs.service.secure.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthService authService;
    private final ReaderDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = jwtService.getTokenFromRequest(request);
            if (token != null && authService.isTokenValid(token)) {
                String username = jwtService.getUsernameFromToken(token);
                setAuthentication(username);
            }
        } catch (Exception e) {
            log.error("JWT error: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Добавляем проверку активности аккаунта
        if (!userDetails.isEnabled()) {
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "Аккаунт не активирован");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
