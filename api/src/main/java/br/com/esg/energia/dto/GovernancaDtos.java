package br.com.esg.energia.dto;

import jakarta.validation.constraints.NotNull;

public class GovernancaDtos {
    public record ValidarMetaRequest(@NotNull String setorId, @NotNull String anoMes) {}
    public record ValidarMetaResponse(String anoMes, String setorId, java.math.BigDecimal consumoTotal) {}
}
