package com.evrental.evrentalsystem.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload file lên Cloudinary
     * @param file Multipart file cần upload
     * @param folder Folder trong Cloudinary (vd: "ev-rental/vehicles")
     * @return URL của file đã upload
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                log.warn("⚠️ File is null or empty");
                throw new IllegalArgumentException("File không được rỗng");
            }

            String originalFileName = file.getOriginalFilename();
            log.info("📸 Starting upload: {} ({} bytes)", originalFileName, file.getSize());
            
            String publicId = folder + "/" + System.currentTimeMillis();
            log.debug("Public ID: {}", publicId);

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "auto",
                    "quality", "auto",
                    "fetch_format", "auto"
                )
            );

            String imageUrl = (String) uploadResult.get("secure_url");
            
            if (imageUrl == null || imageUrl.isEmpty()) {
                log.error("❌ Cloudinary returned null or empty URL");
                log.debug("Upload result: {}", uploadResult);
                throw new RuntimeException("Cloudinary không trả về URL hợp lệ");
            }

            log.info("✅ File uploaded successfully to Cloudinary");
            log.info("📎 URL: {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            log.error("❌ IOException during Cloudinary upload: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi I/O khi upload: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Exception during Cloudinary upload: {}", e.getMessage(), e);
            log.error("Exception type: {}", e.getClass().getName());
            throw new RuntimeException("Lỗi upload lên Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Upload ảnh từ URL
     * @param imageUrl URL của ảnh cần upload
     * @param folder Folder trong Cloudinary
     * @return URL của ảnh đã upload
     */
    public String uploadFromUrl(String imageUrl, String folder) {
        try {
            log.info("📸 Uploading from URL: {}", imageUrl);
            
            String publicId = folder + "/" + System.currentTimeMillis();

            Map uploadResult = cloudinary.uploader().upload(
                imageUrl,
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", true,
                    "fetch_format", "auto"
                )
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("✅ Image from URL uploaded successfully: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("❌ Error uploading image from URL: {}", e.getMessage(), e);
            // Return original URL nếu upload thất bại
            return imageUrl;
        }
    }

    /**
     * Delete file từ Cloudinary
     * @param publicId ID của file cần xóa
     */
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("✅ File deleted successfully: {}", publicId);
        } catch (IOException e) {
            log.error("❌ Error deleting file: {}", e.getMessage());
        }
    }

    /**
     * Generate thumbnail URL
     * @param imageUrl URL của ảnh gốc
     * @param width Chiều rộng thumbnail
     * @param height Chiều cao thumbnail
     * @return URL của thumbnail
     */
    public String generateThumbnail(String imageUrl, int width, int height) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return imageUrl;
        }

        // Cloudinary thumbnail transformation
        return imageUrl.replace("/upload/", "/upload/w_" + width + ",h_" + height + ",c_fill/");
    }

    /**
     * Kiểm tra Cloudinary configuration
     */
    public void testConnection() {
        try {
            log.info("🔍 Testing Cloudinary connection...");
            
            if (cloudinary == null) {
                log.error("❌ Cloudinary bean is null!");
                throw new RuntimeException("Cloudinary bean not initialized");
            }
            
            log.info("✅ Cloudinary bean initialized successfully");
            
        } catch (Exception e) {
            log.error("❌ Cloudinary connection test failed: {}", e.getMessage(), e);
        }
    }
}
