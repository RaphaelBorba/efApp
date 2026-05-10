package br.com.esg.energia.bdd;

import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class SetupHooks {

    @LocalServerPort
    private int port;

    @Autowired
    private ScenarioContext ctx;

    @Before
    public void beforeScenario() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "";

        String token = given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "system", "roles", "ADMIN"))
                .when()
                .post("/auth/token")
                .jsonPath().getString("token");

        ctx.setSystemToken(token);

        List<Map<String, Object>> setores = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/setores")
                .jsonPath().getList("$");

        if (setores != null && !setores.isEmpty()) {
            ctx.setCurrentSetorId((String) setores.get(0).get("id"));
        }
    }
}
