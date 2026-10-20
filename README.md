# Sistema de Controle de Estoque

API REST feita com Java 21 e Spring Boot 4.0.8 para gerenciar um sistema de controle de estoque.

Desenvolvi esse projeto para praticar Spring Boot na prática: autenticação com JWT, CRUD de produtos e clientes, movimentação de estoque, upload de imagens, relatórios em PDF e testes de integração.

## Tecnologias

- Java 21
- Spring Boot 4.0.8
- Spring Data JPA / Hibernate
- Spring Security com JWT
- Bean Validation
- Records (DTOs imutáveis)
- PostgreSQL
- H2 (para testes)
- Docker & Docker Compose
- Swagger/OpenAPI (SpringDoc)
- JasperReports
- JUnit + WebTestClient
- Lombok
- Maven

## Modelo de Dados

O diagrama abaixo mostra as entidades principais e os relacionamentos entre elas:

![Diagrama de Entidades](docs/diagrama-entidades.png)

Entidades e relacionamentos principais:

- **Product** — produtos com controle de quantidade e upload de imagem. Cada produto pertence a uma **Category** e a um **Supplier**.
- **Category** — categorias para organizar os produtos. Uma categoria pode ter vários produtos.
- **StockMovement** — movimentações de entrada e saída de estoque com código sequencial. Cada movimentação está vinculada a um **Product** e a um **Client**.
- **Client** — clientes que podem ter várias movimentações de estoque associadas.
- **Supplier** — fornecedores vinculados aos produtos.
- **User** — usuários do sistema com controle de acesso por perfil (ADMIN ou ATTENDANT).

## Autenticação

A API usa Spring Security com JWT. Existem dois perfis:

- `ADMIN`
- `ATTENDANT`

Alguns endpoints são restritos por perfil.

## Documentação da API

Depois de subir a aplicação, acesse o Swagger em:

`http://localhost:8080/docs-stock.html`

Lá dá para ver e testar todos os endpoints.

## Relatórios

O projeto gera relatórios em PDF com JasperReports. Por enquanto tem:

- **Movimentações de Estoque** — PDF com as entradas e saídas de estoque

## Testes

Os testes E2E cobrem os principais fluxos da API:

- Login e geração de token JWT
- Produtos (CRUD + upload de imagem)
- Categorias, clientes e fornecedores (CRUD)
- Movimentações de estoque (entrada e saída)
- Gerenciamento de usuários
- Geração de relatórios em PDF

## Pré-requisitos

- Java 21+
- PostgreSQL
- Git
- Docker e Docker Compose (opcional)
- Maven (ou use o Maven Wrapper do projeto)

## Banco de Dados

Por padrão, a aplicação usa PostgreSQL:

- **Database:** `stock_api`
- **Username:** `postgres`
- **Password:** `postgres`
- **Port:** `5432`

As credenciais do banco e outras configurações ficam em variáveis de ambiente:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `IMAGE_UPLOAD_DIR`

## Usuário de demonstração

Quando a API sobe pela primeira vez, um usuário admin é criado automaticamente:

- **E-mail:** `admin@stock.com`
- **Senha:** `admin123`
- **Perfil:** `ADMIN`

> Cuidado: essas credenciais são só para testes. Mude a senha em produção.

Use esse usuário no endpoint de login para pegar o token JWT e depois criar outros usuários.

## Como rodar

1. Clone o repositório:

```bash
git clone https://github.com/Marques-Souza/stock.git
cd stock
```

2. Configure as variáveis de ambiente (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`) na sua IDE ou no sistema operacional.

3. Rode a aplicação:

```bash
cd stock
./mvnw spring-boot:run
```

4. Acesse o Swagger:

`http://localhost:8080/docs-stock.html`

## Rodando com Docker

```bash
docker-compose up --build
```

Depois é só acessar `http://localhost:8080/docs-stock.html`.

## Estrutura do projeto

```
stock/
├── stock/                      # Módulo Spring Boot
│   ├── src/main/java/com/marquesdev/stock/
│   │   ├── config/             # Security, Swagger, Upload
│   │   ├── controller/         # 8 controllers REST
│   │   ├── dto/                # 19 DTOs (Records)
│   │   ├── entity/             # 6 entidades JPA
│   │   ├── enums/              # Profile, Type
│   │   ├── exception/          # 11 exceptions customizadas
│   │   ├── jwt/                # Autenticação JWT
│   │   ├── mapper/             # 6 mappers
│   │   ├── repository/         # 6 repositories JPA
│   │   ├── service/            # 8 services
│   │   ├── validator/          # Validadores customizados
│   │   └── Application.java    # Classe principal
│   ├── src/main/resources/
│   │   └── application.yaml    # Configuração
│   ├── src/test/               # Testes E2E
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
├── uploads/                    # Imagens enviadas
└── README.md
```

## Licença

Projeto desenvolvido por **Marques Souza** para fins de estudo e portfólio.
