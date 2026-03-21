package com.evrental.evrentalsystem.repository;

import com.evrental.evrentalsystem.entity.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByUserEmailAndUsed(String userEmail, boolean used);
}
