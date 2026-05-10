package br.com.esg.energia.bdd.steps;

import br.com.esg.energia.bdd.ScenarioContext;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class EquipamentoSteps {

    @Autowired
    private ScenarioContext ctx;

    @Quando("eu cadastro um equipamento com nome {string} e potência {double} kW")
    public void cadastrarEquipamento(String nome, double potencia) {
        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .body(Map.of(
                                "setorId", ctx.getCurrentSetorId(),
                                "nome", nome,
                                "tipo", "SERVIDOR",
                                "potenciaNominal", potencia
                        ))
                        .when()
                        .post("/equipamentos")
        );
    }

    @Quando("eu envio um POST para {string} sem informar o setor")
    public void postSemSetor(String path) {
        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .body(Map.of("nome", "Equipamento Sem Setor", "tipo", "TESTE", "potenciaNominal", 5.0))
                        .when()
                        .post(path)
        );
    }

    @E("a resposta deve conter o id do equipamento criado")
    public void respostaContemIdEquipamento() {
        assertThat(ctx.getLastResponse().jsonPath().getString("id")).isNotBlank();
    }
}
