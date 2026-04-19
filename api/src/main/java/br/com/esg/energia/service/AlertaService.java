package br.com.esg.energia.service;

import br.com.esg.energia.domain.AlertaEnergia;
import br.com.esg.energia.repository.AlertaEnergiaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertaService {

    private final AlertaEnergiaRepository alertaRepo;

    public AlertaService(AlertaEnergiaRepository alertaRepo) {
        this.alertaRepo = alertaRepo;
    }

    public List<AlertaEnergia> consultar(String tipo, String setorId, String equipamentoId, LocalDateTime inicio, LocalDateTime fim) {
        List<AlertaEnergia> base;
        if (tipo != null && !tipo.isBlank()) {
            base = new ArrayList<>(alertaRepo.findByTipoAlerta(tipo));
        } else {
            base = new ArrayList<>(alertaRepo.findAll());
        }

        if (setorId != null) {
            base.removeIf(a -> !setorId.equals(a.getSetorId()));
        }
        if (equipamentoId != null) {
            base.removeIf(a -> !equipamentoId.equals(a.getEquipamentoId()));
        }
        if (inicio != null && fim != null) {
            base.removeIf(a -> a.getCriadoEm().isBefore(inicio) || a.getCriadoEm().isAfter(fim));
        }
        return base;
    }
}
