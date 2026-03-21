package com.evrental.evrentalsystem.config;

import com.evrental.evrentalsystem.service.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AppInitializer {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Bean
    public CommandLineRunner initializeApp() {
        return args -> {
            log.info("\n" +
                    "╔═══════════════════════════════════════════╗\n" +
                    "║   EV RENTAL SYSTEM - INITIALIZING...      ║\n" +
                    "╚═══════════════════════════════════════════╝\n");
            
            // Test Cloudinary connection
            log.info("🔍 Testing Cloudinary connection...");
            try {
                cloudinaryService.testConnection();
                log.info("✅ Cloudinary connection successful!\n");
            } catch (Exception e) {
                log.error("❌ Cloudinary connection failed: {}\n", e.getMessage());
            }
            
            log.info("╔═══════════════════════════════════════════╗\n" +
                    "║   ✅ App initialized successfully!        ║\n" +
                    "║   🌐 Server running on: http://localhost:8080\n" +
                    "║   📊 Admin Dashboard: /admin/dashboard\n" +
                    "╚═══════════════════════════════════════════╝\n");
        };
    }
}
