package com.evrental.evrentalsystem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@evrental.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:8080}")
    private String frontendUrl;

    /**
     * Gửi email đặt lại mật khẩu
     */
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("EV Rental System - Đặt lại Mật khẩu");

            String htmlContent = buildPasswordResetEmailHtml(fullName, resetLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }

    /**
     * Gửi email xác nhận đăng ký
     */
    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Chào mừng đến EV Rental System!");
            message.setText(String.format(
                "Chào %s,\n\n" +
                "Cảm ơn bạn đã đăng ký tài khoản trên EV Rental System!\n" +
                "Bạn có thể đăng nhập ngay bằng email và mật khẩu của bạn.\n\n" +
                "Trân trọng,\n" +
                "EV Rental System Team",
                fullName
            ));
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Xây dựng nội dung HTML cho email đặt lại mật khẩu
     */
    private String buildPasswordResetEmailHtml(String fullName, String resetLink) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #ecf0f1; padding: 20px; border-radius: 0 0 5px 5px; }
                    .button { background-color: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                    .footer { margin-top: 20px; font-size: 12px; color: #7f8c8d; text-align: center; }
                    .warning { color: #e74c3c; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>EV Rental System</h1>
                        <p>Đặt lại mật khẩu</p>
                    </div>
                    <div class="content">
                        <p>Xin chào <strong>%s</strong>,</p>
                        <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Nhấp vào nút dưới để đặt lại mật khẩu:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Đặt lại Mật khẩu</a>
                        </p>
                        <p><span class="warning">⚠️ Lưu ý:</span> Link này sẽ hết hạn sau <strong>24 giờ</strong>. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
                        <p>Hoặc sao chép link này vào trình duyệt:</p>
                        <p style="word-break: break-all; background-color: #fff; padding: 10px; border-left: 3px solid #3498db;">%s</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 EV Rental System. All rights reserved.</p>
                        <p>Nếu bạn có câu hỏi, vui lòng liên hệ với chúng tôi.</p>
                    </div>
                </div>
            </body>
            </html>
            """, fullName, resetLink, resetLink);
    }
}
