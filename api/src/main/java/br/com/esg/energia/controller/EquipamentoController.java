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
    public ResponseEntity<List<EquipamentoDtos.View>> listar(@RequestParam(required = false) String setorId) {
        return ResponseEntity.ok(equipamentoService.listar(setorId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALISTA','GESTOR_SETOR','ADMIN')")
    public ResponseEntity<EquipamentoDtos.View> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(equipamentoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_SETOR','ADMIN')")
    public ResponseEntity<EquipamentoDtos.View> criar(@RequestBody EquipamentoDtos.Create dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipamentoService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_SETOR','ADMIN')")
    public ResponseEntity<EquipamentoDtos.View> atualizar(@PathVariable String id,
                                                          @RequestBody EquipamentoDtos.Update dto) {
        return ResponseEntity.ok(equipamentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        equipamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
