package br.com.esg.energia.repository;

import br.com.esg.energia.domain.AlertaEnergia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertaEnergiaRepository extends MongoRepository<AlertaEnergia, String> {
    List<AlertaEnergia> findByTipoAlerta(String tipoAlerta);
    List<AlertaEnergia> findBySetorId(String setorId);
    List<AlertaEnergia> findByEquipamentoId(String equipamentoId);
    List<AlertaEnergia> findByCriadoEmBetween(LocalDateTime inicio, LocalDateTime fim);
}
