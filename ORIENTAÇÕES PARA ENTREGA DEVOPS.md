# **VISÃO GERAL DO QUE VOCÊS VÃO CONSTRUIR**

Vocês vão transformar o projeto atual em algo com:

* Backend Java (Spring Boot)  
* Banco MongoDB (já validado no projeto)  
* Docker (containerização)  
* Docker Compose (app \+ banco)  
* Pipeline CI/CD (ex: GitHub Actions)  
* Deploy em:  
  * **Staging (teste)**  
  * **Produção**

---

# **ESTRATÉGIA GERAL (IMPORTANTE)**

Vocês NÃO devem fazer tudo ao mesmo tempo.

Ordem correta:

1. Ajustar o projeto backend  
2. Criar Dockerfile  
3. Criar docker-compose  
4. Subir localmente (simular produção)  
5. Criar pipeline CI/CD  
6. Fazer deploy staging  
7. Fazer deploy produção  
8. Documentar tudo

---

# **DIVISÃO DE TAREFAS (IDEAL PARA SUA EQUIPE)**

### **Pedro (Backend líder)**

* Ajustar projeto Spring Boot  
* Garantir que roda com MongoDB  
* Criar endpoints (se faltar algo)  
* Ajudar no Dockerfile

---

### **Caio (Dados \+ Backend)**

* Configurar MongoDB  
* Criar `.env` (variáveis de ambiente)  
* Ajustar conexão com banco no Spring  
* Ajudar no docker-compose

---

### **Raphael (Frontend)**

* Se tiver frontend:  
  * Ajustar integração com API  
* Se NÃO tiver:  
  * Criar um dashboard simples (opcional)  
* Pode ajudar no README \+ prints

---

### **Vitor (Documentação)**

* Docker \+ CI/CD (FOCO PRINCIPAL)  
* README.md  
* Organização do projeto  
* Testes de execução

---

### **🧑‍💻 Adryel (Auxiliar)**

* Auxiliar em:  
  * Testes  
  * Documentação (PDF/PPT)  
  * Prints  
  * Checklist  
* Pode ajudar no docker-compose 

# **ETAPA 1 — AJUSTAR O BACKEND**

✔ Garantir que o projeto roda localmente

Exemplo:  
mvn spring-boot:run

✔ Testar endpoints:

* sensores  
* leituras  
* unidades consumidoras

# **ETAPA 2 — DOCKERFILE**

Exemplo padrão para Spring Boot:

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/\*.jar app.jar

EXPOSE 8080

ENTRYPOINT \["java","-jar","app.jar"\]

Antes disso:

mvn clean package

# **ETAPA 3 — DOCKER COMPOSE**

Aqui vocês simulam produção completa.

version: '3.8'

services:

  app:

    build: .

    ports:

      \- "8080:8080"

    environment:

      \- SPRING\_DATA\_MONGODB\_URI=mongodb://mongo:27017/esgdb

    depends\_on:

      \- mongo

  mongo:

    image: mongo:6

    ports:

      \- "27017:27017"

    volumes:

      \- mongo\_data:/data/db

volumes:

  mongo\_data:

Rodar:

docker-compose up \--build

# **ETAPA 4 — TESTAR AMBIENTE LOCAL**

Vocês precisam validar:

* API funcionando  
* Banco funcionando  
* Dados sendo salvos

Se isso não funcionar → pipeline NÃO vai funcionar depois.

# 

#  **ETAPA 5 — CI/CD (GitHub Actions \- recomendado)**

Criar pasta no github.

Exemplo básico:

name: CI/CD Pipeline

on:

 push:

   branches: \[ "main" \]

jobs:

 build:

   runs-on: ubuntu-latest

   steps:

   \- name: Checkout

     uses: actions/checkout@v3

   \- name: Setup Java

     uses: actions/setup-java@v3

     with:

       java-version: '17'

   \- name: Build

     run: mvn clean install

   \- name: Test

     run: mvn test

   \- name: Build Docker image

     run: docker build \-t smart-energy .

# **ETAPA 6 — DEPLOY (STAGING E PRODUÇÃO)**

### **Opções simples (recomendado para projeto acadêmico):**

* Render  
* Railway  
* Fly.io  
* AWS (mais complexo)

### **Estratégia:**

| Ambiente | Branch |
| ----- | ----- |
| Staging | develop |
| Produção | main |

#  **ETAPA 7 — README.md**

Você vai escrever algo assim:

## **Como executar**

docker-compose up \--build

## 

## **Pipeline**

* Build automático  
* Testes com Maven  
* Build Docker  
* Deploy automático

## **Containerização**

Explicar:

* Dockerfile  
* Imagem Java  
* Porta 8080

# **ETAPA 8 — DOCUMENTAÇÃO (PDF/PPT)**

Divisão:

* Adryel → estrutura  
* Raphael → design  
* Vitor → conteúdo técnico  
* Pedro/Caio → validação técnica

---

# **CHECKLIST ( PRECISAM ENTREGAR)**

✔ Dockerfile funcionando  
 ✔ docker-compose funcionando  
 ✔ Pipeline rodando  
 ✔ Deploy feito  
 ✔ README completo  
 ✔ PDF com prints

---

# **⚠️ ERROS COMUNS (EVITEM ISSO)**

* ❌ Docker não conecta no Mongo  
* ❌ Porta errada (8080)  
* ❌ Variáveis de ambiente hardcoded  
* ❌ Pipeline sem teste  
* ❌ Não ter staging separado

---

# **🧩 DICA FINAL (NÍVEL PROFESSOR)**

NÃO precisam fazer algo super complexo.

Se vocês entregarem:

* Docker funcionando  
* Pipeline rodando  
* Deploy simples (mesmo que básico)

→ Já é nível alto para atividade acadêmica.

