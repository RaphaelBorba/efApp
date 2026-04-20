package br.com.esg.energia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "setores")
public class Setor {

    @Id
    private String id;
    private String nome;
    private String gestor;
    private BigDecimal metaConsumoMensal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGestor() {
        return gestor;
    }

    public void setGestor(String gestor) {
        this.gestor = gestor;
    }

    public BigDecimal getMetaConsumoMensal() {
        return metaConsumoMensal;
    }

    public void setMetaConsumoMensal(BigDecimal metaConsumoMensal) {
        this.metaConsumoMensal = metaConsumoMensal;
    }
}
