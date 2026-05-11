package br.com.esg.energia.bdd.steps;

import br.com.esg.energia.bdd.ScenarioContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    @Autowired
    private ScenarioContext ctx;

    @Dado("que estou autenticado como {string}")
    public void queEstouAutenticadoComo(String username) {
        String token = given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "roles", "ADMIN"))
                .when()
                .post("/auth/token")
                .jsonPath().getString("token");
        ctx.setCurrentToken(token);
    }

    @Quando("eu faço uma requisição GET para {string}")
    public void euFacoGetPara(String path) {
        ctx.setLastResponse(
                given()
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .when()
                        .get(path)
        );
    }

    @Quando("eu faço uma requisição GET para {string} sem token de autenticação")
    public void euFacoGetSemToken(String path) {
        ctx.setLastResponse(given().when().get(path));
    }

    @Quando("eu faço uma requisição GET para {string} com token inválido")
    public void euFacoGetComTokenInvalido(String path) {
        ctx.setLastResponse(
                given()
                        .header("Authorization", "Bearer token.invalido.assinatura")
                        .when()
                        .get(path)
        );
    }

    @Então("o status code da resposta deve ser {int}")
    public void oStatusCodeDeveSer(int expectedStatus) {
        assertThat(ctx.getLastResponse().statusCode()).isEqualTo(expectedStatus);
    }

    @E("a resposta deve ser uma lista JSON")
    public void aRespostaDeveSerUmaListaJson() {
        ctx.getLastResponse().then().contentType(ContentType.JSON);
        assertThat(ctx.getLastResponse().jsonPath().getList("$")).isNotNull();
    }

    @E("a resposta deve seguir o schema de autenticação")
    public void schemaAutenticacao() {
        ctx.getLastResponse().then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/auth-schema.json"));
    }

    @E("a resposta deve seguir o schema de leitura")
    public void schemaLeitura() {
        ctx.getLastResponse().then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/leitura-schema.json"));
    }

    @E("a resposta deve seguir o schema de equipamento")
    public void schemaEquipamento() {
        ctx.getLastResponse().then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/equipamento-list-schema.json"));
    }

    @E("a resposta deve seguir o schema de equipamento individual")
    public void schemaEquipamentoIndividual() {
        ctx.getLastResponse().then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/equipamento-schema.json"));
    }

    @E("a resposta deve seguir o schema de setor")
    public void schemaSetor() {
        ctx.getLastResponse().then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/setor-list-schema.json"));
    }

    @E("a resposta deve seguir o schema de alerta")
    public void schemaAlerta() {
        ctx.getLastResponse().then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/alerta-list-schema.json"));
    }

    @E("a resposta deve seguir o schema de governança")
    public void schemaGovernanca() {
        ctx.getLastResponse().then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/governanca-schema.json"));
    }
}
