package br.com.esg.energia.repository;

import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.domain.Setor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    List<Equipamento> findBySetor(Setor setor);
}


