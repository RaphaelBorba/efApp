package br.com.esg.energia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LeituraDtos {
    public record Create(
            @NotNull Long equipamentoId,
            @NotNull @DecimalMin("0.0") BigDecimal consumoKwh,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestampLeitura
    ) {}

    public record View(Long id, Long equipamentoId, BigDecimal consumoKwh, LocalDateTime timestampLeitura) {}
}


