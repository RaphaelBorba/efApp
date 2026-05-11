# Projeto - EF Energia: Cidades ESG Inteligentes

API REST de Eficiência Energética ESG — Spring Boot 3.3.3 + MongoDB.

Monitora consumo energético por setor, gerencia equipamentos, processa leituras de sensores IoT, gera alertas automáticos e valida metas mensais de consumo.

---

## Como executar localmente com Docker

### Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Docker | 24+ |
| Docker Compose | v2 |

### Ambiente de desenvolvimento

```bash
# Sobe a API + MongoDB juntos
docker compose up --build
```

Aguarde as mensagens:
```
ef-mongo-dev | {...} "msg":"Waiting for connections"    # MongoDB pronto
ef-api-dev   | Started EfAppApplication in X.XXX seconds # API pronta
```

A API estará disponível em: `http://localhost:8080`

```bash
# Verificar saúde da aplicação
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

### Ambiente de staging

```bash
docker compose -f docker-compose.staging.yml up --build
```

A API de staging estará em: `http://localhost:8081`

### Ambiente de produção

```bash
# Defina o segredo JWT antes de subir
export APP_JWT_SECRET=seu_segredo_seguro_aqui

docker compose -f docker-compose.prod.yml up --build
```

A API de produção estará em: `http://localhost:8080`

### Parar os ambientes

```bash
docker compose down          # para dev
docker compose down -v       # para dev + apaga volumes
docker compose -f docker-compose.staging.yml down
docker compose -f docker-compose.prod.yml down
```

### Configurar variáveis de ambiente

Copie o arquivo de exemplo e ajuste os valores:

```bash
cp .env.example .env
```

| Variável | Padrão | Descrição |
|---|---|---|
| `MONGO_URI` | `mongodb://mongo:27017/efenergia` | URI de conexão MongoDB |
| `APP_JWT_SECRET` | *(valor de dev)* | Segredo JWT — **troque em produção** |
| `APP_JWT_EXPIRATION_SECONDS` | `3600` | Validade do token em segundos |
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil Spring ativo |

---

## Testes Automatizados (BDD)

### Tecnologias

| Ferramenta | Finalidade |
|---|---|
| **Cucumber 7** | BDD — executa cenários `.feature` em Gherkin |
| **RestAssured** | Testes de API HTTP (status code, corpo JSON) |
| **JSON Schema Validator** | Validação de contrato das respostas |
| **Testcontainers** | MongoDB isolado por test run (sem dependência externa) |
| **JUnit 5** | Runner principal |

### Pré-requisitos para rodar os testes

| Ferramenta | Versão mínima |
|---|---|
| Java | 17+ |
| Docker | 24+ (necessário para o Testcontainers subir o MongoDB) |
| Maven | 3.9+ (ou use o wrapper `./mvnw`) |

> **Nota:** O Docker precisa estar em execução. No Mac, inicie o Colima: `colima start`

### Executar os testes localmente

**Opção 1 — Com Maven instalado:**

```bash
cd api
mvn test
```

**Opção 2 — Via Docker (sem Java/Maven local):**

```bash
cd api

docker run --rm \
  -v "$(pwd)":/app \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -w /app \
  maven:3.9-eclipse-temurin-17 \
  mvn test
```

> 💡 O volume `/var/run/docker.sock` é necessário para o Testcontainers conseguir subir o MongoDB.

Após a execução, você verá algo como:
```
Tests run: 43, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Onde **43 testes = 24 unitários (Mockito) + 19 cenários BDD (Cucumber)**.

### Estrutura dos testes

```
api/src/test/
├── java/br/com/esg/energia/
│   ├── bdd/
│   │   ├── CucumberRunnerTest.java       ← Runner JUnit Platform Suite
│   │   ├── CucumberSpringConfiguration.java ← @SpringBootTest + Testcontainers
│   │   ├── ScenarioContext.java          ← Estado compartilhado entre steps
│   │   ├── SetupHooks.java               ← @Before: obtém token e setorId
│   │   └── steps/
│   │       ├── AuthSteps.java            ← Steps de autenticação
│   │       ├── CommonSteps.java          ← Steps genéricos (GET, status code)
│   │       ├── EquipamentoSteps.java     ← Steps de equipamentos/setores
│   │       ├── GovernancaSteps.java      ← Steps de governança
│   │       └── LeituraSteps.java         ← Steps de leituras
│   └── service/                          ← Testes unitários (Mockito)
└── resources/
    ├── features/
    │   ├── autenticacao.feature          ← 3 cenários de auth
    │   ├── equipamentos.feature          ← 9 cenários de equipamentos/setores
    │   ├── governanca.feature            ← 3 cenários de governança
    │   └── leituras.feature              ← 4 cenários de leituras
    ├── schemas/
    │   ├── auth-schema.json              ← Contrato de autenticação
    │   ├── alerta-list-schema.json       ← Contrato de alertas (lista)
    │   ├── equipamento-list-schema.json  ← Contrato de equipamentos (lista)
    │   ├── equipamento-schema.json       ← Contrato de equipamento individual
    │   ├── governanca-schema.json        ← Contrato de governança
    │   ├── leitura-schema.json           ← Contrato de leitura
    │   └── setor-list-schema.json        ← Contrato de setores
    ├── application-test.yml              ← Profile de teste (MongoDB via TC)
    └── mockito-extensions/
        └── org.mockito.plugins.MockMaker ← SubclassMockMaker (Mockito 5.x fix)
```

### Cenários BDD cobertos (19 no total)

| Feature | Cenários |
|---|---|
| **Autenticação** (pilar G) | Token JWT gerado com sucesso · Acesso bloqueado com token inválido · Acesso bloqueado sem token |
| **Leituras** (pilar E) | Registro de leitura normal · Leitura crítica gera alerta · Listagem de leituras · Equipamento inexistente |
| **Equipamentos** (pilar G) | Listagem · Cadastro válido · Sem setor (400) · ID inexistente · Atualização (PUT) · Atualização inexistente · Remoção (DELETE) · Remoção inexistente · Listagem de setores |
| **Governança** (pilares E+G) | Validação de meta mensal · Acesso bloqueado sem auth · Consulta de alertas |

### Cobertura de endpoints (11/11)

| Método | Endpoint | Cenários |
|---|---|---|
| POST | `/auth/token` | Autenticação positiva |
| GET | `/setores` | Listagem de setores · Sem token · Token inválido |
| GET | `/equipamentos` | Listagem |
| GET | `/equipamentos/{id}` | ID inexistente |
| POST | `/equipamentos` | Cadastro válido · Sem setor |
| PUT | `/equipamentos/{id}` | Atualização válida · ID inexistente |
| DELETE | `/equipamentos/{id}` | Remoção válida · ID inexistente |
| POST | `/leituras` | Leitura normal · Leitura crítica · Equipamento inexistente |
| GET | `/leituras` | Listagem por equipamento |
| GET | `/alertas` | Consulta de alertas · Verificação de alerta gerado |
| POST | `/governanca/validar-meta-mensal` | Validação de meta · Sem auth |

### Relatório HTML

Após executar os testes, o relatório interativo fica em:

```
api/target/cucumber-reports/report.html
```

### Validação de contrato (JSON Schema)

Cada API tem um JSON Schema em `src/test/resources/schemas/` que valida a estrutura da resposta. Exemplo para alertas:

```json
{
  "type": "array",
  "items": {
    "required": ["id", "tipo", "severidade", "criadoEm"],
    "properties": {
      "tipo": { "enum": ["CONSUMO_CRITICO", "OCIOSIDADE", "META_EXCEDIDA"] },
      "severidade": { "enum": ["INFO", "WARN", "CRITICAL"] }
    }
  }
}
```

---

## Pipeline CI/CD

### Ferramenta utilizada

**GitHub Actions** — configurado em `.github/workflows/ci.yml`.

O pipeline é acionado automaticamente a cada push nas branches `main`, `master`, `develop`, `feature/devops-setup` e `feature/bdd-tests`, e em pull requests para `main` ou `master`.

### Etapas do pipeline

```
Push/PR
  │
  ├── 1. Build & Test
  │     ├── Checkout do código
  │     ├── Setup Java 17 (Temurin)
  │     ├── Cache do repositório Maven
  │     ├── mvn clean package  →  compila o projeto
  │     └── mvn test           →  executa 43 testes (24 unitários + 19 BDD)
  │
  ├── 2. Deploy Staging  (apenas na branch develop)
  │     ├── Build da imagem Docker
  │     └── docker compose -f docker-compose.staging.yml up
  │
  └── 3. Deploy Production  (apenas na branch main)
        ├── Build da imagem Docker
        └── docker compose -f docker-compose.prod.yml up
```

### Testes automatizados executados

O pipeline executa **43 testes** ao todo: 24 unitários + 19 cenários BDD.

**Testes unitários** (Mockito):

| Classe de teste | Testes | O que valida |
|---|---|---|
| `EquipamentoServiceTest` | 10 | CRUD, validações, busca por setor |
| `LeituraServiceTest` | 5 | Registro de leitura, geração de alerta crítico, acúmulo diário |
| `AlertaServiceTest` | 5 | Filtros por tipo, setor, equipamento e período |
| `GovernancaServiceTest` | 4 | Validação de meta mensal, geração de alerta META_EXCEDIDA |

**Testes BDD** (Cucumber + RestAssured + Testcontainers):

| Feature | Cenários | Foco |
|---|---|---|
| `autenticacao.feature` | 3 | Geração de token JWT, bloqueio sem/com token inválido |
| `leituras.feature` | 4 | Registro de leitura IoT, alerta crítico, listagem |
| `equipamentos.feature` | 9 | CRUD completo de equipamentos + listagem de setores |
| `governanca.feature` | 3 | Validação de meta mensal, alertas, controle de acesso |

Rodar testes localmente:
```bash
cd api
mvn test
```

---

## Containerização

### Dockerfile

O `Dockerfile` em `api/` segue uma estratégia de cache em duas etapas:

```dockerfile
FROM eclipse-temurin:17
WORKDIR /app

# Instala Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copia e baixa dependências primeiro (camada cacheável)
COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests

# Copia o código-fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests
RUN mv target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Estratégias adotadas:**
- `dependency:go-offline` antes de copiar o `src/` — o Docker reutiliza essa camada enquanto o código muda mas o `pom.xml` não, acelerando rebuilds
- `rm -rf /var/lib/apt/lists/*` — reduz o tamanho da imagem removendo cache do apt
- `-DskipTests` no build da imagem — testes são responsabilidade do pipeline CI, não do Dockerfile

### Ambientes Docker Compose

| Arquivo | Ambiente | Porta API | Banco | Política de restart |
|---|---|---|---|---|
| `docker-compose.yml` | Desenvolvimento | 8080 | efenergia_dev | unless-stopped |
| `docker-compose.staging.yml` | Homologação | 8081 | efenergia_staging | unless-stopped |
| `docker-compose.prod.yml` | Produção | 8080 | efenergia_prod | always |

Cada ambiente tem sua própria **rede bridge isolada** e **volume nomeado** para os dados do MongoDB, garantindo que dados de dev, staging e prod nunca se misturem.

O ambiente de produção também define limites de recursos:
```yaml
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 512M
```

### Profiles Spring

| Profile | Configuração |
|---|---|
| `dev` | URI padrão `localhost`, sem restrições de log |
| `staging` | Log `DEBUG` para `br.com.esg` e queries MongoDB |
| `prod` | Log `WARN` global, `INFO` para a aplicação, apenas endpoint `/health` exposto |

---

## Prints do funcionamento

> Os prints de execução do pipeline, deploy em staging e produção serão adicionados após a primeira execução no GitHub Actions.

Para gerar as evidências localmente:

```bash
# 1. Subir staging
docker compose -f docker-compose.staging.yml up --build -d

# 2. Verificar saúde
curl http://localhost:8081/actuator/health

# 3. Obter token
curl -X POST http://localhost:8081/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","roles":"ADMIN"}'

# 4. Listar setores
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8081/setores
```

---

## Tecnologias utilizadas

### Backend
| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.3.3 | Framework web e IoC |
| Spring Data MongoDB | 3.3.3 | Persistência |
| Spring Security | 3.3.3 | Autenticação e autorização |
| Spring Validation | 3.3.3 | Validação de entrada |
| Spring Actuator | 3.3.3 | Healthcheck e métricas |
| JJWT | 0.11.5 | Geração e validação de tokens JWT |
| SpringDoc OpenAPI | 2.6.0 | Documentação Swagger |

### Banco de dados
| Tecnologia | Versão | Uso |
|---|---|---|
| MongoDB | 6 | Banco de dados principal |

### DevOps
| Tecnologia | Uso |
|---|---|
| Docker | Containerização da aplicação |
| Docker Compose | Orquestração dos serviços (dev, staging, prod) |
| GitHub Actions | Pipeline de CI/CD |
| Maven | Build, dependências e execução de testes |

### Testes
| Tecnologia | Uso |
|---|---|
| JUnit 5 | Framework de testes |
| Mockito | Mocks para testes unitários |
| AssertJ | Asserções fluentes |

---

## Estrutura do projeto

```
efApp/
├── api/
│   ├── src/
│   │   ├── main/java/br/com/esg/energia/
│   │   │   ├── controller/      # 6 controllers REST
│   │   │   ├── service/         # 4 services de negócio
│   │   │   ├── repository/      # 5 repositórios MongoDB
│   │   │   ├── domain/          # 5 entidades
│   │   │   ├── dto/             # DTOs de entrada e saída
│   │   │   ├── security/        # JWT filter e utilitário
│   │   │   └── config/          # SecurityConfig, DataInitializer
│   │   ├── main/resources/
│   │   │   ├── application.yml
│   │   │   ├── application-staging.yml
│   │   │   └── application-prod.yml
│   │   └── test/                # 24 unitários + 19 cenários BDD
│   └── Dockerfile
├── docker-compose.yml           # Desenvolvimento
├── docker-compose.staging.yml   # Homologação
├── docker-compose.prod.yml      # Produção
├── .env.example
└── .github/workflows/           # Pipeline CI/CD
```

---

## Endpoints disponíveis

### Autenticação

```bash
# Obter token JWT
curl -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","roles":"ADMIN"}'
```

### Setores, Equipamentos, Leituras, Alertas, Governança

Documentação interativa disponível em: `http://localhost:8080/swagger-ui.html`

---

## Roles disponíveis

| Role | Permissões |
|---|---|
| `ADMIN` | Acesso total |
| `GESTOR_SETOR` | Leitura + criar/editar equipamentos + validar metas |
| `ANALISTA` | Somente leitura |
| `IOT_GATEWAY` | Registrar leituras de sensor |

---

## Checklist de entrega

| Item | Status |
|---|---|
| Projeto compactado em .ZIP com estrutura organizada | ☐ |
| Dockerfile funcional | ✅ |
| docker-compose.yml para dev/staging/prod | ✅ |
| Pipeline CI com build e testes (GitHub Actions) | ✅ |
| README.md com instruções de execução | ✅ |
| Testes BDD em Gherkin (mínimo 3 cenários) | ✅ (19 cenários) |
| Testes de API para todos os endpoints | ✅ (11/11) |
| Validação de contrato com JSON Schema | ✅ (7 schemas) |
| Documentação técnica com evidências (PDF ou PPT) | ☐ |
| Deploy realizado nos ambientes staging e produção | ☐ |
