package com.sena.security.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.sena.security.model.enums.Estado;
import com.sena.security.model.enums.Prioridad;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ABIERTO;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "sla_vence_en")
    private LocalDateTime slaVenceEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User creadoPor;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = Estado.ABIERTO;
        }
    }
}