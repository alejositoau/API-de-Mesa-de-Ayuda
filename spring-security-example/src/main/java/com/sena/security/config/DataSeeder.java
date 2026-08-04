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
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build());
        }
        if (!userRepository.existsByUsername("user")) {
            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build());
        }
    }
}
