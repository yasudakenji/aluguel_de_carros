# Aluguel de carros

Esse projeto faz parte dos meus estudos com Java e Spring Boot. A ideia foi montar um sistema de aluguel de carros para colocar em prática o que estou aprendendo sobre API REST, banco de dados e relacionamento entre entidades.

O sistema tem uma tela simples para cadastrar lojas, carros e clientes, registrar um aluguel e fazer a devolução do carro.

O frontend foi desenvolvido por completo utilizando inteligência artificial (IA), a partir das minhas orientações sobre as telas e o funcionamento. O foco dos meus estudos neste projeto está no backend com Java e Spring Boot.

## Funcionalidades

- Cadastrar, consultar, editar e excluir lojas, carros e clientes.
- Vincular cada carro a uma loja cadastrada.
- Alugar um carro disponível, informando o CPF de um cliente cadastrado e a quantidade de dias.
- Mostrar automaticamente a loja do carro escolhido no aluguel.
- Calcular o valor usando a diária do carro multiplicada pelos dias.
- Registrar a devolução e deixar o carro disponível novamente.
- Impedir a exclusão de registros com vínculos ou histórico de aluguéis.

## Tecnologias utilizadas

| Tecnologia | Como foi usada |
| --- | --- |
| Java 25 | Linguagem usada para desenvolver o backend. |
| Spring Boot | Inicialização e configuração da aplicação e do servidor que recebe as requisições. |
| Spring Web | Criação dos endpoints HTTP para cadastro, consulta, edição, exclusão, aluguel e devolução. |
| Spring Data JPA e Hibernate | Mapeamento das entidades e dos relacionamentos com as tabelas e acesso aos dados pelos repositories. |
| PostgreSQL 16 | Banco de dados que armazena os cadastros e os aluguéis. |
| Docker | Execução do PostgreSQL em um container no ambiente local. |
| Maven Wrapper | Download das dependências, execução da aplicação e dos testes, sem precisar instalar o Maven separadamente. |
| Lombok | Redução de código repetitivo, como getters, setters e alguns construtores. |
| HTML, CSS e JavaScript | Construção da interface e envio das requisições para o backend usando `fetch`. |
| JUnit, Mockito e H2 | Testes dos endpoints e das regras, usando um banco em memória separado do PostgreSQL. |

O frontend é servido pelo próprio Spring Boot. Quando faço uma ação na tela, o JavaScript envia uma requisição HTTP para a API. O backend verifica os dados, acessa o banco e devolve uma resposta para a tela.

## Estrutura do projeto

- `controller`: recebe as requisições e devolve as respostas HTTP.
- `service`: concentra as regras, como verificar se um carro está disponível para alugar.
- `repository`: faz o acesso aos dados com Spring Data JPA.
- `entity`: representa as tabelas e os relacionamentos do banco.
- `dto`: define os dados que entram e saem pela API.
- `config`: contém configurações da aplicação, como a conexão com o banco.

Os arquivos da interface ficam em [`aluguel_carroapi/src/main/resources/static`](aluguel_carroapi/src/main/resources/static).

## Instalação e execução

### Pré-requisitos

- **Git:** baixe no [site oficial para Windows](https://git-scm.com/install/windows), execute o instalador e siga as instruções.
- **JDK 25:** uma opção é o [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=25&os=windows). Selecione Java 25, Windows, pacote JDK e o instalador `.msi` compatível com seu computador. Durante a instalação, mantenha a opção de adicionar ao `PATH` e habilite a opção de configurar `JAVA_HOME`. O [guia oficial do instalador](https://adoptium.net/installation/windows) mostra essas opções.
- **Docker Desktop:** siga o [guia oficial para Windows](https://docs.docker.com/desktop/setup/install/windows-install/). Ele explica os requisitos e a configuração do WSL 2, usado para rodar containers Linux no Windows. Depois da instalação, abra o Docker Desktop e aguarde o mecanismo do Docker iniciar.

Se o instalador pedir para reiniciar o computador, faça isso antes de continuar. Após instalar as ferramentas, feche os terminais antigos e abra um novo.

Os comandos abaixo são para Windows, usando PowerShell. Não precisa instalar uma IDE, Node.js ou Maven separado para executar o projeto. Na primeira execução, precisa de internet para baixar a imagem do banco e as dependências.

### 1. Abrir o terminal e conferir as ferramentas

Abra a pasta **Documentos** no Explorador de Arquivos. Clique na barra de endereço da pasta, digite `powershell` e pressione Enter. Isso abre o terminal nessa pasta.

Copie os comandos dos blocos abaixo e execute uma linha de cada vez, pressionando Enter. Confira as ferramentas:

```powershell
git --version
java -version
javac -version
docker version
```

Git deve mostrar uma versão; `java` e `javac` devem mostrar a versão 25. O comando do Docker deve mostrar informações de **Client** e **Server**. Se aparecer erro em alguma dessas etapas, veja a seção **Problemas comuns** antes de continuar.

### 2. Baixar o projeto

```powershell
git clone https://github.com/yasudakenji/car-rental-fullstack.git
cd car-rental-fullstack\aluguel_carroapi
```

Esses comandos baixam o projeto e entram na pasta da aplicação. Confira se você está no lugar certo:

```powershell
Get-ChildItem
```

Na lista devem aparecer `pom.xml`, `mvnw.cmd`, `comandos-sql.txt` e a pasta `src`. Mantenha esse terminal aberto e execute as próximas etapas nele.

### 3. Subir o banco de dados

Para criar um PostgreSQL novo no Docker:

```powershell
docker run -d --name acdb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=ac -v acdb_dados:/var/lib/postgresql/data postgres:16
```

O volume `acdb_dados` guarda os dados do banco entre as execuções do container. A porta 5432 precisa estar livre.

Na primeira execução, o Docker baixa a imagem do PostgreSQL. Aguarde o comando terminar. Você não precisa instalar o PostgreSQL separadamente.

Se o container `acdb` já existir, use este comando para iniciá-lo:

```powershell
docker start acdb
```

Confira se o banco está pronto:

```powershell
docker exec acdb pg_isready -U postgres -d ac
```

Quando aparecer `accepting connections`, pode seguir. Se aparecer `no response` ou `rejecting connections`, aguarde alguns segundos e execute a verificação novamente.

### 4. Criar as tabelas

**Se esta é a primeira instalação**, o banco está vazio. Execute dentro da pasta `aluguel_carroapi`, onde está o `pom.xml`:

```powershell
Get-Content -Raw .\comandos-sql.txt | docker exec -i acdb psql -U postgres -d ac -v ON_ERROR_STOP=1 --single-transaction -f -
```

Se aparecerem mensagens `CREATE TABLE` sem erros, as tabelas foram criadas.

**Somente para quem já usava a versão anterior:** se as tabelas já existiam, execute a migração no lugar do comando acima. Quem acabou de criar as tabelas pode pular este comando:

```powershell
Get-Content -Raw .\migracao-banco-existente.sql | docker exec -i acdb psql -U postgres -d ac -v ON_ERROR_STOP=1 --single-transaction -f -
```

A migração mantém os registros, adiciona o campo de devolução, permite aluguel sem funcionário e padroniza o CPF para guardar somente números. A aplicação valida a estrutura ao iniciar; ela não cria as tabelas automaticamente.

Arquivos SQL: [criação das tabelas](aluguel_carroapi/comandos-sql.txt) e [migração do banco existente](aluguel_carroapi/migracao-banco-existente.sql).

Também é possível executar esses arquivos pelo pgAdmin ou usar um PostgreSQL instalado diretamente no computador.

### 5. Iniciar a aplicação

Na pasta que contém o `pom.xml`:

```powershell
.\mvnw.cmd spring-boot:run
```

A primeira execução pode demorar enquanto as dependências são baixadas. Aguarde aparecer `Started Application` e a mensagem de que o Tomcat iniciou na porta 8080.

Deixe o terminal aberto. Abra seu navegador e acesse [http://localhost:8080](http://localhost:8080). A tela do sistema deve aparecer.

Para encerrar a aplicação, volte ao terminal e pressione **Ctrl + C**. Para usar novamente, abra o Docker Desktop, inicie o container com `docker start acdb` e execute ` .\mvnw.cmd spring-boot:run ` na pasta do projeto. Não precisa recriar as tabelas a cada execução.

### Opcional: usar outra configuração de banco

A configuração padrão usa o banco `ac`, na porta `5432`, com usuário e senha `postgres`. Para usar outro banco, ajuste as variáveis antes de iniciar a aplicação:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/ac"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "sua-senha"
.\mvnw.cmd spring-boot:run
```

### Problemas comuns

| O que apareceu | O que conferir |
| --- | --- |
| `git`, `java`, `javac` ou `docker` não é reconhecido | Confira a instalação da ferramenta e abra um novo PowerShell. Para o Java, confira as opções `PATH` e `JAVA_HOME` do instalador. |
| `JAVA_HOME` não está definido corretamente | Execute novamente o instalador do JDK, escolha a opção de modificar a instalação e habilite a configuração de `JAVA_HOME`. Abra um novo terminal. |
| Docker mostra erro de conexão ou não mostra `Server` | Abra o Docker Desktop e aguarde iniciar. Se ele indicar um problema de WSL ou virtualização, siga a orientação exibida e o guia oficial de instalação. |
| O nome `acdb` já está em uso | O container já foi criado. Use `docker start acdb` no lugar de `docker run`. |
| A porta `5432` já está em uso | Pode haver outro PostgreSQL ou container usando essa porta. Identifique qual é antes de iniciar um segundo banco. |
| `mvnw.cmd` ou `comandos-sql.txt` não foi encontrado | Você precisa estar na pasta interna `aluguel_carroapi`, onde fica o `pom.xml`. Confira com `Get-ChildItem`. |
| `relation ... does not exist` ou falta de tabela ao iniciar | Confira se executou o arquivo SQL de criação das tabelas no banco `ac`. |
| A porta `8080` já está em uso | Pode existir outra execução da aplicação, inclusive pela IDE. Encerre essa execução antes de iniciar outra. |

## Como usar

Primeiro, cadastre uma loja. Depois, cadastre um carro e escolha a loja à qual ele pertence. Cadastre também um cliente com CPF.

Na seção **Alugar**, escolha um carro disponível, informe o CPF do cliente e a quantidade de dias. A loja é preenchida automaticamente com base no carro, e o sistema mostra o valor previsto.

Após registrar o aluguel, o carro fica como alugado. Para liberá-lo, use a opção **Devolver** na lista de aluguéis.

## Endpoints da API

| Caminho | Métodos |
| --- | --- |
| `/loja` | GET e POST |
| `/loja/{id}` | GET, PUT e DELETE |
| `/carros` | GET e POST |
| `/carros/{id}` | GET, PUT e DELETE |
| `/cliente` | GET e POST |
| `/cliente/{id}` | GET, PUT e DELETE |
| `/alugueis` | GET e POST |
| `/alugueis/{id}` | GET |
| `/alugueis/{id}/devolucao` | POST |

Também dá para filtrar lojas pela cidade, por exemplo: `GET /loja?cidade=Curitiba`.

## Testes

```powershell
.\mvnw.cmd test
```

Os testes usam H2 em memória e não alteram os dados do PostgreSQL. Para rodar esses testes, não precisa iniciar o container.

## Escopo desta versão

Esse é um projeto de estudos para execução local. Nesta versão, ainda não há autenticação, cálculo de multa ou prorrogação do aluguel.

## Autor

Vitor Kenji — [@yasudakenji](https://github.com/yasudakenji).
