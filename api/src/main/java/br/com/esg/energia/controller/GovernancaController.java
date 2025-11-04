package br.com.esg.energia.controller;

import br.com.esg.energia.dto.GovernancaDtos;
import br.com.esg.energia.service.GovernancaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/governanca")
@Validated
public class GovernancaController {

    private final GovernancaService governancaService;

    public GovernancaController(GovernancaService governancaService) {
        this.governancaService = governancaService;
    }

    @PostMapping("/validar-meta-mensal")
    @PreAuthorize("hasAnyRole('GESTOR_SETOR','ADMIN')")
    public ResponseEntity<GovernancaDtos.ValidarMetaResponse> validar(@RequestParam Long setorId,
                                                                      @RequestParam String anoMes) {
        YearMonth ym = YearMonth.parse(anoMes);
        var consumo = governancaService.validarMetaMensal(setorId, ym);
        return ResponseEntity.ok(new GovernancaDtos.ValidarMetaResponse(anoMes, setorId, consumo));
    }
}


