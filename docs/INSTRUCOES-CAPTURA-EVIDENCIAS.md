# Instruções para Captura de Evidências — Testes BDD

Este guia descreve passo a passo o que deve ser capturado para montar o PDF/PPT de evidências da atividade de Testes Automatizados.

---

## Pré-requisitos

Antes de começar, garanta que tem instalado:

- **Docker Desktop ou Colima** — em execução
- **Java 17+** — para rodar os testes (opcional se rodar via Docker)
- **Editor de código** — VSCode, IntelliJ, ou qualquer editor para abrir arquivos
- **Ferramenta de screenshot** — Snipping Tool (Windows), Cmd+Shift+4 (Mac), ou similar
- **Navegador** — Chrome, Firefox ou Edge para abrir o relatório HTML

---

## Estrutura sugerida do PDF/PPT

```
1. Capa
2. Visão geral do projeto e tema ESG
3. Estrutura do projeto de testes
4. Cenários BDD (Gherkin) — 4 features
5. Configurações técnicas (pom.xml, classes de suporte)
6. Validação de contrato (JSON Schema)
7. Evidências de execução (terminal + HTML report)
8. Pipeline CI/CD (opcional)
9. Conclusão
```

---

## PARTE 1 — Preparação do ambiente

### Passo 1.1 — Iniciar o Docker

No terminal:

```bash
# Mac (Colima):
colima start

# Linux/Windows:
# Inicie o Docker Desktop manualmente
```

**Aguarde** até o Docker estar pronto (em poucos segundos).

### Passo 1.2 — Navegar até o projeto

```bash
cd /caminho/para/efic_energ/efApp
```

### Passo 1.3 — Executar os testes uma vez (gera o relatório HTML)

```bash
docker run --rm \
  -v "$(pwd)/api":/app \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -w /app \
  maven:3.9-eclipse-temurin-17 \
  mvn test
```

Aguarde até ver `BUILD SUCCESS` (leva ~2-3 minutos na primeira vez).

> 💡 **Dica:** Se quiser rodar mais rápido nas próximas vezes, instale o Maven localmente e use `cd api && mvn test`.

---

## PARTE 2 — Captura dos cenários Gherkin (4 features)

📁 **Local dos arquivos:** `api/src/test/resources/features/`

Abrir cada um dos 4 arquivos no editor de código e tirar um screenshot mostrando o **conteúdo completo** do arquivo. Capture cada arquivo com o cabeçalho da feature visível.

### 📸 Screenshot 2.1 — `autenticacao.feature`
- Mostra o pilar **G (Governança)** no preâmbulo
- 3 cenários de autenticação/controle de acesso

### 📸 Screenshot 2.2 — `leituras.feature`
- Mostra o pilar **E (Environmental)** no preâmbulo
- 4 cenários de registro de leituras IoT

### 📸 Screenshot 2.3 — `equipamentos.feature`
- Mostra o pilar **G (Governança)** no preâmbulo
- 9 cenários de CRUD de equipamentos (cobre PUT e DELETE)

### 📸 Screenshot 2.4 — `governanca.feature`
- Mostra os pilares **E + G** no preâmbulo
- 3 cenários de compliance e alertas

> ⚠️ **Importante:** Cada feature deve aparecer com a sintaxe Gherkin colorizada pelo editor (`Funcionalidade`, `Cenário`, `Dado`, `Quando`, `Então` destacados).

---

## PARTE 3 — Captura da estrutura do projeto

### 📸 Screenshot 3.1 — Estrutura de pastas

No terminal, executar:

```bash
tree api/src/test -L 4
```

Se `tree` não estiver instalado, usar:

```bash
find api/src/test -type f -not -path '*/\.*' | sort
```

Capturar a saída mostrando a organização:

```
api/src/test/
├── java/br/com/esg/energia/
│   ├── bdd/
│   │   ├── CucumberRunnerTest.java
│   │   ├── CucumberSpringConfiguration.java
│   │   ├── ScenarioContext.java
│   │   ├── SetupHooks.java
│   │   └── steps/
│   │       ├── AuthSteps.java
│   │       ├── CommonSteps.java
│   │       ├── EquipamentoSteps.java
│   │       ├── GovernancaSteps.java
│   │       └── LeituraSteps.java
│   └── service/  (testes unitários existentes)
└── resources/
    ├── features/         (4 arquivos .feature)
    ├── schemas/          (7 arquivos JSON Schema)
    ├── application-test.yml
    └── mockito-extensions/
```

---

## PARTE 4 — Captura das configurações técnicas

### 📸 Screenshot 4.1 — Dependências no `pom.xml`

📁 **Arquivo:** `api/pom.xml`

Abrir o arquivo e capturar a seção `<dependencies>`, focando nas dependências de teste:

- Cucumber (`cucumber-java`, `cucumber-spring`, `cucumber-junit-platform-engine`)
- RestAssured (`rest-assured`, `json-schema-validator`)
- Testcontainers (`spring-boot-testcontainers`, `testcontainers:mongodb`)

> 💡 Capturar do início da seção `<dependencies>` até o fim das deps de teste (~linha 23 a 105).

### 📸 Screenshot 4.2 — `CucumberSpringConfiguration.java`

📁 **Arquivo:** `api/src/test/java/br/com/esg/energia/bdd/CucumberSpringConfiguration.java`

Capturar o arquivo **inteiro** (são ~25 linhas). Destacar:
- `@CucumberContextConfiguration`
- `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`
- `MongoDBContainer` com Testcontainers
- `@DynamicPropertySource` configurando o MongoDB

### 📸 Screenshot 4.3 — `CucumberRunnerTest.java`

📁 **Arquivo:** `api/src/test/java/br/com/esg/energia/bdd/CucumberRunnerTest.java`

Capturar o arquivo inteiro mostrando o Suite JUnit Platform que dispara o Cucumber.

### 📸 Screenshot 4.4 — Step Definitions

📁 **Arquivo:** `api/src/test/java/br/com/esg/energia/bdd/steps/CommonSteps.java`

Capturar o arquivo (são ~95 linhas). É o que tem mais variedade de steps: autenticação, GET, validação de status code e schemas.

---

## PARTE 5 — Captura dos JSON Schemas

📁 **Local:** `api/src/test/resources/schemas/`

### 📸 Screenshot 5.1 — `alerta-list-schema.json`

Capturar o arquivo inteiro. Mostra:
- Estrutura de array
- Campos obrigatórios (`id`, `tipo`, `severidade`, `criadoEm`)
- Enums dos tipos (`CONSUMO_CRITICO`, `OCIOSIDADE`, `META_EXCEDIDA`)
- Enums das severidades (`INFO`, `WARN`, `CRITICAL`)

### 📸 Screenshot 5.2 — `equipamento-schema.json`

Capturar o arquivo. Mostra contrato de objeto único (usado em POST/PUT).

> 💡 Esses 2 schemas são suficientes para mostrar a técnica. Capture mais se quiser destacar a cobertura completa.

---

## PARTE 6 — Captura da execução dos testes (terminal)

### 📸 Screenshot 6.1 — Execução completa com BUILD SUCCESS

No terminal, rodar:

```bash
docker run --rm \
  -v "$(pwd)/api":/app \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -w /app \
  maven:3.9-eclipse-temurin-17 \
  mvn test
```

Aguardar até o final e capturar a tela mostrando:

```
[INFO] Tests run: 43, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  XX:XX min
```

> 💡 **Capturar a saída final** (últimas ~30 linhas do terminal), incluindo a contagem de testes BDD (`Tests run: 19`) e a soma total.

### 📸 Screenshot 6.2 — Detalhamento de uma feature

Rolar o terminal para cima e capturar a saída do Cucumber mostrando os cenários executados, como:

```
Cenário: Registro de leitura gera alerta de consumo crítico
  Dado que existe um equipamento com potência nominal de 10.0 kW
  E que estou autenticado como "admin"
  Quando eu registro uma leitura de 9.5 kWh para o equipamento
  Então o status code da resposta deve ser 200
  E um alerta do tipo "CONSUMO_CRITICO" deve ter sido gerado para o equipamento
```

Os steps devem aparecer com indicação de sucesso.

---

## PARTE 7 — Captura do relatório HTML do Cucumber

### Passo 7.1 — Abrir o relatório

No terminal:

```bash
# Mac:
open api/target/cucumber-reports/report.html

# Linux:
xdg-open api/target/cucumber-reports/report.html

# Windows:
start api/target/cucumber-reports/report.html
```

O relatório abre no navegador.

### 📸 Screenshot 7.1 — Tela inicial (overview)

Capturar a tela inicial mostrando:
- Total de cenários: 19
- Todos passaram (verde 100%)
- Duração total

### 📸 Screenshot 7.2 — Lista de features

Mostrar a lista das 4 features com indicação de cenários passados em cada uma.

### 📸 Screenshot 7.3 — Detalhamento de uma feature

Clicar em uma feature (ex: `equipamentos.feature`) para expandir e capturar:
- Lista de cenários
- Todos verdes
- Steps detalhados de pelo menos 1 cenário

### 📸 Screenshot 7.4 — Cenário detalhado

Clicar em um cenário específico (ex: "Atualização de equipamento existente") e capturar os passos detalhados:

```
✓ Dado que existe um equipamento com potência nominal de 8.0 kW
✓ E que estou autenticado como "admin"
✓ Quando eu atualizo o equipamento atual com nome "Equipamento Atualizado" e potência 12.0 kW
✓ Então o status code da resposta deve ser 200
✓ E a resposta deve conter o nome "Equipamento Atualizado"
✓ E a resposta deve seguir o schema de equipamento individual
```

---

## PARTE 8 — Captura do CI/CD (opcional, mas recomendado)

### Passo 8.1 — Fazer push da branch para o GitHub

```bash
git push origin feature/bdd-tests
```

### Passo 8.2 — Acessar o GitHub Actions

Abrir no navegador:

```
https://github.com/<seu-usuario>/<seu-repositorio>/actions
```

### 📸 Screenshot 8.1 — Pipeline executando ou concluído

Capturar a tela do GitHub Actions mostrando:
- Nome do workflow (`CI/CD Pipeline`)
- Status (verde se passou, em execução, etc.)
- Etapas executadas: Build e Testes

### 📸 Screenshot 8.2 — Detalhes do job de testes

Clicar no job `Build e Testes` e capturar:
- A saída do passo `mvn test`
- O resultado `BUILD SUCCESS`

---

## PARTE 9 — Captura do README

### 📸 Screenshot 9.1 — Seção "Testes Automatizados (BDD)" do README

📁 **Arquivo:** `README.md` (raiz do projeto)

Abrir no navegador no GitHub ou em um visualizador de Markdown e capturar a seção que documenta:
- Tecnologias usadas
- Como executar os testes
- Estrutura dos testes
- Tabela de cenários BDD cobertos (19)
- Tabela de cobertura de endpoints (11/11)

---

## Resumo do que vai no PDF/PPT

| Seção | Conteúdo | Screenshots |
|---|---|---|
| Visão geral | Tema ESG + objetivo dos testes | — |
| Cenários Gherkin | 4 features completas | 2.1 a 2.4 |
| Estrutura | Pastas e arquivos | 3.1 |
| Configurações | pom.xml + classes de suporte | 4.1 a 4.4 |
| JSON Schema | Contratos das respostas | 5.1 e 5.2 |
| Execução local | Terminal + BUILD SUCCESS | 6.1 e 6.2 |
| Relatório HTML | Cucumber Reports | 7.1 a 7.4 |
| CI/CD | GitHub Actions | 8.1 e 8.2 |
| Documentação | README com instruções | 9.1 |

**Total mínimo:** ~16 screenshots
**Total recomendado:** ~20 screenshots (incluindo CI)

---

## Comandos úteis em caso de problemas

### Os testes não rodam
```bash
# Verificar se o Docker está rodando
docker ps

# Se não estiver, no Mac:
colima start
```

### O relatório HTML não está atualizado
```bash
# Reexecutar os testes para regerar o relatório
cd api && mvn test
```

### O CI não dispara automaticamente
```bash
# Confirmar que o push foi feito
git status
git log -1

# Forçar nova execução (commit vazio)
git commit --allow-empty -m "trigger ci"
git push
```
