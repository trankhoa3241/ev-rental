package com.evrental.evrentalsystem.controller;

import com.evrental.evrentalsystem.entity.PasswordResetToken;
import com.evrental.evrentalsystem.entity.User;
import com.evrental.evrentalsystem.repository.PasswordResetTokenRepository;
import com.evrental.evrentalsystem.repository.UserRepository;
import com.evrental.evrentalsystem.service.EmailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("errorMessage", "Email này đã được đăng ký trước đó");
            return "auth/register";
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setOauthProvider("LOCAL");
        
        // Khởi tạo các fields mặc định
        user.setRole("USER");
        user.setIsActive(true);
        user.setProfileImage("https://via.placeholder.com/150?text=Avatar");
        user.setLicenseNumber("");
        user.setIdCardNumber(user.getIdCardNumber() != null ? user.getIdCardNumber() : "");
        user.setAddress(user.getAddress() != null ? user.getAddress() : "");
        user.setPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Send welcome email (optional)
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
        } catch (Exception e) {
            log.warn("Failed to send welcome email: {}", e.getMessage());
        }
        
        return "redirect:/auth/login?registered=true";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(String email, Model model) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Generate password reset token
                String token = UUID.randomUUID().toString();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiresAt = now.plusHours(24); // Token expires in 24 hours
                
                PasswordResetToken resetToken = new PasswordResetToken();
                resetToken.setToken(token);
                resetToken.setUserId(user.getId());
                resetToken.setUserEmail(user.getEmail());
                resetToken.setCreatedAt(now);
                resetToken.setExpiresAt(expiresAt);
                resetToken.setUsed(false);
                
                passwordResetTokenRepository.save(resetToken);
                
                // Build reset link
                String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
                
                // Send email
                emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetLink);
                
                log.info("Password reset link sent to: {}", email);
            } else {
                log.info("Password reset requested for non-existent email: {}", email);
            }
            
            // Always show success message for security (don't reveal if email exists)
            model.addAttribute("message", "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được một email với link đặt lại mật khẩu");
            return "auth/forgot-password";
        } catch (Exception e) {
            log.error("Error processing forgot password request: ", e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword(String token, Model model) {
        try {
            if (token == null || token.isEmpty()) {
                model.addAttribute("errorMessage", "Link không hợp lệ");
                return "auth/reset-password";
            }
            
            Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
            
            if (resetTokenOptional.isPresent()) {
                PasswordResetToken resetToken = resetTokenOptional.get();
                
                // Check if token is valid
                if (!resetToken.isValid()) {
                    model.addAttribute("errorMessage", "Link đã hết hạn hoặc không hợp lệ. Vui lòng yêu cầu link mới.");
                    return "auth/reset-password";
                }
                
                model.addAttribute("token", token);
                return "auth/reset-password";
            } else {
                model.addAttribute("errorMessage", "Link không hợp lệ");
                return "auth/reset-password";
            }
        } catch (Exception e) {
            log.error("Error validating reset token: ", e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            return "auth/reset-password";
        }
    }

    @PostMapping("/reset-password")
    public String processReset(String token, String password, String confirmPassword, Model model) {
        try {
            // Validate passwords match
            if (password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
                model.addAttribute("errorMessage", "Mật khẩu không được để trống");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }
            
            if (!password.equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Mật khẩu không khớp");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }
            
            // Validate password length
            if (password.length() < 6) {
                model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }
            
            // Find and validate reset token
            Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
            
            if (resetTokenOptional.isEmpty()) {
                model.addAttribute("errorMessage", "Link không hợp lệ");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }
            
            PasswordResetToken resetToken = resetTokenOptional.get();
            
            if (!resetToken.isValid()) {
                model.addAttribute("errorMessage", "Link đã hết hạn hoặc đã được sử dụng. Vui lòng yêu cầu link mới.");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }
            
            // Find user
            Optional<User> userOptional = userRepository.findById(resetToken.getUserId());
            
            if (userOptional.isEmpty()) {
                model.addAttribute("errorMessage", "Không tìm thấy người dùng");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }
            
            // Update password
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(password));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Mark token as used
            resetToken.setUsed(true);
            passwordResetTokenRepository.save(resetToken);
            
            log.info("Password reset successful for user: {}", user.getEmail());
            return "redirect:/auth/login?reset=success";
        } catch (Exception e) {
            log.error("Error processing password reset: ", e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại.");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
    }
}
