package com.kinormotors.dto.turno;

import com.kinormotors.enums.EstadoTurno;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoTurnoRequest(@NotNull EstadoTurno estado) {
}
