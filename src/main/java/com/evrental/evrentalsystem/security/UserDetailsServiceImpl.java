package com.evrental.evrentalsystem.security;

import com.evrental.evrentalsystem.entity.User;
import com.evrental.evrentalsystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔐 Attempting to load user: {}", username);
        
        // Tìm user bằng email (username = email)
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> {
                log.warn("❌ User không tìm thấy với email: {}", username);
                return new UsernameNotFoundException("User không tìm thấy với email: " + username);
            });

        log.info("✅ User tìm thấy: {}", user.getEmail());
        return new UserDetailsImpl(user);
    }
}
