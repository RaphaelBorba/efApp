package br.com.esg.energia.service;

import br.com.esg.energia.domain.*;
import br.com.esg.energia.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeituraServiceTest {

    @Mock
    private LeituraSensorRepository leituraRepo;
    @Mock
    private EquipamentoRepository equipamentoRepo;
    @Mock
    private AlertaEnergiaRepository alertaRepo;
    @Mock
    private ConsumoDiarioRepository consumoRepo;

    @InjectMocks
    private LeituraService service;

    private Equipamento equipamento;

    @BeforeEach
    void setUp() {
        equipamento = new Equipamento();
        equipamento.setId("eq-1");
        equipamento.setSetorId("setor-1");
        equipamento.setNome("Servidor");
        equipamento.setPotenciaNominal(new BigDecimal("10.0"));
    }

    @Test
    void registrarLeitura_consumoAbaixo90Porcento_naoGeraAlerta() {
        when(equipamentoRepo.findById("eq-1")).thenReturn(Optional.of(equipamento));
        when(leituraRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consumoRepo.findByEquipamentoIdAndDia(any(), any())).thenReturn(Optional.empty());
        when(consumoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // 8.0 < 9.0 (90% de 10.0) → sem alerta
        service.registrarLeitura("eq-1", new BigDecimal("8.0"), LocalDateTime.now());

        verify(alertaRepo, never()).save(any());
    }

    @Test
    void registrarLeitura_consumoAcima90Porcento_geraAlertaCritico() {
        when(equipamentoRepo.findById("eq-1")).thenReturn(Optional.of(equipamento));
        when(leituraRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consumoRepo.findByEquipamentoIdAndDia(any(), any())).thenReturn(Optional.empty());
        when(consumoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // 9.5 >= 9.0 (90% de 10.0) → gera alerta
        service.registrarLeitura("eq-1", new BigDecimal("9.5"), LocalDateTime.now());

        ArgumentCaptor<AlertaEnergia> captor = ArgumentCaptor.forClass(AlertaEnergia.class);
        verify(alertaRepo).save(captor.capture());
        assertThat(captor.getValue().getTipoAlerta()).isEqualTo("CONSUMO_CRITICO");
        assertThat(captor.getValue().getSeveridade()).isEqualTo("CRITICAL");
    }

    @Test
    void registrarLeitura_acumulaConsumoDiario() {
        ConsumoDiario existente = new ConsumoDiario();
        existente.setEquipamentoId("eq-1");
        existente.setTotalKwh(new BigDecimal("5.0"));

        when(equipamentoRepo.findById("eq-1")).thenReturn(Optional.of(equipamento));
        when(leituraRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consumoRepo.findByEquipamentoIdAndDia(any(), any())).thenReturn(Optional.of(existente));
        when(consumoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // consumo abaixo de 90% para não gerar alerta
        service.registrarLeitura("eq-1", new BigDecimal("3.0"), LocalDateTime.now());

        ArgumentCaptor<ConsumoDiario> captor = ArgumentCaptor.forClass(ConsumoDiario.class);
        verify(consumoRepo).save(captor.capture());
        assertThat(captor.getValue().getTotalKwh()).isEqualByComparingTo("8.0");
    }

    @Test
    void registrarLeitura_equipamentoInexistente_lancaException() {
        when(equipamentoRepo.findById("eq-X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarLeitura("eq-X", BigDecimal.ONE, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Equipamento não encontrado");
    }

    @Test
    void listarLeituras_equipamentoInexistente_lancaException() {
        when(equipamentoRepo.existsById("eq-X")).thenReturn(false);

        assertThatThrownBy(() -> service.listarLeituras("eq-X",
                LocalDateTime.now().minusDays(1), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Equipamento não encontrado");
    }
}
