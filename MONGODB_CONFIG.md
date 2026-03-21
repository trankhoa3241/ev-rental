# MongoDB Configuration Guide

## ✅ Cấu Hình Hoàn Tất

Dự án EV Rental System đã được chuyển từ **MySQL** sang **MongoDB**.

### 🔌 Connection String

```
mongodb+srv://120304thuan_db_user:2nmQyiOM0XLdHQh7@cluster0.ybve5x3.mongodb.net/?appName=Cluster0
```

### 📝 Cấu Hình Hiện Tại (application.properties)

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb+srv://120304thuan_db_user:2nmQyiOM0XLdHQh7@cluster0.ybve5x3.mongodb.net/?appName=Cluster0
spring.data.mongodb.database=ev_rental_db
spring.data.mongodb.auto-index-creation=true
```

## 📁 Collections được tạo

Khi ứng dụng khởi động, các collections sau sẽ được tự động tạo:

1. **users** - Lưu trữ thông tin người dùng
2. **vehicles** - Lưu trữ thông tin xe điện
3. **rentals** - Lưu trữ lịch sử thuê xe
4. **reviews** - Lưu trữ đánh giá xe

## 🏗️ Cấu Trúc Document

### Users Collection

```javascript
{
  "_id": ObjectId,
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@email.com",
  "password": "bcrypt_hashed_password",
  "phoneNumber": "0901234567",
  "licenseNumber": "ABC123456",
  "idCardNumber": "123456789",
  "address": "123 Đường ABC, TP. HCM",
  "profileImage": "image_url",
  "isActive": true,
  "oauthProvider": "LOCAL",
  "oauthId": null,
  "createdAt": ISODate("2026-03-15T10:00:00Z"),
  "updatedAt": ISODate("2026-03-15T10:00:00Z")
}
```

### Vehicles Collection

```javascript
{
  "_id": ObjectId,
  "brand": "Tesla",
  "model": "Model 3",
  "vehicleType": "CAR",
  "licensePlate": "ABC123",
  "batteryCapacity": 85,
  "maxRange": 500,
  "pricePerHour": 250000,
  "pricePerDay": 2000000,
  "currentCharge": 85,
  "currentLocation": "123 Đường ABC, TP. HCM",
  "imageUrl": "image_url",
  "description": "Mô tả xe",
  "isAvailable": true,
  "ratings": 4.8,
  "totalReviews": 120,
  "createdAt": ISODate("2026-03-15T10:00:00Z"),
  "updatedAt": ISODate("2026-03-15T10:00:00Z")
}
```

### Rentals Collection

```javascript
{
  "_id": ObjectId,
  "user": DBRef("users", ObjectId),
  "vehicle": DBRef("vehicles", ObjectId),
  "pickupDateTime": ISODate("2026-03-20T10:00:00Z"),
  "returnDateTime": ISODate("2026-03-21T10:00:00Z"),
  "actualReturnDateTime": null,
  "pickupLocation": "Điểm A",
  "returnLocation": "Điểm B",
  "totalPrice": 250000,
  "status": "CONFIRMED",
  "paymentStatus": "COMPLETED",
  "notes": "Ghi chú",
  "createdAt": ISODate("2026-03-15T10:00:00Z"),
  "updatedAt": ISODate("2026-03-15T10:00:00Z")
}
```

### Reviews Collection

```javascript
{
  "_id": ObjectId,
  "vehicle": DBRef("vehicles", ObjectId),
  "user": DBRef("users", ObjectId),
  "rating": 5,
  "comment": "Xe rất tốt!",
  "createdAt": ISODate("2026-03-15T10:00:00Z"),
  "updatedAt": ISODate("2026-03-15T10:00:00Z")
}
```

## 🚀 Chạy Ứng Dụng

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

Ứng dụng sẽ kết nối tự động tới MongoDB Atlas cluster của bạn.

## ✅ Kiểm Tra Kết Nối

### 1. Truy cập MongoDB Atlas

1. Vào https://www.mongodb.com/cloud/atlas
2. Login với tài khoản của bạn
3. Chọn Cluster0
4. Vào tab **Collections** để xem dữ liệu

### 2. Kiểm Tra trong Spring Boot

Khi ứng dụng khởi động, bạn sẽ thấy logging:

```
[main] DEBUG org.springframework.data.mongodb - Connecting to MongoDB...
[main] DEBUG org.springframework.data.mongodb - Connected to MongoDB
```

### 3. Test API

```bash
# Đăng ký người dùng
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "fullName": "Test User",
  "email": "test@example.com",
  "password": "password123",
  "phoneNumber": "0901234567"
}
```

## 🔐 Bảo Mật

### Network Access

Connection string của bạn chỉ có thể truy cập từ những IP được whitelist trong MongoDB Atlas:

- Hiện tại: Cho phép tất cả IP (0.0.0.0/0) - **Không an toàn cho Production**

#### Khuyến cáo:
- Thêm IP của máy chủ production vào whitelist
- Thay đổi password sau khi thiết lập

### Thay Đổi Password MongoDB

1. Vào MongoDB Atlas
2. Security → Database Access
3. Edit user → Change Password
4. Cập nhật connection string trong application.properties

## 📊 Monitoring

### Xem Metrics trong MongoDB Atlas

1. Vào Cluster0
2. Tab **Metrics** để xem:
   - Số lượng connections
   - Throughput (reads/writes)
   - Storage usage
   - CPU usage

### Enable Advanced Monitoring

```properties
# application.properties
spring.data.mongodb.uri=mongodb+srv://user:pass@cluster.mongodb.net/?appName=Cluster0&maxPoolSize=10&minPoolSize=5
spring.data.mongodb.auto-index-creation=true
logging.level.org.springframework.data.mongodb=DEBUG
```

## 🛠️ Troubleshooting

### Lỗi: "Authentication failed"

```
MongoAuthenticationException: Authentication failed
```

**Giải pháp:**
- Kiểm tra username & password trong connection string
- Đảm bảo user có quyền truy cập database `ev_rental_db`
- Kiểm tra IP whitelist trong MongoDB Atlas

### Lỗi: "Connection timeout"

```
MongoTimeoutException: Timed out after 30000 ms
```

**Giải pháp:**
- Kiểm tra kết nối internet
- Đảm bảo cluster MongoDB Atlas đang chạy
- Thử ping connection:
  ```bash
  ping cluster0.ybve5x3.mongodb.net
  ```

### Lỗi: "Database does not exist"

```
MongoServerException: no matching namespaces found for hint on server
```

**Giải pháp:**
- Collections sẽ được tạo tự động khi chèn dữ liệu
- Hoặc chạy lệnh insert đâu tiên để tạo collection:
  ```javascript
  db.users.insertOne({...})
  ```

## 📚 Tài Liệu Tham Khảo

- [MongoDB Atlas Documentation](https://docs.atlas.mongodb.com/)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [MongoDB Java Driver](https://www.mongodb.com/docs/drivers/java/sync/current/)

## 💡 Mẹo & Best Practices

1. **Index Creation** - Tạo index cho các trường hay query:
   ```javascript
   db.users.createIndex({ "email": 1 })
   db.rentals.createIndex({ "userId": 1, "status": 1 })
   ```

2. **Connection Pooling** - Tối ưu performance:
   ```properties
   spring.data.mongodb.uri=...&maxPoolSize=50&minPoolSize=10
   ```

3. **Pagination** - Sử dụng `Pageable` cho query lớn:
   ```java
   Page<Vehicle> findAll(Pageable pageable);
   ```

4. **TTL Index** - Tự động xóa documents sau thời gian:
   ```java
   @Document
   @Indexed(expireAfterSeconds = 3600) // Tự động xóa sau 1h
   public class VerificationToken { ... }
   ```

---

**Dự án đã sẵn sàng sử dụng MongoDB!** 🎉
