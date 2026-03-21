# 🚀 Hướng Dẫn Chạy EV Rental System với MongoDB

## ✅ Yêu Cầu

- Java 17 hoặc cao hơn
- Maven 3.6+
- MongoDB Atlas Account (đã được cấu hình)

## 📋 Bước 1: Clone/Download Dự Án

```bash
cd e:\ev-rental-system\ev-rental-system
```

## 🔧 Bước 2: Cấu Hình (Đã Hoàn Tất)

Dự án đã được cấu hình sẵn để kết nối MongoDB Atlas:

**Connection String:**
```
mongodb+srv://120304thuan_db_user:2nmQyiOM0XLdHQh7@cluster0.ybve5x3.mongodb.net/?appName=Cluster0
```

Được cấu hình trong file: `src/main/resources/application.properties`

```properties
spring.data.mongodb.uri=mongodb+srv://120304thuan_db_user:2nmQyiOM0XLdHQh7@cluster0.ybve5x3.mongodb.net/?appName=Cluster0
spring.data.mongodb.database=ev_rental_db
spring.data.mongodb.auto-index-creation=true
```

## 🏗️ Bước 3: Build Dự Án

### Lệnh Maven

```bash
# Build project
mvn clean install

# Hoặc nếu bạn có Maven wrapper
./mvnw clean install
```

Lệnh này sẽ:
- Tải tất cả dependencies
- Compile Java code
- Run unit tests
- Tạo JAR file

### Thời gian Build

Lần đầu: ~5-10 phút (tùy internet)
Những lần tiếp theo: ~1-2 phút

## 🚀 Bước 4: Chạy Ứng Dụng

### Cách 1: Chạy với Maven (Khuyến nghị)

```bash
mvn spring-boot:run
```

### Cách 2: Chạy JAR file

```bash
# Tìm JAR file
cd target
java -jar ev-rental-system-0.0.1-SNAPSHOT.jar
```

### Cách 3: Chạy trong IDE (IntelliJ IDEA / Eclipse)

1. Nhấp chuột phải trên `EvRentalSystemApplication.java`
2. Chọn **Run 'EvRentalSystemApplication.main()'**
3. Hoặc nhấn `Shift + F10` (IntelliJ) / `Ctrl + F11` (Eclipse)

## ✅ Check Ứng Dụng Có Chạy Không

Khi khởi động, bạn sẽ thấy:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v3.5.11)

2026-03-15 10:00:00.000  INFO 1234 --- [main] c.e.e.EvRentalSystemApplication: Starting EvRentalSystemApplication
...
2026-03-15 10:00:05.000  INFO 1234 --- [main] c.e.e.EvRentalSystemApplication: Started EvRentalSystemApplication in 5.123 seconds
✅ Sample users created successfully!
✅ Sample vehicles created successfully!
✅ Data initialization completed!
```

## 🌐 Truy Cập Ứng Dụng

**URL:** http://localhost:8080

### Các Trang Chính

| Trang | URL | Mô Tả |
|-------|-----|-------|
| Trang Chủ | http://localhost:8080/ | Hero, xe nổi bật, CTA |
| Danh Sách Xe | http://localhost:8080/vehicles | Lọc xe, phân trang |
| Đăng Nhập | http://localhost:8080/auth/login | Form đăng nhập |
| Đăng Ký | http://localhost:8080/auth/register | Form đăng ký |
| Về Chúng Tôi | http://localhost:8080/about | Thông tin công ty |
| Liên Hệ | http://localhost:8080/contact | Form liên hệ, FAQ |

## 👤 Tài Khoản Test

### 1. Đăng Ký Tài Khoản Mới

Truy cập: http://localhost:8080/auth/register

```
Họ và tên: Test User
Email: test@example.com
Số điện thoại: 0901234567
Mật khẩu: password123
Xác nhận: password123
```

### 2. Dùng Tài Khoản Mẫu (Đã Có)

```
Email: nguyenvana@email.com
Mật khẩu: password123
```

hoặc

```
Email: tranthib@email.com
Mật khẩu: password123
```

## 📊 Kiểm Tra MongoDB

### 1. MongoDB Atlas Dashboard

1. Truy cập: https://cloud.mongodb.com
2. Login với account của bạn
3. Chọn **Cluster0**
4. Vào tab **Collections**

### 2. Xem Data trong MongoDB

Nhấp vào collections:
- **users** - Người dùng đã đăng ký
- **vehicles** - Danh sách xe
- **rentals** - Lịch sử thuê
- **reviews** - Đánh giá

## 🛑 Dừng Ứng Dụng

```bash
# Nếu chạy trên Terminal
Nhấn Ctrl + C

# Hoặc nếu chạy IDE
Nhấp nút Stop
```

## 🔍 Troubleshooting

### Lỗi: Port 8080 đang được sử dụng

```
Address already in use: bind
```

**Giải pháp:**

```bash
# Tìm process sử dụng port 8080
netstat -ano | findstr :8080

# Kill process (thay <PID> với ID tìm được)
taskkill /PID <PID> /F

# Hoặc chạy trên port khác
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Lỗi: "Authentication failed" MongoDB

```
MongoAuthenticationException: Authentication failed
```

**Giải pháp:**
- Kiểm tra username & password trong connection string
- Kiểm tra IP whitelist trong MongoDB Atlas (Security → Network Access)
- Cho phép tất cả IPs: 0.0.0.0/0 (chỉ cho development)

### Lỗi: Maven build không thành công

```bash
# Clear cache
mvn clean
rm -rf .m2/repository (Linux/Mac)
rmdir /s /q %userprofile%\.m2\repository (Windows)

# Build lại
mvn install
```

### Lỗi: Java version không đúng

```bash
# Check Java version
java -version

# Cần Java 17+, nếu không có hãy install:
# https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
```

## 📈 Giám Sát

### Xem Logs

**Xem tất cả logs:**
```bash
# Logs được in ra console khi chạy
```

**Xem logs cụ thể:**
```properties
# Trong application.properties
logging.level.com.evrental.evrentalsystem=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
```

### Health Check

```
GET http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "mongodb": {
      "status": "UP"
    }
  }
}
```

## 🔐 Cấu Hình Bảo Mật

### Thay Đổi Google OAuth2 (Optional)

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo OAuth 2.0 Credentials (Web Application)
3. Set Redirect URI: `http://localhost:8080/login/oauth2/code/google`
4. Copy Client ID & Secret
5. Cập nhật trong `application.properties`:

```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```

## 📚 Tệp Tài Liệu Liên Quan

- [MONGODB_CONFIG.md](MONGODB_CONFIG.md) - Cấu hình MongoDB chi tiết
- [README_VN.md](README_VN.md) - Tài liệu dự án
- [pom.xml](pom.xml) - Dependencies

## 💡 Tips & Tricks

1. **Hot Reload** - Tự động reload khi sửa code:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.devtools.restart.enabled=true"
   ```

2. **Skip Tests** - Build nhanh hơn:
   ```bash
   mvn clean install -DskipTests
   ```

3. **View Dependency Tree**:
   ```bash
   mvn dependency:tree
   ```

4. **Generate JAR executable**:
   ```bash
   mvn clean package
   # JAR sẽ ở: target/ev-rental-system-0.0.1-SNAPSHOT.jar
   ```

## ❓ FAQ

**Q: Ứng dụng chạy nhưng không kết nối MongoDB?**
A: Kiểm tra IP whitelist & connection string trong MongoDB Atlas

**Q: Làm sao để reset dữ liệu?**
A: Xóa database `ev_rental_db` trong MongoDB Atlas, restart ứng dụng

**Q: Chạy build lần đầu mất bao lâu?**
A: Khoảng 5-10 phút tùy tốc độ internet

**Q: Có thể chạy trên port khác không?**
A: Có, thêm: `--server.port=8888`

---

**Chúc bạn phát triển dự án thành công!** 🎉

Nếu gặp vấn đề, hãy kiểm tra logs và troubleshooting section.
