# language: pt

Funcionalidade: Gerenciamento de equipamentos energéticos
  Como administrador do sistema
  Quero cadastrar, consultar, atualizar e remover equipamentos monitorados
  Para garantir a rastreabilidade do parque tecnológico, suportar auditoria e a governança ESG (pilar G)

  Cenário: Listagem de equipamentos retorna dados válidos
    Dado que estou autenticado como "admin"
    Quando eu faço uma requisição GET para "/equipamentos"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista JSON
    E a resposta deve seguir o schema de equipamento

  Cenário: Cadastro de novo equipamento com dados válidos
    Dado que estou autenticado como "admin"
    Quando eu cadastro um equipamento com nome "Servidor ESG" e potência 15.0 kW
    Então o status code da resposta deve ser 201
    E a resposta deve conter o id do equipamento criado
    E a resposta deve seguir o schema de equipamento individual

  Cenário: Cadastro de equipamento sem setor retorna erro de validação
    Dado que estou autenticado como "admin"
    Quando eu envio um POST para "/equipamentos" sem informar o setor
    Então o status code da resposta deve ser 400

  Cenário: Busca de equipamento por ID inexistente retorna erro
    Dado que estou autenticado como "admin"
    Quando eu faço uma requisição GET para "/equipamentos/id-inexistente"
    Então o status code da resposta deve ser 400

  Cenário: Atualização de equipamento existente
    Dado que existe um equipamento com potência nominal de 8.0 kW
    E que estou autenticado como "admin"
    Quando eu atualizo o equipamento atual com nome "Equipamento Atualizado" e potência 12.0 kW
    Então o status code da resposta deve ser 200
    E a resposta deve conter o nome "Equipamento Atualizado"
    E a resposta deve seguir o schema de equipamento individual

  Cenário: Atualização de equipamento inexistente retorna erro
    Dado que estou autenticado como "admin"
    Quando eu atualizo o equipamento "id-inexistente" com nome "Teste" e potência 5.0 kW
    Então o status code da resposta deve ser 400

  Cenário: Remoção de equipamento existente
    Dado que existe um equipamento com potência nominal de 7.0 kW
    E que estou autenticado como "admin"
    Quando eu removo o equipamento atual
    Então o status code da resposta deve ser 204

  Cenário: Remoção de equipamento inexistente retorna erro
    Dado que estou autenticado como "admin"
    Quando eu removo o equipamento "id-inexistente"
    Então o status code da resposta deve ser 400

  Cenário: Listagem de setores retorna dados válidos
    Dado que estou autenticado como "admin"
    Quando eu faço uma requisição GET para "/setores"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista JSON
    E a resposta deve seguir o schema de setor
