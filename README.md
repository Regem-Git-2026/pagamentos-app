# Sistema de Pagamentos de Débitos teste

Aplicação full-stack para cadastro, consulta, processamento de status e exclusão lógica de pagamentos de pessoas físicas e jurídicas.

## Stack

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- H2
- Spring Kafka
- Springdoc OpenAPI / Swagger
- JUnit 5 + Mockito
- Angular 17
- Docker / Docker Compose
- Kafka + Zookeeper

## Funcionalidades

- Cadastro de pagamento
- Status inicial `PENDENTE_PROCESSAMENTO`
- Listagem com filtros por `idPagamento`, `cpfCnpj` e `status`
- Alteração de status com regras de negócio
- Exclusão lógica somente de pagamentos pendentes
- Interface Angular com formulário, filtros, tabela, paginação e toast
- Endpoint REST para processamento de status
- Producer Kafka via endpoint
- Consumer Kafka no próprio backend
- Swagger
- Testes unitários com JUnit e Mockito

## Regras de status

- `PENDENTE_PROCESSAMENTO` -> `PROCESSADO_SUCESSO`
- `PENDENTE_PROCESSAMENTO` -> `PROCESSADO_FALHA`
- `PROCESSADO_FALHA` -> `PENDENTE_PROCESSAMENTO`
- `PROCESSADO_SUCESSO` -> não pode ser alterado

O backend rejeita transições inválidas com HTTP 422. O Angular exibe a mensagem em toast.

## Segurança de dados de cartão

O campo `numeroCartao` é aceito somente quando o método for cartão. Em uma aplicação real, não se deve armazenar o número completo do cartão; deve-se usar tokenização/PCI DSS. Neste projeto didático, a regra funcional foi mantida conforme o enunciado.

## Executar com Docker

Pré-requisitos: Docker Desktop com Compose.

Na raiz:

```bash
docker compose up --build
```

Acesse:

- Front-end: http://localhost:4200
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

H2:
- JDBC URL: `jdbc:h2:mem:pagamentosdb`
- User: `sa`
- Password: vazio

Para encerrar:

```bash
docker compose down
```

## Executar sem Docker

### Backend

```bash
cd backend
mvn spring-boot:run
```

O Kafka pode ser iniciado separadamente. Se Kafka não estiver disponível, a API REST continua funcionando; o envio Kafka falhará e retornará erro apropriado.

### Frontend

```bash
cd frontend
npm install
npm start
```

## Endpoints

### Criar

`POST /pagamentos`

Exemplo:

```json
{
  "idPagamento": 123,
  "cpfCnpj": "12345678901",
  "metodoPagamento": "pix",
  "numeroCartao": null,
  "valor": 150.75
}
```

### Listar

`GET /lista-pagamentos`

Filtros:

```text
/lista-pagamentos?idPagamento=123
/lista-pagamentos?cpfCnpj=12345678901
/lista-pagamentos?status=PENDENTE_PROCESSAMENTO
```

### Atualizar status diretamente

`PUT /pagamentos/status`

```json
{
  "idPagamento": 123,
  "novoStatus": "PROCESSADO_SUCESSO"
}
```

### Publicar status no Kafka

`POST /pagamentos/status/kafka`

```json
{
  "idPagamento": 123,
  "novoStatus": "PROCESSADO_SUCESSO"
}
```

O producer publica no tópico `pagamento-status`; o consumer do próprio backend recebe a mensagem e aplica as regras.

### Exclusão lógica

`DELETE /pagamentos/{id}`

O `{id}` é o ID interno gerado pelo banco. Só é permitido quando o status for `PENDENTE_PROCESSAMENTO`.

## Arquitetura

```text
Angular
   |
   | HTTP/JSON
   v
Spring Boot REST
   |
   +--> Service --> JPA --> H2
   |
   +--> Kafka Producer --> tópico pagamento-status
                              |
                              v
                       Kafka Consumer
                              |
                              v
                         Service --> H2
```

## Testes

```bash
cd backend
mvn test
```

Os testes cobrem principalmente as regras de transição de status e exclusão lógica.

## Observação

O `docker-compose.yml` inclui Kafka e Zookeeper para demonstrar a integração REST + Kafka. Em produção, recomenda-se Kafka em cluster, banco persistente externo, autenticação/autorização, observabilidade e tratamento de credenciais/segredos.
