# language: pt

Funcionalidade: Governança e compliance energético
  Como gestor de setor
  Quero validar o consumo mensal contra a meta estabelecida e consultar alertas
  Para garantir conformidade regulatória e os objetivos ESG da organização (pilares E e G)

  Cenário: Validação de meta mensal de setor existente retorna consumo total
    Dado que estou autenticado como "admin"
    Quando eu valido a meta mensal do setor para o período "2025-01"
    Então o status code da resposta deve ser 200
    E a resposta deve seguir o schema de governança

  Cenário: Validação de meta mensal sem autenticação é bloqueada
    Quando eu valido a meta mensal do setor sem autenticação para o período "2025-01"
    Então o status code da resposta deve ser 403

  Cenário: Consulta de alertas retorna lista válida
    Dado que estou autenticado como "admin"
    Quando eu faço uma requisição GET para "/alertas"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista JSON
    E a resposta deve seguir o schema de alerta
