package br.com.esg.energia.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ALERTA_ENERGIA")
public class AlertaEnergia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EQUIPAMENTO_ID")
    private Equipamento equipamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SETOR_ID")
    private Setor setor;

    @Column(name = "TIPO_ALERTA", nullable = false, length = 30)
    private String tipoAlerta;

    @Column(name = "MENSAGEM", length = 400)
    private String mensagem;

    @Column(name = "SEVERIDADE", nullable = false, length = 20)
    private String severidade;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

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

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getSeveridade() {
        return severidade;
    }

    public void setSeveridade(String severidade) {
        this.severidade = severidade;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}


