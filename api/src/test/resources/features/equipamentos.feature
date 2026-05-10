# language: pt

Funcionalidade: Gerenciamento de equipamentos
  Como administrador do sistema
  Quero gerenciar os equipamentos monitorados
  Para manter o cadastro atualizado e rastreável

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

  Cenário: Cadastro de equipamento sem setor retorna erro de validação
    Dado que estou autenticado como "admin"
    Quando eu envio um POST para "/equipamentos" sem informar o setor
    Então o status code da resposta deve ser 400

  Cenário: Busca de equipamento por ID inexistente retorna erro
    Dado que estou autenticado como "admin"
    Quando eu faço uma requisição GET para "/equipamentos/id-inexistente"
    Então o status code da resposta deve ser 400

  Cenário: Listagem de setores retorna dados válidos
    Dado que estou autenticado como "admin"
    Quando eu faço uma requisição GET para "/setores"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista JSON
    E a resposta deve seguir o schema de setor
