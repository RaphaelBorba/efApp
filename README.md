# EF Energia API

API REST de Eficiência Energética ESG — Spring Boot 3.3.3 + MongoDB.

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java | 17 |
| Maven | 3.8+ |
| Docker | 24+ |
| Docker Compose | v2 |

Verifique sua instalação:
```bash
java -version
mvn -version
docker -version
docker compose version
```

---

## Opção 1 — Executar com Docker Compose (recomendado)

Sobe a API e o MongoDB juntos, sem precisar instalar nada além do Docker.

```bash
# Na raiz do projeto
docker compose up --build
```

Aguarde as mensagens:
```
ef-mongo  | {"t":...,"s":"I","c":"NETWORK","id":23015,...} # MongoDB pronto
ef-api    | Started EfAppApplication in X.XXX seconds      # API pronta
```

Para parar:
```bash
docker compose down
```

Para parar e apagar os dados do banco:
```bash
docker compose down -v
```

---

## Opção 2 — Executar localmente sem Docker

### 1. Instalar e iniciar MongoDB localmente

**macOS (Homebrew):**
```bash
brew tap mongodb/brew
brew install mongodb-community@6.0
brew services start mongodb-community@6.0
```

**Ubuntu/Debian:**
```bash
sudo apt-get install -y mongodb
sudo systemctl start mongodb
```

**Windows:** Baixe o instalador em https://www.mongodb.com/try/download/community

Confirme que o MongoDB está rodando:
```bash
mongosh --eval "db.runCommand({ ping: 1 })"
# Esperado: { ok: 1 }
```

### 2. Compilar e executar a API

```bash
cd api
mvn clean package -DskipTests
java -jar target/ef-energia-api-0.0.1-SNAPSHOT.jar
```

Ou usando o plugin Maven:
```bash
cd api
mvn spring-boot:run
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `MONGO_URI` | `mongodb://localhost:27017/efenergia` | URI de conexão MongoDB |
| `APP_JWT_SECRET` | *(base64 embutido)* | Segredo para assinar tokens JWT — **nunca use o valor padrão em produção** |
| `APP_JWT_EXPIRATION_SECONDS` | `3600` | Validade do token em segundos |

Para sobrescrever localmente:
```bash
MONGO_URI=mongodb://usuario:senha@host:27017/efenergia mvn spring-boot:run
```

---

## Validar funcionamento

### 1. Healthcheck

```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada:
```json
{"status":"UP"}
```

### 2. Obter token JWT

```bash
curl -s -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","roles":"ADMIN"}'
```

Resposta esperada:
```json
{"token":"eyJhbGciOiJIUzI1NiJ9..."}
```

Exporte o token para usar nos próximos exemplos:
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","roles":"ADMIN"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)
```

---

## Endpoints

### Setores

```bash
# Listar setores (criados automaticamente na inicialização)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/setores
```

### Equipamentos

```bash
# Listar todos os equipamentos
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/equipamentos

# Listar por setor
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/equipamentos?setorId=<ID>"

# Buscar por ID
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/equipamentos/<ID>

# Criar equipamento (requer role GESTOR_SETOR ou ADMIN)
curl -X POST http://localhost:8080/equipamentos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"setorId":"<ID>","nome":"Ar Condicionado 01","tipo":"Climatização","potenciaNominal":3.5}'

# Atualizar equipamento
curl -X PUT http://localhost:8080/equipamentos/<ID> \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ar Condicionado 01 Atualizado","tipo":"Climatização","potenciaNominal":4.0}'

# Deletar equipamento (requer role ADMIN)
curl -X DELETE -H "Authorization: Bearer $TOKEN" http://localhost:8080/equipamentos/<ID>
```

### Leituras de sensor

```bash
# Registrar leitura (requer role IOT_GATEWAY ou ADMIN)
curl -X POST http://localhost:8080/leituras \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"equipamentoId":"<ID>","consumoKwh":4.6,"timestampLeitura":"2026-04-19T10:00:00"}'

# Listar leituras por período
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/leituras?equipamentoId=<ID>&inicio=2026-04-01T00:00:00&fim=2026-04-30T23:59:59"
```

### Alertas

```bash
# Listar todos os alertas
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/alertas

# Filtrar por tipo
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/alertas?tipo=CONSUMO_CRITICO"

# Filtrar por setor e período
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/alertas?setorId=<ID>&inicio=2026-04-01T00:00:00&fim=2026-04-30T23:59:59"
```

### Governança

```bash
# Validar meta mensal de um setor (requer role GESTOR_SETOR ou ADMIN)
curl -X POST -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/governanca/validar-meta-mensal?setorId=<ID>&anoMes=2026-04"
```

---

## Roles disponíveis

| Role | Permissões |
|---|---|
| `ADMIN` | Acesso total |
| `GESTOR_SETOR` | Leitura + criar/editar equipamentos + validar metas |
| `ANALISTA` | Somente leitura |
| `IOT_GATEWAY` | Registrar leituras de sensor |

---

## Dados iniciais

Na primeira inicialização, a API cria automaticamente:
- Setor **Operações** (meta: 5.000 kWh/mês) com equipamento **Chiller 01**
- Setor **TI** (meta: 3.000 kWh/mês) com equipamento **Servidor 01**
- 3 alertas de exemplo (CONSUMO_CRITICO, OCIOSIDADE, META_EXCEDIDA)

---

## Containerização

O `Dockerfile` em `api/` usa `eclipse-temurin:17` como imagem base, instala o Maven, compila o projeto e executa o JAR na porta `8080`.

O `docker-compose.yml` na raiz orquestra dois serviços:
- `mongo` — MongoDB 6 com volume persistente em `mongo_data`
- `api` — a aplicação Spring Boot, conectada ao Mongo via `MONGO_URI`

---

## Pipeline CI/CD

O pipeline (`.github/workflows/`) executa automaticamente a cada push na branch `main`:
1. Checkout do código
2. Setup Java 17
3. `mvn clean install` — compila e testa
4. Build da imagem Docker
