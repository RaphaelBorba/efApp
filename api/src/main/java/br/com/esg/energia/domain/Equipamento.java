package br.com.esg.energia.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "EQUIPAMENTO")
public class Equipamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SETOR_ID", nullable = false)
    private Setor setor;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "TIPO", length = 60)
    private String tipo;

    @Column(name = "POTENCIA_NOMINAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal potenciaNominal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
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


