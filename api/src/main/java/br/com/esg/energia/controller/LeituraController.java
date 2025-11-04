package br.com.esg.energia.controller;

import br.com.esg.energia.domain.LeituraSensor;
import br.com.esg.energia.dto.LeituraDtos;
import br.com.esg.energia.service.LeituraService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/leituras")
@Validated
public class LeituraController {

    private final LeituraService leituraService;

    public LeituraController(LeituraService leituraService) {
        this.leituraService = leituraService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('IOT_GATEWAY','ADMIN')")
    public ResponseEntity<LeituraDtos.View> criar(@Valid @RequestBody LeituraDtos.Create req) {
        LeituraSensor l = leituraService.registrarLeitura(req.equipamentoId(), req.consumoKwh(), req.timestampLeitura());
        return ResponseEntity.ok(new LeituraDtos.View(l.getId(), l.getEquipamento().getId(), l.getConsumoKwh(), l.getTimestampLeitura()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALISTA','GESTOR_SETOR','ADMIN')")
    public ResponseEntity<List<LeituraDtos.View>> listar(@RequestParam Long equipamentoId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<LeituraDtos.View> resp = leituraService.listarLeituras(equipamentoId, inicio, fim).stream()
                .map(l -> new LeituraDtos.View(l.getId(), l.getEquipamento().getId(), l.getConsumoKwh(), l.getTimestampLeitura()))
                .toList();
        return ResponseEntity.ok(resp);
    }
}


