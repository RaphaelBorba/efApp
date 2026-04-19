package br.com.esg.energia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "leituras_sensor")
public class LeituraSensor {

    @Id
    private String id;
    private String equipamentoId;
    private BigDecimal consumoKwh;
    private LocalDateTime timestampLeitura;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEquipamentoId() {
        return equipamentoId;
    }

    public void setEquipamentoId(String equipamentoId) {
        this.equipamentoId = equipamentoId;
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
