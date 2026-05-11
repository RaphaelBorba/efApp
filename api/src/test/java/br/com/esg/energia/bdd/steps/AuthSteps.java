package br.com.esg.energia.bdd.steps;

import br.com.esg.energia.bdd.ScenarioContext;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthSteps {

    @Autowired
    private ScenarioContext ctx;

    @Quando("eu envio uma requisição POST para {string} com usuário {string} e role {string}")
    public void postComUsuarioERole(String path, String username, String role) {
        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .body(Map.of("username", username, "roles", role))
                        .when()
                        .post(path)
        );
    }

    @Quando("eu envio uma requisição POST para {string} sem corpo")
    public void postSemCorpo(String path) {
        ctx.setLastResponse(
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .post(path)
        );
    }

    @E("a resposta deve conter um token JWT não vazio")
    public void respostaContemToken() {
        assertThat(ctx.getLastResponse().jsonPath().getString("token")).isNotBlank();
    }
}
