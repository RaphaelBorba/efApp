package br.com.esg.energia.controller;

import br.com.esg.energia.domain.Setor;
import br.com.esg.energia.dto.SetorDtos;
import br.com.esg.energia.repository.SetorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setores")
public class SetorController {

    private final SetorRepository setorRepository;

    public SetorController(SetorRepository setorRepository) {
        this.setorRepository = setorRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALISTA','GESTOR_SETOR','ADMIN')")
    public ResponseEntity<List<SetorDtos.View>> listar() {
        List<SetorDtos.View> resp = setorRepository.findAll().stream()
                .map(s -> new SetorDtos.View(s.getId(), s.getNome(), s.getGestor(), s.getMetaConsumoMensal()))
                .toList();
        return ResponseEntity.ok(resp);
    }
}


