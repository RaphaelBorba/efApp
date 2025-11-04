package br.com.esg.energia.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "SETOR")
public class Setor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "GESTOR", length = 100)
    private String gestor;

    @Column(name = "META_CONSUMO_MENSAL", nullable = false, precision = 15, scale = 2)
    private BigDecimal metaConsumoMensal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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


