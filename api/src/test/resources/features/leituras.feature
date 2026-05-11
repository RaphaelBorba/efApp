# language: pt

Funcionalidade: Registro e monitoramento de leituras de consumo energético
  Como gateway IoT integrado ao sistema
  Quero registrar leituras de consumo dos equipamentos em tempo real
  Para monitorar a eficiência energética e identificar consumos críticos (pilar E de ESG)

  Cenário: Registro de leitura com consumo normal
    Dado que existe um equipamento com potência nominal de 10.0 kW
    E que estou autenticado como "admin"
    Quando eu registro uma leitura de 5.0 kWh para o equipamento
    Então o status code da resposta deve ser 200
    E a resposta deve conter o id da leitura registrada
    E a resposta deve seguir o schema de leitura

  Cenário: Registro de leitura gera alerta de consumo crítico
    Dado que existe um equipamento com potência nominal de 10.0 kW
    E que estou autenticado como "admin"
    Quando eu registro uma leitura de 9.5 kWh para o equipamento
    Então o status code da resposta deve ser 200
    E um alerta do tipo "CONSUMO_CRITICO" deve ter sido gerado para o equipamento

  Cenário: Listagem de leituras de um equipamento
    Dado que existe um equipamento com potência nominal de 10.0 kW
    E que estou autenticado como "admin"
    Quando eu busco as leituras do equipamento
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista JSON

  Cenário: Registro de leitura com equipamento inexistente retorna erro
    Dado que estou autenticado como "admin"
    Quando eu registro uma leitura para o equipamento "id-inexistente" com 5.0 kWh
    Então o status code da resposta deve ser 400
