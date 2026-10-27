# Projeto de aluguel de carros

Aplicação de estudos com Java 25, Spring Boot, JPA, PostgreSQL e uma interface simples em HTML, CSS e JavaScript. O código está separado em `controller` (HTTP), `service` (regras), `repository` (acesso ao banco), `dto` (dados da API) e `entity` (tabelas).

## O que funciona

- Cadastro, consulta, edição e exclusão de lojas, carros e clientes.
- Vínculo de cada carro a uma loja existente.
- Aluguel de carro disponível para um cliente identificado pelo CPF.
- Total do aluguel = diária do carro × número de dias.
- Devolução que libera novamente o carro.
- Proteção contra exclusão de registros com vínculos ou histórico de aluguéis.

## Como executar

1. Instale Java 25 e tenha um PostgreSQL disponível. O arquivo `aluguel_carroapi/comandos-docker.txt` traz um exemplo de container PostgreSQL 16; também é possível usar uma instalação local.
2. Crie o banco `ac`. Se ele estiver vazio, execute `aluguel_carroapi/comandos-sql.txt` no pgAdmin. Se você já tinha criado as tabelas para a versão anterior deste projeto, execute **somente** `aluguel_carroapi/migracao-banco-existente.sql`. A migração mantém os registros existentes; não exclui a coluna antiga de funcionário.
3. Por padrão, a aplicação usa `jdbc:postgresql://localhost:5432/ac`, usuário `postgres` e senha `postgres`. Para alterar, configure as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`.
4. No PowerShell, dentro de `aluguel_carroapi`, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Acesse [http://localhost:8080/](http://localhost:8080/) para usar a tela. A API e a interface rodam no mesmo servidor, portanto não é necessário configurar CORS.

## Principais endpoints

| Recurso | Operações |
| --- | --- |
| `/loja` | `GET` (lista ou filtra por `?cidade=`), `POST` |
| `/loja/{id}` | `GET`, `PUT`, `DELETE` |
| `/carros` | `GET`, `POST` |
| `/carros/{id}` | `GET`, `PUT`, `DELETE` |
| `/cliente` | `GET`, `POST` |
| `/cliente/{id}` | `GET`, `PUT`, `DELETE` |
| `/alugueis` | `GET`, `POST` |
| `/alugueis/{id}` | `GET` |
| `/alugueis/{id}/devolucao` | `POST` |

Exemplo de aluguel:

```json
{
  "idCarro": "UUID-do-carro",
  "cpfCliente": "12345678901",
  "tempo": 3
}
```

O CPF precisa corresponder a um cliente cadastrado. Os POSTs retornam `201 Created` e o endereço do registro em `Location`. Entradas inválidas retornam `400`, registros não encontrados retornam `404` e conflitos de vínculo ou disponibilidade retornam `409`.

## Testes

Dentro de `aluguel_carroapi`, execute ` .\mvnw.cmd test `. Os testes usam H2 em memória; não alteram o banco PostgreSQL local.

## Limites desta versão

Este é um projeto acadêmico para execução local. Não há autenticação nem autorização de acesso; não publique a aplicação com dados pessoais reais. O CPF é usado apenas para localizar o cliente e os testes empregam números fictícios. O registro de devolução não calcula multas nem prorrogações.
