package br.com.esg.energia.bdd.steps;

import br.com.esg.energia.bdd.ScenarioContext;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;

public class GovernancaSteps {

    @Autowired
    private ScenarioContext ctx;

    @Quando("eu valido a meta mensal do setor para o período {string}")
    public void validarMetaMensal(String anoMes) {
        ctx.setLastResponse(
                given()
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .queryParam("setorId", ctx.getCurrentSetorId())
                        .queryParam("anoMes", anoMes)
                        .when()
                        .post("/governanca/validar-meta-mensal")
        );
    }

    @Quando("eu valido a meta mensal do setor {string} para o período {string}")
    public void validarMetaMensalSetorId(String setorId, String anoMes) {
        ctx.setLastResponse(
                given()
                        .header("Authorization", "Bearer " + ctx.getCurrentToken())
                        .queryParam("setorId", setorId)
                        .queryParam("anoMes", anoMes)
                        .when()
                        .post("/governanca/validar-meta-mensal")
        );
    }

    @Quando("eu valido a meta mensal do setor sem autenticação para o período {string}")
    public void validarMetaMensalSemAuth(String anoMes) {
        ctx.setLastResponse(
                given()
                        .queryParam("setorId", ctx.getCurrentSetorId())
                        .queryParam("anoMes", anoMes)
                        .when()
                        .post("/governanca/validar-meta-mensal")
        );
    }
}
