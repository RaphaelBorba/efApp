package br.com.esg.energia.bdd.steps;

import br.com.esg.energia.bdd.ScenarioContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class LeituraSteps {

    @Autowired
    private ScenarioContext ctx;

    @Dado("que existe um equipamento com potência nominal de {double} kW")
    public void criarEquipamentoComPotencia(double potencia) {
        String id = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + ctx.getSystemToken())
                .body(Map.of(
                        "setorId", ctx.getCurrentSetorId(),
                        "nome", "Equipamento BDD " + potencia + "kW",
                        "tipo", "TESTE",
                        "potenciaNominal", potencia
                ))
                .when()
                .post("/equipamentos")
                .jsonPath().getString("id");
        ctx.setCurrentEquipamentoId(id);
    }

    @Quando("eu registro uma leitura de {double} kWh para o equipamento")
    public void registrarLeitura(double consumo) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .body(Map.of(
                                "equipamentoId", ctx.getCurrentEquipamentoId(),
                                "consumoKwh", consumo,
                                "timestampLeitura", ts
                        ))
                        .when()
                        .post("/leituras")
        );
    }

    @Quando("eu registro uma leitura para o equipamento {string} com {double} kWh")
    public void registrarLeituraEquipamentoId(String equipamentoId, double consumo) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .body(Map.of(
                                "equipamentoId", equipamentoId,
                                "consumoKwh", consumo,
                                "timestampLeitura", ts
                        ))
                        .when()
                        .post("/leituras")
        );
    }

    @Quando("eu busco as leituras do equipamento")
    public void buscarLeiturasDoEquipamento() {
        String inicio = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String fim = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ctx.setLastResponse(
                given()
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .queryParam("equipamentoId", ctx.getCurrentEquipamentoId())
                        .queryParam("inicio", inicio)
                        .queryParam("fim", fim)
                        .when()
                        .get("/leituras")
        );
    }

    @E("a resposta deve conter o id da leitura registrada")
    public void respostaContemIdLeitura() {
        assertThat(ctx.getLastResponse().jsonPath().getString("id")).isNotBlank();
    }

    @E("um alerta do tipo {string} deve ter sido gerado para o equipamento")
    public void alertaDeveExistir(String tipoAlerta) {
        List<Map<String, Object>> alertas = given()
                .header("Authorization", "Bearer " + ctx.getCurrentToken())
                .queryParam("tipo", tipoAlerta)
                .queryParam("equipamentoId", ctx.getCurrentEquipamentoId())
                .when()
                .get("/alertas")
                .jsonPath().getList("$");

        assertThat(alertas).isNotEmpty();
        assertThat(alertas.get(0).get("tipo")).isEqualTo(tipoAlerta);
    }
}
