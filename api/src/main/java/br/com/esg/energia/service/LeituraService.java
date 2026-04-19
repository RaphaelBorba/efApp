package br.com.esg.energia.service;

import br.com.esg.energia.domain.*;
import br.com.esg.energia.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeituraService {

    private final LeituraSensorRepository leituraRepo;
    private final EquipamentoRepository equipamentoRepo;
    private final AlertaEnergiaRepository alertaRepo;
    private final ConsumoDiarioRepository consumoRepo;

    public LeituraService(LeituraSensorRepository leituraRepo,
                          EquipamentoRepository equipamentoRepo,
                          AlertaEnergiaRepository alertaRepo,
                          ConsumoDiarioRepository consumoRepo) {
        this.leituraRepo = leituraRepo;
        this.equipamentoRepo = equipamentoRepo;
        this.alertaRepo = alertaRepo;
        this.consumoRepo = consumoRepo;
    }

    public LeituraSensor registrarLeitura(String equipamentoId, BigDecimal consumoKwh, LocalDateTime timestamp) {
        Equipamento equipamento = equipamentoRepo.findById(equipamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado: " + equipamentoId));

        LeituraSensor leitura = new LeituraSensor();
        leitura.setEquipamentoId(equipamentoId);
        leitura.setConsumoKwh(consumoKwh);
        leitura.setTimestampLeitura(timestamp);
        LeituraSensor salva = leituraRepo.save(leitura);

        verificarConsumoCriticoEGerarAlerta(equipamento, consumoKwh);
        atualizarConsumoDiario(equipamento.getId(), timestamp.toLocalDate(), consumoKwh);

        return salva;
    }

    private void verificarConsumoCriticoEGerarAlerta(Equipamento equipamento, BigDecimal consumoKwh) {
        if (equipamento.getPotenciaNominal() == null) {
            return;
        }
        BigDecimal limiar = equipamento.getPotenciaNominal().multiply(BigDecimal.valueOf(0.9));
        if (consumoKwh.compareTo(limiar) >= 0) {
            AlertaEnergia alerta = new AlertaEnergia();
            alerta.setEquipamentoId(equipamento.getId());
            alerta.setSetorId(equipamento.getSetorId());
            alerta.setTipoAlerta("CONSUMO_CRITICO");
            alerta.setSeveridade("CRITICAL");
            alerta.setMensagem("Consumo em kWh acima de 90% da potência nominal");
            alertaRepo.save(alerta);
        }
    }

    private void atualizarConsumoDiario(String equipamentoId, LocalDate dia, BigDecimal deltaKwh) {
        ConsumoDiario consumo = consumoRepo.findByEquipamentoIdAndDia(equipamentoId, dia)
                .orElseGet(() -> {
                    ConsumoDiario c = new ConsumoDiario();
                    c.setEquipamentoId(equipamentoId);
                    c.setDia(dia);
                    c.setTotalKwh(BigDecimal.ZERO);
                    return c;
                });
        consumo.setTotalKwh(consumo.getTotalKwh().add(deltaKwh));
        consumoRepo.save(consumo);
    }

    public List<LeituraSensor> listarLeituras(String equipamentoId, LocalDateTime inicio, LocalDateTime fim) {
        if (!equipamentoRepo.existsById(equipamentoId)) {
            throw new IllegalArgumentException("Equipamento não encontrado: " + equipamentoId);
        }
        return leituraRepo.findByEquipamentoIdAndTimestampLeituraBetween(equipamentoId, inicio, fim);
    }
}
