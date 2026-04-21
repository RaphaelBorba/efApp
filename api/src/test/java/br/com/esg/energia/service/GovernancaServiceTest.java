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
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GovernancaServiceTest {

    @Mock
    private SetorRepository setorRepo;
    @Mock
    private EquipamentoRepository equipamentoRepo;
    @Mock
    private ConsumoDiarioRepository consumoRepo;
    @Mock
    private AlertaEnergiaRepository alertaRepo;

    @InjectMocks
    private GovernancaService service;

    private Setor setor;
    private Equipamento equipamento;

    @BeforeEach
    void setUp() {
        setor = new Setor();
        setor.setId("setor-1");
        setor.setNome("TI");
        setor.setMetaConsumoMensal(new BigDecimal("100.0"));

        equipamento = new Equipamento();
        equipamento.setId("eq-1");
        equipamento.setSetorId("setor-1");
    }

    @Test
    void validarMetaMensal_consumoAbaixoMeta_naoGeraAlerta() {
        ConsumoDiario consumo = new ConsumoDiario();
        consumo.setTotalKwh(new BigDecimal("50.0"));

        when(setorRepo.findById("setor-1")).thenReturn(Optional.of(setor));
        when(equipamentoRepo.findBySetorId("setor-1")).thenReturn(List.of(equipamento));
        when(consumoRepo.findByEquipamentoIdInAndDiaBetween(any(), any(), any())).thenReturn(List.of(consumo));

        BigDecimal total = service.validarMetaMensal("setor-1", YearMonth.of(2025, 1));

        assertThat(total).isEqualByComparingTo("50.0");
        verify(alertaRepo, never()).save(any());
    }

    @Test
    void validarMetaMensal_consumoAcimaMeta_geraAlertaMetaExcedida() {
        ConsumoDiario consumo = new ConsumoDiario();
        consumo.setTotalKwh(new BigDecimal("150.0"));

        when(setorRepo.findById("setor-1")).thenReturn(Optional.of(setor));
        when(equipamentoRepo.findBySetorId("setor-1")).thenReturn(List.of(equipamento));
        when(consumoRepo.findByEquipamentoIdInAndDiaBetween(any(), any(), any())).thenReturn(List.of(consumo));

        BigDecimal total = service.validarMetaMensal("setor-1", YearMonth.of(2025, 1));

        assertThat(total).isEqualByComparingTo("150.0");
        ArgumentCaptor<AlertaEnergia> captor = ArgumentCaptor.forClass(AlertaEnergia.class);
        verify(alertaRepo).save(captor.capture());
        assertThat(captor.getValue().getTipoAlerta()).isEqualTo("META_EXCEDIDA");
        assertThat(captor.getValue().getSeveridade()).isEqualTo("WARN");
    }

    @Test
    void validarMetaMensal_setorInexistente_lancaException() {
        when(setorRepo.findById("setor-X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validarMetaMensal("setor-X", YearMonth.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Setor não encontrado");
    }

    @Test
    void validarMetaMensal_semMetaDefinida_naoGeraAlertaMesmoComConsumoAlto() {
        setor.setMetaConsumoMensal(null);
        ConsumoDiario consumo = new ConsumoDiario();
        consumo.setTotalKwh(new BigDecimal("999.0"));

        when(setorRepo.findById("setor-1")).thenReturn(Optional.of(setor));
        when(equipamentoRepo.findBySetorId("setor-1")).thenReturn(List.of(equipamento));
        when(consumoRepo.findByEquipamentoIdInAndDiaBetween(any(), any(), any())).thenReturn(List.of(consumo));

        service.validarMetaMensal("setor-1", YearMonth.of(2025, 1));

        verify(alertaRepo, never()).save(any());
    }
}
