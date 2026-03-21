package com.evrental.evrentalsystem.config;

import com.evrental.evrentalsystem.entity.User;
import com.evrental.evrentalsystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        try {
            log.info("🔐 [OAuth2SuccessHandler] OAuth2 authentication success handler called");
            
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            
            log.info("✅ [OAuth2SuccessHandler] OAuth2 user authenticated: {} ({})", email, name);
            log.info("📋 [OAuth2SuccessHandler] Authorities: {}", authentication.getAuthorities());
            
            // Get user from database to check role
            Optional<User> userOptional = userRepository.findByEmail(email);
            String redirectUrl = "/"; // Default to home page
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                log.info("👤 [OAuth2SuccessHandler] User found in DB - Email: {}, Role: {}", email, user.getRole());
                
                // Redirect based on role for admin
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    redirectUrl = "/admin/dashboard";
                    log.info("👑 [OAuth2SuccessHandler] Admin user detected, redirecting to: {}", redirectUrl);
                } else {
                    redirectUrl = "/";
                    log.info("👤 [OAuth2SuccessHandler] Regular user detected, redirecting to: {}", redirectUrl);
                }
            } else {
                log.warn("⚠️ [OAuth2SuccessHandler] User not found in DB for email: {}", email);
                redirectUrl = "/";
            }
            
            log.info("🔄 [OAuth2SuccessHandler] Redirecting to: {}", redirectUrl);
            response.sendRedirect(request.getContextPath() + redirectUrl);
            
        } catch (Exception ex) {
            log.error("❌ [OAuth2SuccessHandler] Error in OAuth2 success handler: {}", ex.getMessage(), ex);
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
