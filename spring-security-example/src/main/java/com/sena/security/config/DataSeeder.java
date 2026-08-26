package com.sena.security.config;

import com.sena.security.model.Role;
import com.sena.security.model.User;
import com.sena.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedIfMissing("Administrador", "admin@mesadeayuda.com", "admin123", Role.ADMIN);
        seedIfMissing("Agente de Soporte", "soporte@mesadeayuda.com", "soporte123", Role.SOPORTE);
        seedIfMissing("Usuario Demo", "usuario@mesadeayuda.com", "usuario123", Role.USUARIO);
    }

    private void seedIfMissing(String nombre, String email, String password, Role role) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .nombre(nombre)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());
        }
    }
}
