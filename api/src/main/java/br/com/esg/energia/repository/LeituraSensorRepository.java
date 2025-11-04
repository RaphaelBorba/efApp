package br.com.esg.energia.repository;

import br.com.esg.energia.domain.LeituraSensor;
import br.com.esg.energia.domain.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {
    List<LeituraSensor> findByEquipamentoAndTimestampLeituraBetween(Equipamento equipamento, LocalDateTime inicio, LocalDateTime fim);

    @Query("select l from LeituraSensor l where l.timestampLeitura between :inicio and :fim and l.equipamento = :equip")
    List<LeituraSensor> buscarPorPeriodo(@Param("equip") Equipamento equipamento,
                                         @Param("inicio") LocalDateTime inicio,
                                         @Param("fim") LocalDateTime fim);
}


