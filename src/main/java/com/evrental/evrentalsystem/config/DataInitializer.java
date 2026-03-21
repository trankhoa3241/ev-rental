package com.evrental.evrentalsystem.config;

import com.evrental.evrentalsystem.entity.User;
import com.evrental.evrentalsystem.entity.Vehicle;
import com.evrental.evrentalsystem.repository.UserRepository;
import com.evrental.evrentalsystem.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository, VehicleRepository vehicleRepository) {
        return args -> {
            // Check if data already exists
            if (userRepository.count() == 0) {
                // Create sample users
                User user1 = new User();
                user1.setFullName("Nguyễn Văn A");
                user1.setEmail("nguyenvana@email.com");
                user1.setPassword(passwordEncoder.encode("password123"));
                user1.setPhoneNumber("0901234567");
                user1.setLicenseNumber("ABC123456");
                user1.setIdCardNumber("123456789");
                user1.setAddress("123 Đường Nguyễn Hữu Cảnh, Quận Bình Thạnh, TP. HCM");
                user1.setIsActive(true);
                user1.setOauthProvider("LOCAL");

                User user2 = new User();
                user2.setFullName("Trần Thị B");
                user2.setEmail("tranthib@email.com");
                user2.setPassword(passwordEncoder.encode("password123"));
                user2.setPhoneNumber("0902345678");
                user2.setLicenseNumber("DEF789012");
                user2.setIdCardNumber("987654321");
                user2.setAddress("456 Đường Lê Hồng Phong, Quận 5, TP. HCM");
                user2.setIsActive(true);
                user2.setOauthProvider("LOCAL");
                user2.setRole("USER");

                // Create admin user
                User adminUser = new User();
                adminUser.setFullName("Admin System");
                adminUser.setEmail("admin@evrental.com");
                adminUser.setPassword(passwordEncoder.encode("admin123456"));
                adminUser.setPhoneNumber("0900000000");
                adminUser.setLicenseNumber("ADMIN001");
                adminUser.setIdCardNumber("000000001");
                adminUser.setAddress("Admin Office, TP. HCM");
                adminUser.setIsActive(true);
                adminUser.setOauthProvider("LOCAL");
                adminUser.setRole("ADMIN");

                userRepository.saveAll(Arrays.asList(user1, user2, adminUser));
                System.out.println("✅ Sample users created successfully!");
            }

            if (vehicleRepository.count() == 0) {
                // Create sample vehicles với hình ảnh từ công khai
                Vehicle vehicle1 = new Vehicle();
                vehicle1.setBrand("Tesla");
                vehicle1.setModel("Model 3");
                vehicle1.setVehicleType("CAR");
                vehicle1.setLicensePlate("ABC123");
                vehicle1.setBatteryCapacity(85);
                vehicle1.setMaxRange(500);
                vehicle1.setPricePerHour(new BigDecimal("250000"));
                vehicle1.setPricePerDay(new BigDecimal("2000000"));
                vehicle1.setCurrentCharge(85);
                vehicle1.setCurrentLocation("123 Đường Nguyễn Hữu Cảnh");
                vehicle1.setImageUrl("https://images.unsplash.com/photo-1560958089-b8a63019b29c?w=500&h=400&fit=crop");
                vehicle1.setDescription("Xe Tesla Model 3 sang trọng và hiện đại, phù hợp cho các chuyến đi ngắn hoặc dài");
                vehicle1.setIsAvailable(true);
                vehicle1.setRatings(4.8);
                vehicle1.setTotalReviews(120);

                Vehicle vehicle2 = new Vehicle();
                vehicle2.setBrand("Honda");
                vehicle2.setModel("Vision");
                vehicle2.setVehicleType("MOTORCYCLE");
                vehicle2.setLicensePlate("DEF456");
                vehicle2.setBatteryCapacity(3);
                vehicle2.setMaxRange(150);
                vehicle2.setPricePerHour(new BigDecimal("50000"));
                vehicle2.setPricePerDay(new BigDecimal("300000"));
                vehicle2.setCurrentCharge(90);
                vehicle2.setCurrentLocation("456 Đường Lê Hồng Phong");
                vehicle2.setImageUrl("https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500&h=400&fit=crop");
                vehicle2.setDescription("Xe máy Honda Vision điện, tiết kiệm xăng, tiện lợi di chuyển trong thành phố");
                vehicle2.setIsAvailable(true);
                vehicle2.setRatings(4.5);
                vehicle2.setTotalReviews(95);

                Vehicle vehicle3 = new Vehicle();
                vehicle3.setBrand("Vespa");
                vehicle3.setModel("S");
                vehicle3.setVehicleType("MOTORCYCLE");
                vehicle3.setLicensePlate("GHI789");
                vehicle3.setBatteryCapacity(5);
                vehicle3.setMaxRange(200);
                vehicle3.setPricePerHour(new BigDecimal("80000"));
                vehicle3.setPricePerDay(new BigDecimal("500000"));
                vehicle3.setCurrentCharge(75);
                vehicle3.setCurrentLocation("789 Đường Trần Hung Dao");
                vehicle3.setImageUrl("https://images.unsplash.com/photo-1606611283424-430f63602df0?w=500&h=400&fit=crop");
                vehicle3.setDescription("Xe máy Vespa S cao cấp, kiểu dáng cổ điển, hiệu suất mạnh mẽ");
                vehicle3.setIsAvailable(true);
                vehicle3.setRatings(4.9);
                vehicle3.setTotalReviews(210);

                vehicleRepository.saveAll(Arrays.asList(vehicle1, vehicle2, vehicle3));
                System.out.println("✅ Sample vehicles created successfully!");
            }

            System.out.println("✅ Data initialization completed!");
        };
    }
}
