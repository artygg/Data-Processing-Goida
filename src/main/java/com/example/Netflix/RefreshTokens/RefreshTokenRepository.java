package com.example.Netflix.RefreshTokens;

import com.example.Netflix.ApiUsers.ApiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenByToken(String token);
    void deleteByToken(String token);
}
