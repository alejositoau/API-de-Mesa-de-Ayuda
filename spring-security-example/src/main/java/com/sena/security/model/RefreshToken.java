package com.sena.security.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token opaco (UUID) almacenado en la BD
    @Column(nullable = false, unique = true)
    private String token;

    // Relación al usuario dueño del token
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    // Permite invalidar tokens sin borrarlos (auditoría)
    @Builder.Default
    private boolean revoked = false;
}
