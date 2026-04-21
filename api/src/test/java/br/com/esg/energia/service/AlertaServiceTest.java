package br.com.esg.energia.service;

import br.com.esg.energia.domain.AlertaEnergia;
import br.com.esg.energia.repository.AlertaEnergiaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaEnergiaRepository alertaRepo;

    @InjectMocks
    private AlertaService service;

    private AlertaEnergia criarAlerta(String tipo, String setorId, String equipamentoId) {
        AlertaEnergia a = new AlertaEnergia();
        a.setTipoAlerta(tipo);
        a.setSetorId(setorId);
        a.setEquipamentoId(equipamentoId);
        return a;
    }

    @Test
    void consultar_semFiltros_retornaTodosAlertas() {
        List<AlertaEnergia> alertas = List.of(
                criarAlerta("CONSUMO_CRITICO", "s1", "eq1"),
                criarAlerta("META_EXCEDIDA", "s2", "eq2")
        );
        when(alertaRepo.findAll()).thenReturn(alertas);

        List<AlertaEnergia> result = service.consultar(null, null, null, null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void consultar_comTipo_filtraPorTipoAlerta() {
        List<AlertaEnergia> alertas = List.of(criarAlerta("CONSUMO_CRITICO", "s1", "eq1"));
        when(alertaRepo.findByTipoAlerta("CONSUMO_CRITICO")).thenReturn(alertas);

        List<AlertaEnergia> result = service.consultar("CONSUMO_CRITICO", null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipoAlerta()).isEqualTo("CONSUMO_CRITICO");
    }

    @Test
    void consultar_comSetorId_filtraPorSetor() {
        AlertaEnergia a1 = criarAlerta("CONSUMO_CRITICO", "s1", "eq1");
        AlertaEnergia a2 = criarAlerta("META_EXCEDIDA", "s2", "eq2");
        when(alertaRepo.findAll()).thenReturn(List.of(a1, a2));

        List<AlertaEnergia> result = service.consultar(null, "s1", null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSetorId()).isEqualTo("s1");
    }

    @Test
    void consultar_comEquipamentoId_filtraPorEquipamento() {
        AlertaEnergia a1 = criarAlerta("CONSUMO_CRITICO", "s1", "eq1");
        AlertaEnergia a2 = criarAlerta("CONSUMO_CRITICO", "s1", "eq2");
        when(alertaRepo.findAll()).thenReturn(List.of(a1, a2));

        List<AlertaEnergia> result = service.consultar(null, null, "eq1", null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEquipamentoId()).isEqualTo("eq1");
    }

    @Test
    void consultar_comFiltroTemporal_excluiAlertasForaDoPeriodo() {
        LocalDateTime agora = LocalDateTime.now();

        AlertaEnergia dentro = criarAlerta("CONSUMO_CRITICO", "s1", "eq1");
        dentro.setCriadoEm(agora);

        AlertaEnergia fora = criarAlerta("META_EXCEDIDA", "s2", "eq2");
        fora.setCriadoEm(agora.minusDays(5));

        when(alertaRepo.findAll()).thenReturn(List.of(dentro, fora));

        List<AlertaEnergia> result = service.consultar(null, null, null,
                agora.minusHours(1), agora.plusHours(1));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSetorId()).isEqualTo("s1");
    }
}
