package br.com.esg.energia.repository;

import br.com.esg.energia.domain.Setor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SetorRepository extends MongoRepository<Setor, String> {
}
