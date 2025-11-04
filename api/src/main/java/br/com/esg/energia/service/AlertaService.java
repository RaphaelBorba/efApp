package br.com.esg.energia.service;

import br.com.esg.energia.domain.AlertaEnergia;
import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.domain.Setor;
import br.com.esg.energia.repository.AlertaEnergiaRepository;
import br.com.esg.energia.repository.EquipamentoRepository;
import br.com.esg.energia.repository.SetorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaService {

    private final AlertaEnergiaRepository alertaRepo;
    private final SetorRepository setorRepo;
    private final EquipamentoRepository equipamentoRepo;

    public AlertaService(AlertaEnergiaRepository alertaRepo, SetorRepository setorRepo, EquipamentoRepository equipamentoRepo) {
        this.alertaRepo = alertaRepo;
        this.setorRepo = setorRepo;
        this.equipamentoRepo = equipamentoRepo;
    }

    public List<AlertaEnergia> consultar(String tipo, Long setorId, Long equipamentoId, LocalDateTime inicio, LocalDateTime fim) {
        // Estratégia simples: usar filtros incrementais
        List<AlertaEnergia> base;
        if (tipo != null && !tipo.isBlank()) {
            base = alertaRepo.findByTipoAlerta(tipo);
        } else {
            base = alertaRepo.findAll();
        }

        if (setorId != null) {
            Setor setor = setorRepo.findById(setorId).orElse(null);
            if (setor != null) {
                base.removeIf(a -> a.getSetor() == null || !a.getSetor().getId().equals(setorId));
            }
        }
        if (equipamentoId != null) {
            Equipamento eq = equipamentoRepo.findById(equipamentoId).orElse(null);
            if (eq != null) {
                base.removeIf(a -> a.getEquipamento() == null || !a.getEquipamento().getId().equals(equipamentoId));
            }
        }
        if (inicio != null && fim != null) {
            base.removeIf(a -> a.getCriadoEm().isBefore(inicio) || a.getCriadoEm().isAfter(fim));
        }
        return base;
    }
}


