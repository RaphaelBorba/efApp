package br.com.esg.energia.dto;

import java.time.LocalDateTime;

public class AlertaDtos {
    public record View(Long id, String tipo, String severidade, String mensagem, Long setorId, Long equipamentoId, LocalDateTime criadoEm) {}
}


