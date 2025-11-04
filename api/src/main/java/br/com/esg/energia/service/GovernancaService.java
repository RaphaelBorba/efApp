package br.com.esg.energia.service;

import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.domain.Setor;
import br.com.esg.energia.domain.AlertaEnergia;
import br.com.esg.energia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GovernancaService {

    private final SetorRepository setorRepo;
    private final EquipamentoRepository equipamentoRepo;
    private final ConsumoDiarioRepository consumoRepo;
    private final AlertaEnergiaRepository alertaRepo;

    public GovernancaService(SetorRepository setorRepo,
                             EquipamentoRepository equipamentoRepo,
                             ConsumoDiarioRepository consumoRepo,
                             AlertaEnergiaRepository alertaRepo) {
        this.setorRepo = setorRepo;
        this.equipamentoRepo = equipamentoRepo;
        this.consumoRepo = consumoRepo;
        this.alertaRepo = alertaRepo;
    }

    @Transactional
    public BigDecimal validarMetaMensal(Long setorId, YearMonth anoMes) {
        Setor setor = setorRepo.findById(setorId)
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado: " + setorId));

        List<Equipamento> equipamentos = equipamentoRepo.findBySetor(setor);
        List<Long> equipamentoIds = equipamentos.stream().map(Equipamento::getId).collect(Collectors.toList());
        LocalDate inicio = anoMes.atDay(1);
        LocalDate fim = anoMes.atEndOfMonth();

        BigDecimal consumoTotal = consumoRepo.somarConsumoPorEquipamentosEPeriodo(equipamentoIds, inicio, fim);
        if (consumoTotal.compareTo(setor.getMetaConsumoMensal()) > 0) {
            AlertaEnergia alerta = new AlertaEnergia();
            alerta.setSetor(setor);
            alerta.setTipoAlerta("META_EXCEDIDA");
            alerta.setSeveridade("WARN");
            alerta.setMensagem("Consumo do setor no mês excedeu a meta");
            alertaRepo.save(alerta);
        }
        return consumoTotal;
    }
}


