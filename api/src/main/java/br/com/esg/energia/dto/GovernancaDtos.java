package br.com.esg.energia.dto;

import jakarta.validation.constraints.NotNull;

public class GovernancaDtos {
    public record ValidarMetaRequest(@NotNull Long setorId, @NotNull String anoMes) {}
    public record ValidarMetaResponse(String anoMes, Long setorId, java.math.BigDecimal consumoTotal) {}
}


