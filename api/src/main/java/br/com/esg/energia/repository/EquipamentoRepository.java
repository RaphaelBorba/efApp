package br.com.esg.energia.repository;

import br.com.esg.energia.domain.Equipamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EquipamentoRepository extends MongoRepository<Equipamento, String> {
    List<Equipamento> findBySetorId(String setorId);
}
