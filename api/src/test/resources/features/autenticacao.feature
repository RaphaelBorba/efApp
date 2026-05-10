# language: pt

Funcionalidade: Autenticação de usuários
  Como um sistema IoT ou usuário do sistema
  Quero obter um token JWT
  Para acessar os recursos protegidos da API

  Cenário: Autenticação com dados válidos gera token JWT
    Quando eu envio uma requisição POST para "/auth/token" com usuário "admin" e role "ROLE_ADMIN"
    Então o status code da resposta deve ser 200
    E a resposta deve conter um token JWT não vazio
    E a resposta deve seguir o schema de autenticação

  Cenário: Acesso com token inválido é bloqueado
    Quando eu faço uma requisição GET para "/setores" com token inválido
    Então o status code da resposta deve ser 403

  Cenário: Acesso a endpoint protegido sem token retorna acesso negado
    Quando eu faço uma requisição GET para "/setores" sem token de autenticação
    Então o status code da resposta deve ser 403
