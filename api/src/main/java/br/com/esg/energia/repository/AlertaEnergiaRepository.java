package br.com.esg.energia.repository;

import br.com.esg.energia.domain.AlertaEnergia;
import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.domain.Setor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertaEnergiaRepository extends JpaRepository<AlertaEnergia, Long> {
    List<AlertaEnergia> findByTipoAlerta(String tipoAlerta);
    List<AlertaEnergia> findBySetor(Setor setor);
    List<AlertaEnergia> findByEquipamento(Equipamento equipamento);
    List<AlertaEnergia> findByCriadoEmBetween(LocalDateTime inicio, LocalDateTime fim);
}


