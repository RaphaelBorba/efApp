# Design: Migração Oracle/JPA → MongoDB

**Data:** 2026-04-19  
**Projeto:** efApp — EF Energia API  
**Autor:** Pedro (backend líder)

---

## Contexto

O projeto usa Spring Boot 3.3.3 com JPA/Hibernate + Oracle em produção e H2 em dev. O documento de entrega DevOps exige MongoDB. Esta spec descreve a migração completa.

## Decisão de design

Relacionamentos representados por **referências por ID (String)**. Cada documento armazena o ID do relacionado (ex: `equipamentoId`). Lookups explícitos no service quando necessário. Sem `@DBRef` (deprecated pattern).

---

## Mudanças por camada

### pom.xml
- Remove: `spring-boot-starter-data-jpa`, `flyway-core`, `ojdbc11`, `h2`
- Adiciona: `spring-boot-starter-data-mongodb`

### Domínio (5 documentos)

| Entidade | Collection | Relacionamentos |
|---|---|---|
| `Setor` | `setores` | — |
| `Equipamento` | `equipamentos` | `String setorId` |
| `LeituraSensor` | `leituras_sensor` | `String equipamentoId` |
| `ConsumoDiario` | `consumo_diario` | `String equipamentoId` |
| `AlertaEnergia` | `alertas_energia` | `String equipamentoId`, `String setorId` |

Anotações: `@Document`, `@Id String id`, `@Field`. Remove `@Entity`, `@Table`, `@Column`, `@ManyToOne`, `@JoinColumn`, `@GeneratedValue`.

### Repositories (5 interfaces)

- `JpaRepository<Entity, Long>` → `MongoRepository<Entity, String>`
- Métodos com entidade como parâmetro → métodos com ID como parâmetro
- `@Query` JPQL → `@Query` MongoDB JSON ou derived queries do Spring Data

### Services

- IDs mudam de `Long` para `String`
- Lookups de relacionados: `repository.findById(id).orElseThrow()`
- `GovernancaService`: `List<Long>` → `List<String>`
- `DataInitializer`: remove dependências JPA, usa repositórios MongoDB

### application.yml

Remove: `datasource`, `jpa`, `flyway`  
Adiciona:
```yaml
spring:
  data:
    mongodb:
      uri: ${MONGO_URI:mongodb://localhost:27017/efenergia}
```

### docker-compose.yml

Adiciona serviço `mongo:6` com volume persistente. API recebe `MONGO_URI` via environment.

---

## README — seções planejadas

1. Pré-requisitos (Java 17, Maven, Docker)
2. Executar com Docker Compose
3. Executar localmente sem Docker
4. Variáveis de ambiente
5. Validar funcionamento: `GET /actuator/health` + `POST /auth/token`
6. Tabela de endpoints com exemplos curl
