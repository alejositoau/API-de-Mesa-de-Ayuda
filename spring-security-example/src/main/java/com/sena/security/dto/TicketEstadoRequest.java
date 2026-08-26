package com.sena.security.dto;

import com.sena.security.model.enums.Estado;
import jakarta.validation.constraints.NotNull;

public record TicketEstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        Estado estado
) {}
