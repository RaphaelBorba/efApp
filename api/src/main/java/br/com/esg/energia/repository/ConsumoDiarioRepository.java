package br.com.esg.energia.repository;

import br.com.esg.energia.domain.ConsumoDiario;
import br.com.esg.energia.domain.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ConsumoDiarioRepository extends JpaRepository<ConsumoDiario, Long> {
    Optional<ConsumoDiario> findByEquipamentoAndDia(Equipamento equipamento, LocalDate dia);

    @Query("select coalesce(sum(c.totalKwh), 0) from ConsumoDiario c where c.equipamento.id in (:equipIds) and c.dia between :inicio and :fim")
    java.math.BigDecimal somarConsumoPorEquipamentosEPeriodo(@Param("equipIds") java.util.List<Long> equipamentoIds,
                                                             @Param("inicio") LocalDate inicio,
                                                             @Param("fim") LocalDate fim);
}


