package br.com.esg.energia.dto;

import java.math.BigDecimal;

public class EquipamentoDtos {

    public record View(
            String id,
            String setorId,
            String setorNome,
            String nome,
            String tipo,
            BigDecimal potenciaNominal
    ) {}

    public record Create(
            String setorId,
            String nome,
            String tipo,
            BigDecimal potenciaNominal
    ) {}

    public record Update(
            String nome,
            String tipo,
            BigDecimal potenciaNominal
    ) {}
}
