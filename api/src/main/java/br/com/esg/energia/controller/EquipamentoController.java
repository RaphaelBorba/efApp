package br.com.esg.energia.controller;

import br.com.esg.energia.dto.EquipamentoDtos;
import br.com.esg.energia.service.EquipamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    public EquipamentoController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALISTA','GESTOR_SETOR','ADMIN')")
    public ResponseEntity<List<EquipamentoDtos.View>> listar(@RequestParam(required = false) Long setorId) {
        List<EquipamentoDtos.View> equipamentos = equipamentoService.listar(setorId);
        return ResponseEntity.ok(equipamentos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALISTA','GESTOR_SETOR','ADMIN')")
    public ResponseEntity<EquipamentoDtos.View> buscarPorId(@PathVariable Long id) {
        EquipamentoDtos.View equipamento = equipamentoService.buscarPorId(id);
        return ResponseEntity.ok(equipamento);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_SETOR','ADMIN')")
    public ResponseEntity<EquipamentoDtos.View> criar(@RequestBody EquipamentoDtos.Create dto) {
        EquipamentoDtos.View equipamento = equipamentoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(equipamento);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_SETOR','ADMIN')")
    public ResponseEntity<EquipamentoDtos.View> atualizar(
            @PathVariable Long id,
            @RequestBody EquipamentoDtos.Update dto) {
        
        EquipamentoDtos.View equipamento = equipamentoService.atualizar(id, dto);
        return ResponseEntity.ok(equipamento);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        equipamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
