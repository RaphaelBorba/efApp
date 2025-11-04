package br.com.esg.energia.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CONSUMO_DIARIO")
public class ConsumoDiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EQUIPAMENTO_ID", nullable = false)
    private Equipamento equipamento;

    @Column(name = "DIA", nullable = false)
    private LocalDate dia;

    @Column(name = "TOTAL_KWH", nullable = false, precision = 15, scale = 6)
    private BigDecimal totalKwh;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public BigDecimal getTotalKwh() {
        return totalKwh;
    }

    public void setTotalKwh(BigDecimal totalKwh) {
        this.totalKwh = totalKwh;
    }
}


