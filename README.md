 Estapar Webhook Service

Este projeto é um serviço Spring Boot responsável por receber eventos via Webhook, processá-los e persistir dados em um banco de dados MySQL. A aplicação também está configurada para exibir no terminal as queries SQL **com os valores reais** enviados ao banco, o que facilita a depuração e rastreamento.

---

## 📦 Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA (Hibernate)
- MySQL
- Lombok
- Postman (para testes)

---

## 🚀 Executando o Projeto

### Pré-requisitos

- Java 17+
- MySQL instalado e rodando localmente
- IDE como IntelliJ ou VSCode
- Postman (opcional, para testes)

### Configuração do Banco de Dados

Certifique-se de que o MySQL esteja rodando e crie o banco de dados:

```sql
CREATE DATABASE estapar;
