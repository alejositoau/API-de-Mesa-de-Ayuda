package com.sena.security.controller;

import com.sena.security.dto.AscenderRequest;
import com.sena.security.model.User;
import com.sena.security.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/soporte")
    public ResponseEntity<Map<String, String>> ascenderASoporte(@Valid @RequestBody AscenderRequest request) {
        User user = adminService.ascenderASoporte(request.email());
        return ResponseEntity.ok(Map.of("mensaje", "Usuario ascendido a SOPORTE", "email", user.getEmail()));
    }
}
