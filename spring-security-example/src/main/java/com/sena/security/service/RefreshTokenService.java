package com.sena.security.service;

import com.sena.security.exception.InvalidRefreshTokenException;
import com.sena.security.model.RefreshToken;
import com.sena.security.model.User;
import com.sena.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // 7 días por defecto
    private long refreshExpirationMs;

    /**
     * Crea y persiste un nuevo refresh token para el usuario.
     * Revoca los anteriores del mismo usuario para evitar acumulación.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.revokeAllByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Verifica que el token exista, no esté revocado y no haya expirado.
     * Si expiró, lo revoca en BD y lanza excepción.
     */
    @Transactional
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token no encontrado"));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token revocado");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new InvalidRefreshTokenException("Refresh token expirado");
        }

        return refreshToken;
    }

    /**
     * Revoca todos los refresh tokens del usuario (logout).
     */
    @Transactional
    public void revokeAllTokens(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
