package com.evrental.evrentalsystem.security;

import com.evrental.evrentalsystem.entity.User;
import com.evrental.evrentalsystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            log.info("🔐 [OAuth2] Starting OAuth2 user loading process...");
            
            OAuth2User oAuth2User = super.loadUser(userRequest);
            
            // Get the OAuth provider (google, github, etc.)
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            log.info("🔐 [OAuth2] OAuth Provider: {}", registrationId);
            
            // Extract user information
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");
            String oauthId = oAuth2User.getName(); // This is typically the 'sub' for Google
            
            log.info("🔐 [OAuth2] Extracted user info - Email: {}, Name: {}, OAuthId: {}", email, name, oauthId);
            
            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                log.info("📌 [OAuth2] EXISTING USER FOUND - Email: {} | DB Records: {} record(s) with this email", email, 1);
                log.info("📌 [OAuth2] Current OAuth Provider in DB: {} | Current OAuth ID in DB: {}", 
                    user.getOauthProvider(), user.getOauthId());
                
                // Update user information if coming from OAuth
                if (user.getOauthProvider() == null || !registrationId.equals(user.getOauthProvider())) {
                    log.info("🔄 [OAuth2] User {} found but with different/no provider. Updating to {}...", email, registrationId);
                    user.setOauthProvider(registrationId);
                    user.setOauthId(oauthId);
                }
                
                // Update profile picture if provided
                if (picture != null && !picture.isEmpty() && 
                    (user.getProfileImage() == null || user.getProfileImage().contains("placeholder"))) {
                    log.info("📸 [OAuth2] Updating profile picture for user: {}", email);
                    user.setProfileImage(picture);
                }
                
                user.setUpdatedAt(LocalDateTime.now());
                User savedUser = userRepository.save(user);
                log.info("✅ [OAuth2] Existing user UPDATED and SAVED to MongoDB - ID: {} | Email: {} | OAuth Provider: {}", 
                    savedUser.getId(), savedUser.getEmail(), savedUser.getOauthProvider());
            } else {
                // Create new user from OAuth (Auto Registration)
                log.info("🆕 [OAuth2] CREATE NEW USER - Email: {} does NOT exist in MongoDB, creating new account...", email);
                
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFullName(name != null ? name : email.split("@")[0]);
                newUser.setProfileImage(picture != null ? picture : "https://via.placeholder.com/150?text=Avatar");
                newUser.setOauthProvider(registrationId);
                newUser.setOauthId(oauthId);
                newUser.setRole("USER");
                newUser.setIsActive(true);
                newUser.setPassword(null); // OAuth users don't have passwords
                newUser.setLicenseNumber("");
                newUser.setIdCardNumber("");
                newUser.setAddress("");
                newUser.setPhoneNumber("");
                newUser.setCreatedAt(LocalDateTime.now());
                newUser.setUpdatedAt(LocalDateTime.now());
                
                User savedUser = userRepository.save(newUser);
                log.info("✅ [OAuth2] NEW USER CREATED and SAVED to MongoDB - ID: {} | Email: {} | Full Name: {} | OAuth Provider: {}", 
                    savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getOauthProvider());
            }
            
            log.info("✅ [OAuth2] User loading completed successfully for: {}", email);
            return oAuth2User;
        } catch (OAuth2AuthenticationException ex) {
            log.error("❌ [OAuth2] OAuth2 authentication failed: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("❌ [OAuth2] Unexpected error during OAuth2 user loading: {}", ex.getMessage(), ex);
            throw new OAuth2AuthenticationException("Error loading OAuth2 user: " + ex.getMessage());
        }
    }
}
