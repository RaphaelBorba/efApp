package br.com.esg.energia.service;

import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.domain.Setor;
import br.com.esg.energia.dto.EquipamentoDtos;
import br.com.esg.energia.repository.EquipamentoRepository;
import br.com.esg.energia.repository.SetorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipamentoServiceTest {

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @Mock
    private SetorRepository setorRepository;

    @InjectMocks
    private EquipamentoService service;

    private Equipamento equipamento;
    private Setor setor;

    @BeforeEach
    void setUp() {
        setor = new Setor();
        setor.setId("setor-1");
        setor.setNome("TI");

        equipamento = new Equipamento();
        equipamento.setId("eq-1");
        equipamento.setSetorId("setor-1");
        equipamento.setNome("Servidor");
        equipamento.setTipo("SERVIDOR");
        equipamento.setPotenciaNominal(new BigDecimal("10.0"));
    }

    @Test
    void listar_semFiltro_retornaTodosEquipamentos() {
        when(equipamentoRepository.findAll()).thenReturn(List.of(equipamento));
        when(setorRepository.findById("setor-1")).thenReturn(Optional.of(setor));

        List<EquipamentoDtos.View> result = service.listar(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nome()).isEqualTo("Servidor");
    }

    @Test
    void listar_comSetorIdValido_retornaEquipamentosDoSetor() {
        when(setorRepository.existsById("setor-1")).thenReturn(true);
        when(equipamentoRepository.findBySetorId("setor-1")).thenReturn(List.of(equipamento));
        when(setorRepository.findById("setor-1")).thenReturn(Optional.of(setor));

        List<EquipamentoDtos.View> result = service.listar("setor-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).setorId()).isEqualTo("setor-1");
    }

    @Test
    void listar_comSetorIdInexistente_lancaException() {
        when(setorRepository.existsById("setor-X")).thenReturn(false);

        assertThatThrownBy(() -> service.listar("setor-X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Setor não encontrado");
    }

    @Test
    void buscarPorId_existente_retornaView() {
        when(equipamentoRepository.findById("eq-1")).thenReturn(Optional.of(equipamento));
        when(setorRepository.findById("setor-1")).thenReturn(Optional.of(setor));

        EquipamentoDtos.View result = service.buscarPorId("eq-1");

        assertThat(result.id()).isEqualTo("eq-1");
        assertThat(result.setorNome()).isEqualTo("TI");
    }

    @Test
    void buscarPorId_inexistente_lancaException() {
        when(equipamentoRepository.findById("eq-X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("eq-X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Equipamento não encontrado");
    }

    @Test
    void criar_valido_persisteERetornaView() {
        EquipamentoDtos.Create dto = new EquipamentoDtos.Create("setor-1", "Ar Condicionado", "AC", new BigDecimal("5.0"));
        when(setorRepository.findById("setor-1")).thenReturn(Optional.of(setor));
        when(equipamentoRepository.save(any())).thenReturn(equipamento);

        EquipamentoDtos.View result = service.criar(dto);

        assertThat(result).isNotNull();
        verify(equipamentoRepository).save(any());
    }

    @Test
    void criar_setorNulo_lancaException() {
        EquipamentoDtos.Create dto = new EquipamentoDtos.Create(null, "Ar", "AC", new BigDecimal("5.0"));

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Setor é obrigatório");
    }

    @Test
    void criar_potenciaNaoPositiva_lancaException() {
        EquipamentoDtos.Create dto = new EquipamentoDtos.Create("setor-1", "Ar", "AC", BigDecimal.ZERO);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Potência nominal");
    }

    @Test
    void deletar_existente_removeDoBanco() {
        when(equipamentoRepository.existsById("eq-1")).thenReturn(true);

        service.deletar("eq-1");

        verify(equipamentoRepository).deleteById("eq-1");
    }

    @Test
    void deletar_inexistente_lancaException() {
        when(equipamentoRepository.existsById("eq-X")).thenReturn(false);

        assertThatThrownBy(() -> service.deletar("eq-X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Equipamento não encontrado");
    }
}
