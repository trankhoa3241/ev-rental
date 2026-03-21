package com.evrental.evrentalsystem.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        log.info("✅ User đăng nhập thành công: {}", username);
        
        // Kiểm tra role của user
        boolean isAdmin = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
        
        String redirectUrl;
        if (isAdmin) {
            redirectUrl = "/admin/dashboard";
            log.info("👑 Admin user {} được redirect đến admin dashboard", username);
        } else {
            redirectUrl = "/";
            log.info("👤 Regular user {} được redirect đến home page", username);
        }
        
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
