package com.example.Netflix.RefreshTokens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Value("${REFRESH_TOKEN_EXPIRATION}")
    private long expirationRefreshToken;

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + expirationRefreshToken));

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public Optional<RefreshToken> findRefreshTokenByToken(String token) {
        return refreshTokenRepository.findRefreshTokenByToken(token);
    }

    @Transactional
    public void deleteRefreshTokenByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public boolean isRefreshedTokenExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().before(new Date());
    }
}
