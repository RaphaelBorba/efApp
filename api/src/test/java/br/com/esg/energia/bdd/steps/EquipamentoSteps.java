package br.com.esg.energia.bdd.steps;

import br.com.esg.energia.bdd.ScenarioContext;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
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
        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Equipamento Sem Setor");
        body.put("tipo", "TESTE");
        body.put("potenciaNominal", 5.0);

        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .body(body)
                        .when()
                        .post(path)
        );
    }

    @Quando("eu atualizo o equipamento atual com nome {string} e potência {double} kW")
    public void atualizarEquipamentoAtual(String nome, double potencia) {
        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .body(Map.of(
                                "nome", nome,
                                "tipo", "SERVIDOR",
                                "potenciaNominal", potencia
                        ))
                        .when()
                        .put("/equipamentos/" + ctx.getCurrentEquipamentoId())
        );
    }

    @Quando("eu atualizo o equipamento {string} com nome {string} e potência {double} kW")
    public void atualizarEquipamentoPorId(String id, String nome, double potencia) {
        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .body(Map.of(
                                "nome", nome,
                                "tipo", "SERVIDOR",
                                "potenciaNominal", potencia
                        ))
                        .when()
                        .put("/equipamentos/" + id)
        );
    }

    @Quando("eu removo o equipamento atual")
    public void removerEquipamentoAtual() {
        ctx.setLastResponse(
                given()
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .when()
                        .delete("/equipamentos/" + ctx.getCurrentEquipamentoId())
        );
    }

    @Quando("eu removo o equipamento {string}")
    public void removerEquipamentoPorId(String id) {
        ctx.setLastResponse(
                given()
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .when()
                        .delete("/equipamentos/" + id)
        );
    }

    @E("a resposta deve conter o id do equipamento criado")
    public void respostaContemIdEquipamento() {
        assertThat(ctx.getLastResponse().jsonPath().getString("id")).isNotBlank();
    }

    @E("a resposta deve conter o nome {string}")
    public void respostaContemNome(String nome) {
        assertThat(ctx.getLastResponse().jsonPath().getString("nome")).isEqualTo(nome);
    }
}
