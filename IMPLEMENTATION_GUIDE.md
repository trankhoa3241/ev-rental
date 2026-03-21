# 🔐 Hướng Dẫn Thiết Lập "Quên Mật Khẩu" và "Đăng Nhập Google"

## 📋 Tổng Quan Các Tính Năng Đã Triển Khai

### ✅ Hoàn Thành
Sau đây là những tính năng đã được triển khai hoàn chỉnh:

#### 1. **Quên Mật Khẩu (Forgot Password)**
- ✅ Giao diện form nhập email tại `/auth/forgot-password`
- ✅ Tạo token với thời gian hết hạn 24 giờ
- ✅ Gửi email với link đặt lại mật khẩu
- ✅ Xác thực token và hiển thị form đặt mật khẩu mới
- ✅ Cập nhật mật khẩu trong database
- ✅ Đánh dấu token đã sử dụng (không thể dùng lại)

#### 2. **Đăng Nhập Bằng Google**
- ✅ Nút "Đăng nhập với Google" tại `/auth/login`
- ✅ Tự động tạo user mới từ thông tin Google
- ✅ Cập nhật user nếu đã tồn tại
- ✅ Redirect dựa trên role (Admin → `/admin/dashboard`, User → `/dashboard`)
- ✅ Lưu thông tin OAuth (provider, oauthId)

---

## 🔧 Cấu Hình Cần Thiết

### 1. Email Configuration (SMTP)

Cặp nhật file `application.properties` với thông tin Gmail của bạn:

```properties
# Email Configuration (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL_EMAIL@gmail.com
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=noreply@evrental.com
```

#### ⚠️ Lưu ý quan trọng: Tạo App Password cho Gmail
1. Truy cập https://myaccount.google.com/apppasswords
2. Chọn "Mail" và "Windows Computer" (hoặc thiết bị của bạn)
3. Google sẽ tạo **App Password** 16 ký tự
4. Sao chép `YOUR_GMAIL_APP_PASSWORD` vào `application.properties`

**Ví dụ:**
```properties
spring.mail.username=myemail@gmail.com
spring.mail.password=tjza wzfe vwxc buln  # Mật khẩu ứng dụng từ Google
```

### 2. Google OAuth2 Configuration

Cấu hình Google Cloud Console:

#### Bước 1: Tạo Google Cloud Project
1. Truy cập https://console.cloud.google.com
2. Tạo project mới: "EV Rental System"
3. Chọn project vừa tạo

#### Bước 2: Kích hoạt Google+ API
1. Chọn "APIs & Services" → "Library"
2. Tìm "Google+ API" hoặc "Google Identity"
3. Click "Enable"

#### Bước 3: Tạo OAuth2 Credentials
1. Chọn "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth client ID"
3. Chọn "Web application"
4. Thêm Authorized JavaScript origins:
   - `http://localhost:8080`
   - `http://localhost:8080/`
5. Thêm Authorized redirect URIs:
   - `http://localhost:8080/login/oauth2/code/google`
6. Click "Create"
7. Copy **Client ID** và **Client Secret**

#### Bước 4: Cập nhật application.properties
```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```

**Ví dụ:**
```properties
spring.security.oauth2.client.registration.google.client-id=123456789-abcdefghijklmno.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=GOCSPX-1234567890abcdefghij
```

---

## 📁 Các File Đã Tạo/Sửa Đổi

### Tạo Mới
- **Entity**: `PasswordResetToken.java` - Model cho token đặt lại mật khẩu
- **Repository**: `PasswordResetTokenRepository.java` - Query token
- **Service**: `EmailService.java` - Gửi email
- **Security**: `CustomOAuth2UserService.java` - Xử lý OAuth2 user
- **Config**: `OAuth2SuccessHandler.java` - Redirect sau khi OAuth2 thành công

### Sửa Đổi
- **Controller**: `AuthController.java` - Thêm logic forgot-password và reset-password
- **Config**: `SecurityConfig.java` - Thêm OAuth2 login configuration
- **Build**: `pom.xml` - Thêm `spring-boot-starter-mail` dependency
- **Properties**: `application.properties` - Thêm cấu hình email và OAuth2

### Đã Có (Không cần thay đổi)
- `templates/auth/forgot-password.html` - UI hoàn chỉnh
- `templates/auth/reset-password.html` - UI hoàn chỉnh
- `templates/auth/login.html` - Đã có nút Google

---

## 📝 Quy Trình Sử Dụng

### Forgot Password (Quên Mật Khẩu)

#### Luồng người dùng:
1. Click "Quên mật khẩu?" tại trang login
2. Nhập email → Nhấn "Gửi hướng dẫn"
3. Kiểm tra email → Click link "Đặt lại Mật khẩu"
4. Nhập mật khẩu mới 2 lần → Nhấn "Cập Nhật Mật Khẩu"
5. Redirect tới login → Đăng nhập với mật khẩu mới

#### Các điểm xử lý:

| Bước | Endpoint | Method | Xử lý |
|------|----------|--------|-------|
| 1 | `/auth/forgot-password` | GET | Hiển thị form nhập email |
| 2 | `/auth/forgot-password` | POST | Tạo token → Gửi email |
| 3 | (Email) | - | User click link |
| 4 | `/auth/reset-password?token=XXX` | GET | Validate token → Hiển thị form |
| 5 | `/auth/reset-password` | POST | Cập nhật password → Redirect login |

### Google Login (Đăng Nhập Google)

#### Luồng người dùng:
1. Tại trang login, click "Đăng nhập với Google"
2. Chọn tài khoản Google
3. Đồng ý quyền truy cập
4. Tự động redirect đến `/dashboard` (hoặc `/admin/dashboard` nếu là admin)

#### Các điểm xử lý:

| Bước | URL | Xử lý |
|------|-----|-------|
| 1 | `/oauth2/authorization/google` | Spring Security redirect đến Google |
| 2 | (Google) | User đăng nhập Google |
| 3 | `/login/oauth2/code/google` | Spring Security callback |
| 4 | `CustomOAuth2UserService` | Tạo/Cập nhật user |
| 5 | `OAuth2SuccessHandler` | Redirect dựa trên role |

---

## 🧪 Testing

### Test Forgot Password
```bash
1. Truy cập http://localhost:8080/auth/forgot-password
2. Nhập email: user@evrental.com
3. Kiểm tra console cho log: "Password reset link sent to: ..."
4. Hoặc kiểm tra mailbox (nếu đã config email)
```

### Test Google Login
```bash
1. Truy cập http://localhost:8080/auth/login
2. Click "Đăng nhập với Google"
3. Chọn tài khoản Google
4. Nếu thành công → redirect /dashboard
5. Kiểm tra MongoDB: user mới được tạo với oauthProvider=GOOGLE
```

---

## 🛠️ Troubleshooting

### Email không gửi được

**Vấn đề**: "Authentication failed; nested exception is javax.mail.AuthenticationFailedException"

**Giải pháp**:
1. Kiểm tra `spring.mail.username` và `spring.mail.password` trong `application.properties`
2. Chắc chắn đó là **App Password** chứ không phải Gmail password
3. Bật 2-Step Verification trong Google Account
4. Kiểm tra console log: `logging.level.org.springframework.mail=DEBUG`

### Google OAuth2 không hoạt động

**Vấn đề**: "Redirect URI mismatch"

**Giải pháp**:
1. Kiểm tra **Authorized redirect URIs** trong Google Cloud Console
2. Phải là: `http://localhost:8080/login/oauth2/code/google`
3. Nếu deploy ra server, thêm domain thực tế: `https://yourdomain.com/login/oauth2/code/google`

**Vấn đề**: "Client authentication failed"

**Giải pháp**:
1. Kiểm tra `client-id` và `client-secret` trong `application.properties`
2. Chắc chắn không có khoảng trắng thừa
3. Lấy credentials mới từ Google Cloud Console

### Token đã hết hạn

**Vấn đề**: "Link đã hết hạn hoặc không hợp lệ"

**Giải pháp**:
1. Token hết hạn sau 24 giờ
2. Yêu cầu tạo token mới: Truy cập `/auth/forgot-password` lại
3. Hoặc tăng thời gian hết hạn: `LocalDateTime.plusHours(24)` → `LocalDateTime.plusHours(48)`

---

## 🔒 Security Best Practices

### Đã Implement
✅ Password mã hóa BCrypt
✅ Token ngẫu nhiên (UUID)
✅ Token hết hạn
✅ Token chỉ dùng được 1 lần
✅ Email validation
✅ CSRF disabled (cần enable sau)
✅ Role-based access control

### Cần Làm Thêm (Future)
- [ ] Enable CSRF protection
- [ ] Rate limiting cho forgot-password endpoint
- [ ] Logging ghi nhận toàn bộ auth events
- [ ] JWT tokens cho API
- [ ] Two-Factor Authentication (2FA)

---

## 📞 Database Schema

### PasswordResetToken Collection
```json
{
  "_id": "ObjectId",
  "token": "uuid-string",
  "userId": "user-id",
  "userEmail": "user@example.com",
  "createdAt": "2024-03-21T10:30:00",
  "expiresAt": "2024-03-22T10:30:00",
  "used": false
}
```

### User Collection (thêm fields OAuth)
```json
{
  "_id": "ObjectId",
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "bcrypt-hash",
  "oauthProvider": "GOOGLE|LOCAL",
  "oauthId": "google-id",
  ...
}
```

---

## 🎯 Next Steps

1. **Cấu hình Email**:
   - [ ] Lấy Gmail App Password
   - [ ] Cập nhật `application.properties`
   - [ ] Test gửi email

2. **Cấu hình Google OAuth2**:
   - [ ] Tạo Google Cloud Project
   - [ ] Tạo OAuth2 credentials
   - [ ] Cập nhật `application.properties` với Client ID/Secret
   - [ ] Test Google login

3. **Build & Deploy**:
   ```bash
   mvn clean install
   java -jar target/ev-rental-system-0.0.1-SNAPSHOT.jar
   ```

4. **Kiểm thử**:
   - [ ] Test forgot password flow
   - [ ] Test Google login
   - [ ] Kiểm tra MongoDB có user mới
   - [ ] Test redirect based on role

---

## 📌 Lưu Ý Quan Trọng

⚠️ **Sensitive Information**:
- KHÔNG commit `application.properties` chứa credentials vào Git
- Sử dụng environment variables hoặc secure vault
- Mẫu `.gitignore`:
  ```
  application.properties
  src/main/resources/application-*.properties
  ```

⚠️ **Production Deployment**:
- Tắt debug logging: `logging.level.root=INFO`
- Enable CSRF protection
- Sử dụng HTTPS (bắt buộc cho OAuth2)
- Set `app.frontend.url` đúng domain
- Tăng thời gian hết hạn token phù hợp
- Backup MongoDB

---

## 📚 Tài Liệu Tham Khảo
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Spring Mail](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.mail)
- [Google OAuth2](https://developers.google.com/identity/protocols/oauth2)
- [MongoDB Spring Data](https://docs.spring.io/spring-data/mongodb/docs/)

---

**Version**: 1.0  
**Ngày cập nhật**: 2024-03-21  
**Status**: ✅ Hoàn thành - Sẵn sàng test
