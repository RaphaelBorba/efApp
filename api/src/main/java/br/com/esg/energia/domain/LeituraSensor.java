package br.com.esg.energia.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "LEITURA_SENSOR")
public class LeituraSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EQUIPAMENTO_ID", nullable = false)
    private Equipamento equipamento;

    @Column(name = "CONSUMO_KWH", nullable = false, precision = 15, scale = 6)
    private BigDecimal consumoKwh;

    @Column(name = "TS_LEITURA", nullable = false)
    private LocalDateTime timestampLeitura;

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

    public BigDecimal getConsumoKwh() {
        return consumoKwh;
    }

    public void setConsumoKwh(BigDecimal consumoKwh) {
        this.consumoKwh = consumoKwh;
    }

    public LocalDateTime getTimestampLeitura() {
        return timestampLeitura;
    }

    public void setTimestampLeitura(LocalDateTime timestampLeitura) {
        this.timestampLeitura = timestampLeitura;
    }
}


