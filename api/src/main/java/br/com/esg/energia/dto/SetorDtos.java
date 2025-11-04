package br.com.esg.energia.dto;

import java.math.BigDecimal;

public class SetorDtos {
    public record View(Long id, String nome, String gestor, BigDecimal metaConsumoMensal) {}
}


