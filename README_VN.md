# EV Rental System - Web Cho Thuê Xe Điện

## 📋 Mô Tả Dự Án

EV Rental System là một nền tảng web hiện đại dành cho việc cho thuê xe điện (Electric Vehicle). Dự án tập trung giải quyết các vấn đề:

- **Thúc đẩy di chuyển xanh**: Giảm phát thải carbon, bảo vệ môi trường
- **Tối ưu chi phí & tiện lợi**: Giải pháp di chuyển ngắn hạn mà không cần sở hữu xe
- **Quản lý vận hành hiện đại**: Hệ thống số hóa toàn bộ quy trình
- **Minh bạch thông tin**: Cung cấp đầy đủ thông số kỹ thuật và giá cả

## 🎯 Tính Năng Chính

### Dành Cho Người Dùng:
- ✅ Tìm kiếm & lọc xe nâng cao (loại, hãng, giá, pin, quãng đường)
- ✅ Đặt xe & thanh toán trực tuyến
- ✅ Bản đồ trạm sạc & cửa hàng
- ✅ Hệ thống đánh giá & feedback
- ✅ Quản lý hồ sơ & lịch sử thuê xe

### Pages Đã Tạo:

#### 🏠 Trang Công Cộng (Không cần đăng nhập)
1. **Trang Chủ** (`/`) - Hero section, xe nổi bật, tính năng, CTA
2. **Danh Sách Xe** (`/vehicles`) - Lọc nâng cao, phân trang
3. **Chi Tiết Xe** (`/vehicles/{id}`) - Thông tin chi tiết, đánh giá
4. **Về Chúng Tôi** (`/about`) - Sứ mệnh, tầm nhìn, giá trị cốt lõi
5. **Liên Hệ** (`/contact`) - Form liên hệ, thông tin, FAQ

#### 🔐 Trang Authentication
1. **Đăng Nhập** (`/auth/login`) - Email/Password, OAuth2 Google
2. **Đăng Ký** (`/auth/register`) - Form validation, kiểm tra email
3. **Quên Mật Khẩu** (`/auth/forgot-password`) - Gửi reset link
4. **Đặt Lại Mật Khẩu** (`/auth/reset-password`) - Tạo mật khẩu mới

#### 👤 Trang Người Dùng (Cần đăng nhập)
1. **Dashboard** (`/dashboard`) - Tổng quan, thống kê
2. **Hồ Sơ** (`/profile`) - Chỉnh sửa thông tin, tài liệu xác minh
3. **Lịch Sử Chuyến Đi** (`/my-rentals`) - Quản lý chuyến đi

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Java 17** - Ngôn ngữ lập trình
- **Spring Boot 3.5.11** - Framework chính
- **Spring Data JPA** - ORM, quản lý dữ liệu
- **Spring Security** - Authentication & Authorization
- **OAuth2** - Đăng nhập Google
- **MySQL** - Cơ sở dữ liệu

### Frontend
- **Thymeleaf** - Template engine
- **Bootstrap 5** - CSS framework
- **Font Awesome** - Icons
- **JavaScript** - Client-side logic

## 📁 Cấu Trúc Dự Án

```
ev-rental-system/
├── src/
│   ├── main/
│   │   ├── java/com/evrental/evrentalsystem/
│   │   │   ├── controller/          # Spring Controllers
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   └── NavigationController.java
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Vehicle.java
│   │   │   │   ├── Rental.java
│   │   │   │   └── Review.java
│   │   │   ├── repository/          # Data Access Layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── VehicleRepository.java
│   │   │   │   ├── RentalRepository.java
│   │   │   │   └── ReviewRepository.java
│   │   │   ├── config/              # Spring Configurations
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── PasswordEncoderConfig.java
│   │   │   └── EvRentalSystemApplication.java
│   │   ├── resources/
│   │   │   ├── templates/           # Thymeleaf HTML templates
│   │   │   │   ├── index.html       # Trang chủ
│   │   │   │   ├── about.html
│   │   │   │   ├── contact.html
│   │   │   │   ├── layout.html      # Base layout
│   │   │   │   ├── auth/
│   │   │   │   │   ├── login.html
│   │   │   │   │   ├── register.html
│   │   │   │   │   ├── forgot-password.html
│   │   │   │   │   └── reset-password.html
│   │   │   │   ├── vehicles/
│   │   │   │   │   ├── list.html
│   │   │   │   │   ├── detail.html
│   │   │   │   │   └── search-results.html
│   │   │   │   └── user/
│   │   │   │       ├── dashboard.html
│   │   │   │       ├── profile.html
│   │   │   │       └── my-rentals.html
│   │   │   ├── static/              # Static files
│   │   │   │   ├── css/
│   │   │   │   │   └── style.css    # Custom styles
│   │   │   │   └── js/
│   │   │   │       └── main.js      # JavaScript utilities
│   │   │   └── application.properties  # Cấu hình ứng dụng
│   └── test/
├── pom.xml                          # Maven dependencies
└── README.md
```

## 🚀 Hướng Dẫn Cài Đặt & Chạy

### Yêu Cầu:
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Bước 1: Cài Đặt MySQL

```sql
CREATE DATABASE ev_rental_db;
USE ev_rental_db;
```

### Bước 2: Cấu Hình application.properties

Chỉnh sửa file `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/ev_rental_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password

# OAuth2 Google (optional)
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
```

#### Lấy Google Client ID & Secret:
1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo dự án mới
3. Tạo OAuth 2.0 Credentials (Web Application)
4. Cấu hình Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
5. Copy Client ID & Secret vào application.properties

### Bước 3: Chạy Ứng Dụng

```bash
# Terminal
mvn clean install
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: http://localhost:8080

## 📝 Tài Khoản Test

Sau khi khởi động, dữ liệu sẽ được tạo tự động qua JPA (ddl-auto: create-drop).

Bạn có thể:
1. Đăng ký tài khoản mới tại `/auth/register`
2. Đăng nhập với Google (nếu đã cấu hình)

## 🔒 Bảo Mật

- Mật khẩu được mã hóa với BCrypt
- CSRF protection được bật mặc định
- Form validation trên cả client và server
- OAuth2 integration với Google

## 📚 Database Schema

### Users Table
```sql
user_id (PK) | full_name | email (UNIQUE) | password (bcrypt) | phone_number | 
license_number | id_card_number | address | oauth_provider | oauth_id | is_active | 
created_at | updated_at
```

### Vehicles Table
```sql
vehicle_id (PK) | brand | model | vehicle_type | license_plate (UNIQUE) | battery_capacity |
max_range | price_per_hour | price_per_day | current_charge | current_location | image_url |
ratings | total_reviews | is_available | created_at | updated_at
```

### Rentals Table
```sql
rental_id (PK) | user_id (FK) | vehicle_id (FK) | pickup_datetime | return_datetime |
actual_return_datetime | pickup_location | return_location | total_price | status |
payment_status | notes | created_at | updated_at
```

### Reviews Table
```sql
review_id (PK) | vehicle_id (FK) | user_id (FK) | rating (1-5) | comment | created_at | updated_at
```

## 🎨 Styling & UI/UX

- Responsive design cho tất cả thiết bị (mobile, tablet, desktop)
- Gradient backgrounds & modern animations
- Bootstrap 5 grid system
- Font Awesome icons
- Custom CSS variables cho dễ dàng custom theme

## 🔄 Quy Trình Đặt Xe

1. **Tìm kiếm** - Người dùng tìm và lọc xe
2. **Chi tiết** - Xem thông tin chi tiết xe
3. **Đăng nhập** - Là bắt buộc để đặt xe
4. **Chọn thời gian** - Chọn ngày giờ nhận/trả
5. **Thanh toán** - Thanh toán online
6. **Xác nhận** - Nhận mã booking
7. **Nhận xe** - Tới điểm nhận xe
8. **Sử dụng** - Di chuyển và sử dụng
9. **Trả xe** - Trả xe đúng giờ
10. **Đánh giá** - Để lại đánh giá (optional)

## 📋 TODO & Tính Năng Sắp Tới

- [ ] Tích hợp thanh toán (Stripe/VNPay)
- [ ] Bản đồ trạm sạc (Google Maps API)
- [ ] Email verification
- [ ] Reset password via email
- [ ] Admin dashboard
- [ ] Real-time notifications
- [ ] Chat support
- [ ] Mobile app (React Native)
- [ ] Analytics & reporting

## 🤝 Đóng Góp

Chúng tôi hoan nghênh các đóng góp! Vui lòng:

1. Fork dự án
2. Tạo branch feature (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📄 License

Dự án này được cấp phép dưới MIT License - xem file [LICENSE](LICENSE) để chi tiết.

## 📞 Liên Hệ & Support

- Email: support@evrental.com
- Hotline: 1900-1234
- Website: www.evrental.com
- Facebook: facebook.com/evrental

---

**Made with ❤️ for a sustainable future** 🌱
