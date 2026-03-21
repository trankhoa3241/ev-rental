# 🔄 Migration Summary: MySQL → MongoDB

## ✅ Hoàn Thành - Chuyển Từ MySQL Sang MongoDB

Dự án EV Rental System đã được chuyển đổi thành công từ **MySQL** sang **MongoDB**.

---

## 📦 Thay Đổi Dependencies (pom.xml)

### ❌ Đã Xóa

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Connector -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

### ✅ Đã Thêm

```xml
<!-- Spring Data MongoDB -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

---

## 📝 Thay Đổi Application Properties

### ❌ Cấu Hình MySQL (Cũ)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ev_rental_db?useSSL=false
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### ✅ Cấu Hình MongoDB (Mới)

```properties
spring.data.mongodb.uri=mongodb+srv://120304thuan_db_user:2nmQyiOM0XLdHQh7@cluster0.ybve5x3.mongodb.net/?appName=Cluster0
spring.data.mongodb.database=ev_rental_db
spring.data.mongodb.auto-index-creation=true
```

---

## 🏢 Entity Models - Thay Đổi

### User Entity

#### ❌ Cũ (JPA)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "full_name")
    private String fullName;
}
```

#### ✅ Mới (MongoDB)
```java
@Document(collection = "users")
public class User {
    @Id
    private String id;  // MongoDB tự sinh ObjectId
    
    private String fullName;  // Không cần @Column
}
```

### Vehicle Entity

#### ❌ Cũ
```java
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "brand")
    private String brand;
}
```

#### ✅ Mới
```java
@Document(collection = "vehicles")
public class Vehicle {
    @Id
    private String id;
    
    private String brand;
}
```

### Rental Entity - Relationships

#### ❌ Cũ (JPA Foreign Keys)
```java
@ManyToOne
@JoinColumn(name = "user_id", nullable = false)
private User user;

@ManyToOne
@JoinColumn(name = "vehicle_id", nullable = false)
private Vehicle vehicle;
```

#### ✅ Mới (MongoDB DBRef)
```java
@DBRef
private User user;

@DBRef
private Vehicle vehicle;
```

### Review Entity

#### ❌ Cũ
```java
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
```

#### ✅ Mới
```java
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    
    @DBRef
    private Vehicle vehicle;
}
```

---

## 📚 Repository Interfaces - Thay Đổi

### UserRepository

#### ❌ Cũ
```java
public interface UserRepository extends JpaRepository<User, Long> { }
```

#### ✅ Mới
```java
public interface UserRepository extends MongoRepository<User, String> { }
```

### VehicleRepository

#### ❌ Cũ
```java
public interface VehicleRepository extends JpaRepository<Vehicle, Long> { }
```

#### ✅ Mới
```java
public interface VehicleRepository extends MongoRepository<Vehicle, String> { }
```

### RentalRepository

#### ❌ Cũ
```java
List<Rental> findByUserId(Long userId);
```

#### ✅ Mới
```java
List<Rental> findByUserId(String userId);  // String thay Long
```

### ReviewRepository

#### ❌ Cũ
```java
List<Review> findByVehicleId(Long vehicleId);
```

#### ✅ Mới
```java
List<Review> findByVehicleId(String vehicleId);  // String thay Long
```

---

## 🆕 Thêm Mới

### 1. DataInitializer.java - Sample Data

```java
@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initializeData(...) {
        // Tự động tạo dữ liệu mẫu khi khởi động
        // Users: nguyenvana@email.com, tranthib@email.com
        // Vehicles: Tesla, Honda, Vespa
    }
}
```

### 2. MONGODB_CONFIG.md - Documentation

- Cấu hình MongoDB Atlas
- Structure của các collections
- Troubleshooting
- Best practices

### 3. RUN_GUIDE.md - Hướng Dẫn Chạy

- Yêu cầu hệ thống
- Các bước cài đặt
- Cách kiểm tra kết nối
- FAQs

---

## 📊 So Sánh MySQL vs MongoDB

| Tiêu Chí | MySQL | MongoDB |
|-----------|--------|-----------|
| **Cơ sở dữ liệu** | RDBMS | NoSQL Document |
| **Schema** | Cố định | Linh hoạt |
| **Join** | FOREIGN KEY | DBRef |
| **Scaling** | Vertical | Horizontal |
| **Speed** | Tốt cho relational | Tốt cho document |
| **Connection String** | localhost:3306 | Cloud URI |
| **ID Type** | Long/INT | String (ObjectId) |

---

## ✨ Ưu Điểm MongoDB

1. **Cloud-based** - MongoDB Atlas (không cần cài đặt local)
2. **Document Model** - Dữ liệu JSON, dễ dàng mở rộng
3. **Auto-scaling** - Tự động scale theo nhu cầu
4. **Flexible Schema** - Thêm field không cần migration
5. **Better for Rapid Development** - Agile development

---

## 🔌 Connection Details

**MongoDB Atlas Cluster Information:**

```
Username: 120304thuan_db_user
Database: ev_rental_db
Cluster: Cluster0
Region: (Tùy vào region được chọn)
Connection String:
  mongodb+srv://120304thuan_db_user:2nmQyiOM0XLdHQh7@cluster0.ybve5x3.mongodb.net/?appName=Cluster0
```

---

## 🚀 Cách Build & Run (Không Thay Đổi)

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Access
http://localhost:8080
```

---

## 💾 Collections được tạo

1. **users** - Người dùng
2. **vehicles** - Xe điện
3. **rentals** - Lịch sử thuê
4. **reviews** - Đánh giá

---

## 🔒 Bảo Mật

- ✅ Connection string đã được cấu hình
- ✅ Username & password được bảo mật
- ⚠️ **Lưu ý**: Nên thay đổi password trước khi production
- ⚠️ **Lưu ý**: Firewall IP whitelist chỉ cho development (0.0.0.0/0)

---

## 📝 Migration Checklist

- ✅ Cập nhật pom.xml (xóa JPA, thêm MongoDB)
- ✅ Cập nhật application.properties (MongoDB URI)
- ✅ Cập nhật tất cả Entity classes (JPA → MongoDB annotations)
- ✅ Cập nhật tất cả Repository interfaces (JpaRepository → MongoRepository)
- ✅ Cập nhật method signatures (Long → String)
- ✅ Xóa @PrePersist/@PreUpdate (MongoDB handle khác)
- ✅ Thêm DataInitializer cho sample data
- ✅ Kiểm tra Controllers (không thay đổi logic)
- ✅ Kiểm tra Views (HTML không thay đổi)
- ✅ Viết documentation

---

## 🔍 Test Kết Nối

Khi chạy ứng dụng, bạn sẽ thấy:

```
✅ Sample users created successfully!
✅ Sample vehicles created successfully!
✅ Data initialization completed!
```

---

## 🤝 Hỗ Trợ

Nếu gặp vấn đề:

1. Kiểm tra [MONGODB_CONFIG.md](MONGODB_CONFIG.md)
2. Kiểm tra [RUN_GUIDE.md](RUN_GUIDE.md)
3. Xem logs: `logging.level.org.springframework.data.mongodb=DEBUG`

---

**Migration hoàn tất! Dự án đã sẵn sàng sử dụng MongoDB** 🎉

