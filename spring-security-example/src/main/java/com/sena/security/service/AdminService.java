package com.sena.security.service;

import com.sena.security.model.Role;
import com.sena.security.model.User;
import com.sena.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public User ascenderASoporte(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        user.setRole(Role.SOPORTE);
        return userRepository.save(user);
    }
}
