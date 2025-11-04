package br.com.esg.energia.service;

import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.domain.Setor;
import br.com.esg.energia.dto.EquipamentoDtos;
import br.com.esg.energia.repository.EquipamentoRepository;
import br.com.esg.energia.repository.SetorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final SetorRepository setorRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository, SetorRepository setorRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.setorRepository = setorRepository;
    }

    public List<EquipamentoDtos.View> listar(Long setorId) {
        List<Equipamento> equipamentos;
        
        if (setorId != null) {
            Setor setor = setorRepository.findById(setorId)
                    .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado: " + setorId));
            equipamentos = equipamentoRepository.findBySetor(setor);
        } else {
            equipamentos = equipamentoRepository.findAll();
        }
        
        return equipamentos.stream()
                .map(this::toView)
                .toList();
    }

    public EquipamentoDtos.View buscarPorId(Long id) {
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado: " + id));
        
        return toView(equipamento);
    }

    @Transactional
    public EquipamentoDtos.View criar(EquipamentoDtos.Create dto) {
        // Validações
        if (dto.setorId() == null) {
            throw new IllegalArgumentException("Setor é obrigatório");
        }
        if (dto.nome() == null || dto.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (dto.potenciaNominal() == null || dto.potenciaNominal().signum() <= 0) {
            throw new IllegalArgumentException("Potência nominal deve ser maior que zero");
        }
        
        Setor setor = setorRepository.findById(dto.setorId())
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado: " + dto.setorId()));
        
        Equipamento equipamento = new Equipamento();
        equipamento.setSetor(setor);
        equipamento.setNome(dto.nome());
        equipamento.setTipo(dto.tipo());
        equipamento.setPotenciaNominal(dto.potenciaNominal());
        
        Equipamento salvo = equipamentoRepository.save(equipamento);
        
        return toView(salvo);
    }

    @Transactional
    public EquipamentoDtos.View atualizar(Long id, EquipamentoDtos.Update dto) {
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado: " + id));
        
        // Validações
        if (dto.nome() == null || dto.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (dto.potenciaNominal() == null || dto.potenciaNominal().signum() <= 0) {
            throw new IllegalArgumentException("Potência nominal deve ser maior que zero");
        }
        
        equipamento.setNome(dto.nome());
        equipamento.setTipo(dto.tipo());
        equipamento.setPotenciaNominal(dto.potenciaNominal());
        
        Equipamento atualizado = equipamentoRepository.save(equipamento);
        
        return toView(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!equipamentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Equipamento não encontrado: " + id);
        }
        
        equipamentoRepository.deleteById(id);
    }

    private EquipamentoDtos.View toView(Equipamento equipamento) {
        return new EquipamentoDtos.View(
                equipamento.getId(),
                equipamento.getSetor().getId(),
                equipamento.getSetor().getNome(),
                equipamento.getNome(),
                equipamento.getTipo(),
                equipamento.getPotenciaNominal()
        );
    }
}

