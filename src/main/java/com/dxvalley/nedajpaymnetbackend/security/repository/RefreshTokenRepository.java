package com.dxvalley.nedajpaymnetbackend.security.repository;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.RefreshToken;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken findByUserId(Long refreshTokenUserId);
    @Modifying
    int deleteByUser(User user);
}
