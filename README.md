# EF Energia API

Serviço RESTful (Spring Boot 3.3, JDK 17) para Eficiência Energética com H2 Database + JWT.

## Banco de Dados
**Nota importante**: Este projeto usa H2 Database em memória ao invés de Oracle devido a inconsistências conhecidas com containers Oracle em MacBooks (problemas de PMON e memória compartilhada). O H2 está configurado em modo Oracle para máxima compatibilidade.

## Requisitos
- Docker e Docker Compose

## Subir com Docker
```bash
cd efApp
docker-compose up --build
```
- API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Password: vazio)

## Obter token
```bash
curl -X POST http://localhost:8080/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"username":"test","roles":"ADMIN"}'
```
Copie o valor `token` retornado.

## Mapa de Rotas da API

### 🔓 Endpoints Públicos
| Método | Rota | Descrição | Retorna |
|--------|------|-----------|---------|
| GET | `/actuator/health` | Status da aplicação | `{"status":"UP"}` |
| POST | `/auth/token` | Gerar JWT | `{"token":"eyJ..."}` |

**Parâmetros `/auth/token`:**
```json
{
  "username": "string",
  "roles": "ADMIN,GESTOR_SETOR,ANALISTA,IOT_GATEWAY"
}
```

### 🔒 Endpoints Protegidos (Bearer Token)

#### 📊 Leituras
| Método | Rota | Roles | Parâmetros | Retorna |
|--------|------|-------|------------|---------|
| POST | `/leituras` | IOT_GATEWAY, ADMIN | Body JSON | Leitura criada |
| GET | `/leituras` | ANALISTA, GESTOR_SETOR, ADMIN | Query params | Array de leituras |

**POST /leituras - Body:**
```json
{
  "equipamentoId": 1,
  "consumoKwh": 45.5,
  "timestampLeitura": "2025-01-01T10:00:00"
}
```

**GET /leituras - Query params (obrigatórios):**
- `equipamentoId`: Long
- `inicio`: DateTime (ISO format)
- `fim`: DateTime (ISO format)

**Retorno leituras:**
```json
[{
  "id": 1,
  "equipamentoId": 1,
  "consumoKwh": 45.5,
  "timestampLeitura": "2025-01-01T10:00:00"
}]
```

#### 🚨 Alertas
| Método | Rota | Roles | Parâmetros | Retorna |
|--------|------|-------|------------|---------|
| GET | `/alertas` | ANALISTA, GESTOR_SETOR, ADMIN | Query params (opcionais) | Array de alertas |

**GET /alertas - Query params (todos opcionais):**
- `tipo`: String (CONSUMO_CRITICO, OCIOSIDADE, META_EXCEDIDA)
- `setorId`: Long
- `equipamentoId`: Long
- `inicio`: DateTime
- `fim`: DateTime

**Retorno alertas:**
```json
[{
  "id": 1,
  "tipo": "CONSUMO_CRITICO",
  "severidade": "CRITICAL",
  "mensagem": "Chiller 01 operando acima de 90% da capacidade",
  "setorId": 1,
  "equipamentoId": 1,
  "criadoEm": "2025-11-04T10:00:00"
}]
```

#### 🏢 Setores
| Método | Rota | Roles | Parâmetros | Retorna |
|--------|------|-------|------------|---------|
| GET | `/setores` | ANALISTA, GESTOR_SETOR, ADMIN | Nenhum | Array de setores |

**Retorno setores:**
```json
[{
  "id": 1,
  "nome": "Operações",
  "gestor": "Maria Silva",
  "metaConsumoMensal": 5000
}]
```

#### ⚡ Equipamentos
| Método | Rota | Roles | Parâmetros | Retorna |
|--------|------|-------|------------|---------|
| GET | `/equipamentos` | ANALISTA, GESTOR_SETOR, ADMIN | Query params (opcionais) | Array de equipamentos |

**GET /equipamentos - Query params (opcionais):**
- `setorId`: Long (filtrar por setor)

**Retorno equipamentos:**
```json
[{
  "id": 1,
  "setorId": 1,
  "nome": "Chiller 01",
  "tipo": "Refrigeração",
  "potenciaNominal": 50.0
}]
```

#### 📋 Governança
| Método | Rota | Roles | Parâmetros | Retorna |
|--------|------|-------|------------|---------|
| POST | `/governanca/validar-meta-mensal` | GESTOR_SETOR, ADMIN | Query params | Resultado da validação |

**POST /governanca/validar-meta-mensal - Query params (obrigatórios):**
- `setorId`: Long
- `anoMes`: String (formato YYYY-MM)

**Retorno validação:**
```json
{
  "anoMes": "2025-01",
  "setorId": 1,
  "consumoTotal": 1250.75
}
```

### 🔍 H2 Console (Desenvolvimento)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/h2-console` | Interface web do banco H2 |

**Credenciais H2:**
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (vazio)

## Testar endpoints (exemplos)
```bash
# 1. Obter token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"username":"test","roles":"ADMIN"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 2. Listar setores
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/setores

# 3. Listar equipamentos
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/equipamentos

# 4. Registrar leitura
curl -X POST http://localhost:8080/leituras \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"equipamentoId":1,"consumoKwh":4.5,"timestampLeitura":"2025-01-01T10:00:00"}'

# 5. Consultar leituras
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/leituras?equipamentoId=1&inicio=2025-01-01T00:00:00&fim=2025-01-02T00:00:00"

# 6. Verificar alertas
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/alertas

# 7. Validar meta mensal
curl -X POST -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/governanca/validar-meta-mensal?setorId=1&anoMes=2025-01"
```

## Postman
- Importar `postman/collection.json` e `postman/environment.json`
- Atualizar variável `token` com o JWT obtido
- Collection inclui todos os endpoints MVP

## Regras de negócio implementadas

### 1. Consumo Crítico (Automático)
- **Trigger**: Ao registrar leitura via `POST /leituras`
- **Condição**: `consumoKwh >= 90% da potenciaNominal` do equipamento
- **Ação**: Cria automaticamente `ALERTA_ENERGIA` tipo `CONSUMO_CRITICO` com severidade `CRITICAL`
- **Localização**: `LeituraService.verificarConsumoCriticoEGerarAlerta()`
- **Exemplo**: Chiller 50kW → alerta se leitura ≥ 45kWh

### 2. Acúmulo Diário (Automático)
- **Trigger**: Ao registrar leitura via `POST /leituras`
- **Ação**: Atualiza/cria registro em `CONSUMO_DIARIO` para o dia da leitura
- **Lógica**: Soma `deltaKwh` ao `totalKwh` existente ou cria novo registro se não existir
- **Localização**: `LeituraService.atualizarConsumoDiario()`
- **Constraint**: Único por `(equipamentoId, dia)`

### 3. Validação de Meta Mensal (Manual)
- **Trigger**: Via `POST /governanca/validar-meta-mensal?setorId=X&anoMes=YYYY-MM`
- **Cálculo**: Soma consumo de todos equipamentos do setor no período mensal
- **Condição**: Se `consumoTotal > metaConsumoMensal` do setor
- **Ação**: Cria `ALERTA_ENERGIA` tipo `META_EXCEDIDA` com severidade `WARN`
- **Localização**: `GovernancaService.validarMetaMensal()`
- **Retorno**: JSON com consumo total calculado

### 4. Estrutura de Alertas
- **Tipos**: `CONSUMO_CRITICO`, `OCIOSIDADE`, `META_EXCEDIDA`
- **Severidades**: `INFO`, `WARN`, `CRITICAL`
- **Associações**: Pode referenciar equipamento específico e/ou setor
- **Timestamp**: `criadoEm` automático com `LocalDateTime.now()`
- **Consulta**: `GET /alertas` com filtros opcionais (tipo, setorId, equipamentoId, período)

### 5. Fluxo de Monitoramento
```
1. IoT Gateway → POST /leituras
2. Sistema verifica consumo crítico automaticamente
3. Sistema atualiza consumo diário automaticamente  
4. Gestor → POST /governanca/validar-meta-mensal (mensal)
5. Analista → GET /alertas (consulta alertas gerados)
```

### 6. Perfis de Acesso
- **IOT_GATEWAY**: Apenas `POST /leituras` (sensores IoT)
- **ANALISTA**: Consultas (leituras, alertas, setores, equipamentos)
- **GESTOR_SETOR**: Analista + validação de meta mensal
- **ADMIN**: Acesso completo a todos endpoints

## Configurações
- Profile `dev` ativo por padrão (H2 + JPA create-drop)
- `GET /actuator/health` é público
- Todos os outros endpoints protegidos por JWT Bearer

## Dados iniciais
- 2 setores: "Operações" (meta 5000 kWh) e "TI" (meta 3000 kWh)
- 2 equipamentos: "Chiller 01" (50kW) e "Servidor 01" (5kW)

## Arquitetura
- Camadas: Controller → Service → Repository
- Diretório da API: `efApp/api`

