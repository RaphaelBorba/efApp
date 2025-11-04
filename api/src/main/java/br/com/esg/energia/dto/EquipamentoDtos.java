package br.com.esg.energia.dto;

import java.math.BigDecimal;

public class EquipamentoDtos {
    
    public record View(
            Long id, 
            Long setorId, 
            String setorNome,
            String nome, 
            String tipo, 
            BigDecimal potenciaNominal
    ) {}
    
    public record Create(
            Long setorId,
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
