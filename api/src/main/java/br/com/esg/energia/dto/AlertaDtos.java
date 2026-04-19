package br.com.esg.energia.dto;

import java.time.LocalDateTime;

public class AlertaDtos {
    public record View(String id, String tipo, String severidade, String mensagem, String setorId, String equipamentoId, LocalDateTime criadoEm) {}
}
