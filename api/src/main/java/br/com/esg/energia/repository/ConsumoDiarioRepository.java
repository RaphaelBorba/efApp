package br.com.esg.energia.repository;

import br.com.esg.energia.domain.ConsumoDiario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsumoDiarioRepository extends MongoRepository<ConsumoDiario, String> {
    Optional<ConsumoDiario> findByEquipamentoIdAndDia(String equipamentoId, LocalDate dia);
    List<ConsumoDiario> findByEquipamentoIdInAndDiaBetween(List<String> equipamentoIds, LocalDate inicio, LocalDate fim);
}
