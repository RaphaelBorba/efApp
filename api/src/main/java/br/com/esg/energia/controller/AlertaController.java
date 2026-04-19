package br.com.esg.energia.controller;

import br.com.esg.energia.domain.AlertaEnergia;
import br.com.esg.energia.dto.AlertaDtos;
import br.com.esg.energia.service.AlertaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALISTA','GESTOR_SETOR','ADMIN')")
    public ResponseEntity<List<AlertaDtos.View>> consultar(@RequestParam(required = false) String tipo,
                                                           @RequestParam(required = false) String setorId,
                                                           @RequestParam(required = false) String equipamentoId,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<AlertaEnergia> lista = alertaService.consultar(tipo, setorId, equipamentoId, inicio, fim);
        List<AlertaDtos.View> resp = lista.stream()
                .map(a -> new AlertaDtos.View(
                        a.getId(), a.getTipoAlerta(), a.getSeveridade(), a.getMensagem(),
                        a.getSetorId(), a.getEquipamentoId(), a.getCriadoEm()
                ))
                .toList();
        return ResponseEntity.ok(resp);
    }
}
