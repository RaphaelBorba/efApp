package br.com.esg.energia.service;

import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.domain.Setor;
import br.com.esg.energia.dto.EquipamentoDtos;
import br.com.esg.energia.repository.EquipamentoRepository;
import br.com.esg.energia.repository.SetorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final SetorRepository setorRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository, SetorRepository setorRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.setorRepository = setorRepository;
    }

    public List<EquipamentoDtos.View> listar(String setorId) {
        List<Equipamento> equipamentos;

        if (setorId != null) {
            if (!setorRepository.existsById(setorId)) {
                throw new IllegalArgumentException("Setor não encontrado: " + setorId);
            }
            equipamentos = equipamentoRepository.findBySetorId(setorId);
        } else {
            equipamentos = equipamentoRepository.findAll();
        }

        return equipamentos.stream()
                .map(this::toView)
                .toList();
    }

    public EquipamentoDtos.View buscarPorId(String id) {
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado: " + id));
        return toView(equipamento);
    }

    public EquipamentoDtos.View criar(EquipamentoDtos.Create dto) {
        if (dto.setorId() == null) {
            throw new IllegalArgumentException("Setor é obrigatório");
        }
        if (dto.nome() == null || dto.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (dto.potenciaNominal() == null || dto.potenciaNominal().signum() <= 0) {
            throw new IllegalArgumentException("Potência nominal deve ser maior que zero");
        }

        setorRepository.findById(dto.setorId())
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado: " + dto.setorId()));

        Equipamento equipamento = new Equipamento();
        equipamento.setSetorId(dto.setorId());
        equipamento.setNome(dto.nome());
        equipamento.setTipo(dto.tipo());
        equipamento.setPotenciaNominal(dto.potenciaNominal());

        return toView(equipamentoRepository.save(equipamento));
    }

    public EquipamentoDtos.View atualizar(String id, EquipamentoDtos.Update dto) {
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado: " + id));

        if (dto.nome() == null || dto.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (dto.potenciaNominal() == null || dto.potenciaNominal().signum() <= 0) {
            throw new IllegalArgumentException("Potência nominal deve ser maior que zero");
        }

        equipamento.setNome(dto.nome());
        equipamento.setTipo(dto.tipo());
        equipamento.setPotenciaNominal(dto.potenciaNominal());

        return toView(equipamentoRepository.save(equipamento));
    }

    public void deletar(String id) {
        if (!equipamentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Equipamento não encontrado: " + id);
        }
        equipamentoRepository.deleteById(id);
    }

    private EquipamentoDtos.View toView(Equipamento equipamento) {
        String setorNome = setorRepository.findById(equipamento.getSetorId())
                .map(Setor::getNome).orElse(null);
        return new EquipamentoDtos.View(
                equipamento.getId(),
                equipamento.getSetorId(),
                setorNome,
                equipamento.getNome(),
                equipamento.getTipo(),
                equipamento.getPotenciaNominal()
        );
    }
}
