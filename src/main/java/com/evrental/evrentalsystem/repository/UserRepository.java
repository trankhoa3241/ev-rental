package com.evrental.evrentalsystem.repository;

import com.evrental.evrentalsystem.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthIdAndOauthProvider(String oauthId, String oauthProvider);
    boolean existsByEmail(String email);
}
