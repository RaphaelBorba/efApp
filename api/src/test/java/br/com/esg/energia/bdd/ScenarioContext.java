package br.com.esg.energia.bdd;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ScenarioContext {

    private Response lastResponse;
    private String currentToken;
    private String systemToken;
    private String currentEquipamentoId;
    private String currentSetorId;

    public Response getLastResponse() { return lastResponse; }
    public void setLastResponse(Response r) { this.lastResponse = r; }

    public String getCurrentToken() { return currentToken; }
    public void setCurrentToken(String t) { this.currentToken = t; }

    public String getSystemToken() { return systemToken; }
    public void setSystemToken(String t) { this.systemToken = t; }

    public String getCurrentEquipamentoId() { return currentEquipamentoId; }
    public void setCurrentEquipamentoId(String id) { this.currentEquipamentoId = id; }

    public String getCurrentSetorId() { return currentSetorId; }
    public void setCurrentSetorId(String id) { this.currentSetorId = id; }
}
