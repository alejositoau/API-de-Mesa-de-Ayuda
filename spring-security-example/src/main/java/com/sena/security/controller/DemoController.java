package com.sena.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "usuario", authentication.getName(),
                "rol", authentication.getAuthorities().toString()
        ));
    }

    @GetMapping("/admin/panel")
    public ResponseEntity<String> adminPanel(Authentication authentication) {
        return ResponseEntity.ok("Bienvenido al panel de administrador, " + authentication.getName());
    }
}
