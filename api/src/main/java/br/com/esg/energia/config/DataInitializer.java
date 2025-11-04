package br.com.esg.energia.config;

import br.com.esg.energia.domain.AlertaEnergia;
import br.com.esg.energia.domain.Equipamento;
import br.com.esg.energia.domain.Setor;
import br.com.esg.energia.repository.AlertaEnergiaRepository;
import br.com.esg.energia.repository.EquipamentoRepository;
import br.com.esg.energia.repository.SetorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SetorRepository setorRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final AlertaEnergiaRepository alertaRepository;

    public DataInitializer(SetorRepository setorRepository, EquipamentoRepository equipamentoRepository, AlertaEnergiaRepository alertaRepository) {
        this.setorRepository = setorRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.alertaRepository = alertaRepository;
    }

    @Override
    public void run(String... args) {
        if (setorRepository.count() == 0) {
            // Criar setores
            Setor operacoes = new Setor();
            operacoes.setNome("Operações");
            operacoes.setGestor("Maria Silva");
            operacoes.setMetaConsumoMensal(new BigDecimal("5000"));
            operacoes = setorRepository.save(operacoes);

            Setor ti = new Setor();
            ti.setNome("TI");
            ti.setGestor("João Souza");
            ti.setMetaConsumoMensal(new BigDecimal("3000"));
            ti = setorRepository.save(ti);

            // Criar equipamentos
            Equipamento chiller = new Equipamento();
            chiller.setSetor(operacoes);
            chiller.setNome("Chiller 01");
            chiller.setTipo("Refrigeração");
            chiller.setPotenciaNominal(new BigDecimal("50.0"));
            equipamentoRepository.save(chiller);

            Equipamento servidor = new Equipamento();
            servidor.setSetor(ti);
            servidor.setNome("Servidor 01");
            servidor.setTipo("TI");
            servidor.setPotenciaNominal(new BigDecimal("5.0"));
            servidor = equipamentoRepository.save(servidor);

            // Criar alertas de exemplo
            AlertaEnergia alertaCritico = new AlertaEnergia();
            alertaCritico.setEquipamento(chiller);
            alertaCritico.setSetor(operacoes);
            alertaCritico.setTipoAlerta("CONSUMO_CRITICO");
            alertaCritico.setSeveridade("CRITICAL");
            alertaCritico.setMensagem("Chiller 01 operando acima de 90% da capacidade");
            alertaCritico.setCriadoEm(LocalDateTime.now().minusHours(2));
            alertaRepository.save(alertaCritico);

            AlertaEnergia alertaOciosidade = new AlertaEnergia();
            alertaOciosidade.setEquipamento(servidor);
            alertaOciosidade.setSetor(ti);
            alertaOciosidade.setTipoAlerta("OCIOSIDADE");
            alertaOciosidade.setSeveridade("WARN");
            alertaOciosidade.setMensagem("Servidor 01 com baixo consumo por período prolongado");
            alertaOciosidade.setCriadoEm(LocalDateTime.now().minusHours(1));
            alertaRepository.save(alertaOciosidade);

            AlertaEnergia alertaMeta = new AlertaEnergia();
            alertaMeta.setSetor(operacoes);
            alertaMeta.setTipoAlerta("META_EXCEDIDA");
            alertaMeta.setSeveridade("WARN");
            alertaMeta.setMensagem("Setor Operações excedeu meta mensal de consumo");
            alertaMeta.setCriadoEm(LocalDateTime.now().minusDays(1));
            alertaRepository.save(alertaMeta);
        }
    }
}
