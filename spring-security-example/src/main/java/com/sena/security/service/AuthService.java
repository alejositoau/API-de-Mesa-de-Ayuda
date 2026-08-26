package com.sena.security.service;

import com.sena.security.dto.AuthResponse;
import com.sena.security.dto.LoginRequest;
import com.sena.security.dto.RefreshRequest;
import com.sena.security.dto.RegisterRequest;
import com.sena.security.exception.UserAlreadyExistsException;
import com.sena.security.model.RefreshToken;
import com.sena.security.model.Role;
import com.sena.security.model.User;
import com.sena.security.repository.UserRepository;
import com.sena.security.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        User user = userRepository.save(User.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USUARIO) // todo registro público entra como USUARIO
                .build());

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado tras autenticación"));

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken oldToken = refreshTokenService.verifyRefreshToken(request.refreshToken());
        User user = oldToken.getUser();

        oldToken.setRevoked(true);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtService.generateToken(userDetails);

        return new AuthResponse(newAccessToken, newRefreshToken.getToken());
    }

    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        refreshTokenService.revokeAllTokens(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken.getToken());
    }
}
