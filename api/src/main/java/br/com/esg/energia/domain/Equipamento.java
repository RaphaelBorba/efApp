package br.com.esg.energia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "equipamentos")
public class Equipamento {

    @Id
    private String id;
    private String setorId;
    private String nome;
    private String tipo;
    private BigDecimal potenciaNominal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSetorId() {
        return setorId;
    }

    public void setSetorId(String setorId) {
        this.setorId = setorId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPotenciaNominal() {
        return potenciaNominal;
    }

    public void setPotenciaNominal(BigDecimal potenciaNominal) {
        this.potenciaNominal = potenciaNominal;
    }
}
