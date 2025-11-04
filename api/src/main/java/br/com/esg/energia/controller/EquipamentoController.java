package br.com.esg.energia.controller;

import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.dto.EquipamentoDtos;
import br.com.esg.energia.repository.EquipamentoRepository;
import br.com.esg.energia.repository.SetorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoRepository equipamentoRepository;
    private final SetorRepository setorRepository;

    public EquipamentoController(EquipamentoRepository equipamentoRepository, SetorRepository setorRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.setorRepository = setorRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALISTA','GESTOR_SETOR','ADMIN')")
    public ResponseEntity<List<EquipamentoDtos.View>> listar(@RequestParam(required = false) Long setorId) {
        List<Equipamento> equipamentos;
        
        if (setorId != null) {
            var setor = setorRepository.findById(setorId)
                    .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado: " + setorId));
            equipamentos = equipamentoRepository.findBySetor(setor);
        } else {
            equipamentos = equipamentoRepository.findAll();
        }
        
        List<EquipamentoDtos.View> resp = equipamentos.stream()
                .map(e -> new EquipamentoDtos.View(
                        e.getId(), 
                        e.getSetor().getId(), 
                        e.getNome(), 
                        e.getTipo(), 
                        e.getPotenciaNominal()
                ))
                .toList();
        return ResponseEntity.ok(resp);
    }
}
