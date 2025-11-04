package br.com.esg.energia.dto;

import java.math.BigDecimal;

public class EquipamentoDtos {
    public record View(Long id, Long setorId, String nome, String tipo, BigDecimal potenciaNominal) {}
}
