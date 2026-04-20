package br.com.esg.energia.repository;

import br.com.esg.energia.domain.LeituraSensor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LeituraSensorRepository extends MongoRepository<LeituraSensor, String> {
    List<LeituraSensor> findByEquipamentoIdAndTimestampLeituraBetween(String equipamentoId, LocalDateTime inicio, LocalDateTime fim);
}
