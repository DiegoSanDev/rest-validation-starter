# REST Validation Starter

**Desenvolvido via Vibecoding com Gemini**
*Vibecoding é um método de desenvolvimento de software onde, em vez de programar linha por linha, você orienta uma inteligência artificial a criar, refinar e depurar aplicativos por meio de instruções em linguagem natural.*

[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Spring Boot auto-configurable starter para tratamento centralizado, padronizado e extensível de erros de validação em APIs REST.

---

## 🚀 Por que usar?

Em arquiteturas modernas de microsserviços, a consistência é fundamental. Este starter resolve:
- **Contrato Único:** Front-ends e consumidores da API lidam com um único formato de erro.
- **Redução de Boilerplate:** Esqueça a criação manual de `@RestControllerAdvice` em cada projeto.
- **Flexibilidade Total:** Adicione metadados em tempo de execução sem acoplamento.
- **Auto-Documentação:** Integração nativa para que o Swagger reflita os erros reais.

---

## 🛠 Tecnologias Utilizadas

| Tecnologia | Versão | Função | Link Oficial |
| :--- | :---: | :--- | :--- |
| **Java** | 21 | Linguagem base (Records, Pattern Matching) | [Link](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) |
| **Spring Boot** | 3.3.0+ | Framework base e Auto-configuration | [Link](https://spring.io/projects/spring-boot) |
| **Jakarta Validation** | 3.0 | API de especificação de validação de beans | [Link](https://beanvalidation.org/) |
| **Springdoc OpenAPI** | 2.5.0 | Documentação interativa da API | [Link](https://springdoc.org/) |
| **SLF4J / Logback** | - | Logging estruturado de exceções | [Link](http://www.slf4j.org/) |

---

## 📂 Arquitetura e Estrutura

O projeto é dividido em camadas de responsabilidade bem definidas para facilitar a extensão:

### 1. Modelagem (`.model`)
- **`ErrorDetail`**: Record que isola a falha. Contém `field` (campo) e `message` (descrição).
- **`ErrorResponse`**: O "Envelope" da resposta. Contém `timestamp`, `message`, `errors` e usa `@JsonAnyGetter` para permitir que metadados dinâmicos apareçam no nível raiz do JSON.

### 2. Mapeamento (`.mapper`)
- **`ViolationMapper`**: Interface que dita como converter violações do Jakarta/Spring.
- **`DefaultViolationMapper`**: Implementação robusta que navega na árvore de nós (`Path.Node`) para extrair apenas o nome do campo folha, evitando nomes longos como `save.userDTO.email`.

### 3. Tratamento de Exceções (`.handler`)
- **`GlobalValidationHandler`**: Advice central que intercepta as seguintes exceções comuns do Spring/Jakarta Validation:
    - `MethodArgumentNotValidException` (para `@Valid` em `@RequestBody`)
    - `ConstraintViolationException` (para `@Validated` em parâmetros de método ou beans)
    - `HttpMessageNotReadableException` (para problemas de desserialização do JSON)
    - `MethodArgumentTypeMismatchException` (para falha na conversão de tipo de parâmetros de URL/query)
    - `MissingServletRequestParameterException` (para parâmetros de requisição obrigatórios ausentes)
- **`ValidationContext`**: Bridge estática thread-safe para injetar dados extras na resposta a partir de qualquer camada (Service, Controller).
- **`ErrorResponseCustomizer`**: Ponto de extensão via Bean para customização global e estática.

### 4. Configuração (`.config`)
- **`ValidationProperties`**: Centraliza as chaves de configuração `rest-validation.*`.
- **`ValidationAutoConfiguration`**: Gerencia a criação condicional dos Beans (`@ConditionalOnMissingBean`).
- **`ValidationOpenApiConfiguration`**: Injeta automaticamente as definições de erro no Swagger UI.

---

## 💻 Guia de Uso Detalhado

### 1. Validação Básica
Basta anotar seus DTOs e utilizar `@Valid`.

```java
public record UserRequest(
    @NotBlank(message = "Nome obrigatório") String name,
    @Email(message = "E-mail inválido") String email
) {}

@PostMapping
public ResponseEntity<Void> create(@Valid @RequestBody UserRequest request) {
    return ResponseEntity.ok().build();
}
```

### 2. Validação por Grupos (OnCreate / OnUpdate)
Útil para regras que mudam conforme o contexto da requisição (ex: ID deve ser nulo no POST, mas obrigatório no PUT).

```java
import io.github.restvalidation.validation.group.OnCreate;
import io.github.restvalidation.validation.group.OnUpdate;

public record ProductRequest(
    @Null(groups = OnCreate.class) @NotNull(groups = OnUpdate.class) Long id,
    @NotBlank String name
) {}

@PostMapping
public void save(@Validated(OnCreate.class) @RequestBody ProductRequest dto) { ... }
```

### 3. Metadados Dinâmicos (`ValidationContext`)
Adicione informações contextuais sem alterar a assinatura dos métodos ou records.

```java
@Service
public class OrderService {
    public void process(OrderRequest request) {
        // Estes dados aparecerão no JSON de erro APENAS se houver uma falha nesta requisição
        ValidationContext.put("order_ref", request.reference());
        ValidationContext.put("user_tier", "GOLD");
        
        // Lógica que pode disparar erro de validação...
    }
}
```

### 4. Customização Global (`ErrorResponseCustomizer`)
Se você deseja que **todo** erro contenha informações como a versão da API.

```java
@Component
public class GlobalCustomizer implements ErrorResponseCustomizer {
    @Override
    public Map<String, Object> customize(Exception ex) {
        return Map.of("api_version", "v1.2.0");
    }
}
```

---

## 📄 Exemplos de Respostas de Erro

A seguir, alguns exemplos de como as respostas de erro são padronizadas pelo starter.

### 1. Erro de Validação (HTTP 422 - Unprocessable Entity)

Quando um ou mais campos de um DTO falham nas regras de validação.

```json
{
  "timestamp": "2023-10-27T10:30:00.123456789Z",
  "message": "Erro de validação detectado.",
  "errors": [
    {
      "field": "name",
      "message": "Nome obrigatório"
    },
    {
      "field": "email",
      "message": "E-mail inválido"
    }
  ]
}
```

### 2. Erro de Conversão de Tipo (HTTP 400 - Bad Request)

Ocorre quando um parâmetro da requisição não pode ser convertido para o tipo esperado (ex: string para número).

```json
{
  "timestamp": "2023-10-27T10:35:00.987654321Z",
  "message": "O parâmetro 'age' recebeu um valor inválido: 'abc'.",
  "errors": []
}
```

### 3. Erro com Metadados Dinâmicos (HTTP 422 - Unprocessable Entity)

Exemplo de uma resposta de validação que inclui metadados adicionados via `ValidationContext`.

```json
{
  "timestamp": "2023-10-27T10:40:00.555444333Z",
  "message": "Erro de validação detectado.",
  "order_ref": "ORD-2023-001",
  "user_tier": "GOLD",
  "errors": [
    {
      "field": "productCode",
      "message": "Código do produto inválido"
    }
  ]
}
```

### 4. Erro Interno do Servidor (HTTP 500 - Internal Server Error)

Para erros inesperados no servidor. A mensagem é genérica por segurança.

```json
{
  "timestamp": "2023-10-27T10:45:00.111222333Z",
  "message": "Ocorreu um erro interno crítico.",
  "errors": []
}
```

---

## ⚙️ Configuração via application.yml

Você pode sobrescrever as mensagens padrão sem precisar de código:

```yaml
rest-validation:
  messages:
    validation-failed: "Erro de validação detectado."
    type-mismatch: "O parâmetro '%s' recebeu um valor inválido: '%s'."
    not-found: "Recurso não localizado."
    internal-error: "Ocorreu um erro interno crítico."
```

### Internacionalização (i18n)

O starter suporta internacionalização das mensagens de erro utilizando os arquivos `messages.properties` do Spring. Ele busca automaticamente as traduções com base no `Locale` da requisição (geralmente definido pelo cabeçalho `Accept-Language`).

**Como configurar:**

1.  **Crie os arquivos `messages.properties`:**
    Coloque-os na pasta `src/main/resources/` do seu projeto.

    *   **`src/main/resources/messages.properties` (para o idioma padrão, ex: Inglês):**
        ```properties
        rest-validation.validation-failed=Validation error detected.
        rest-validation.type-mismatch=The parameter '{0}' received an invalid value: '{1}'.
        rest-validation.not-found=Resource not found.
        rest-validation.internal-error=An critical internal error occurred.
        ```

    *   **`src/main/resources/messages_pt_BR.properties` (para Português do Brasil):**
        ```properties
        rest-validation.validation-failed=Erro de validação detectado.
        rest-validation.type-mismatch=O parâmetro '{0}' recebeu um valor inválido: '{1}'.
        rest-validation.not-found=Recurso não localizado.
        rest-validation.internal-error=Ocorreu um erro interno crítico.
        ```

2.  **Envie o cabeçalho `Accept-Language` na requisição:**
    O cliente da API deve enviar o cabeçalho `Accept-Language` com o `Locale` desejado.

    *   **Exemplo de requisição para Português do Brasil:**
        ```
        GET /api/users
        Accept-Language: pt-BR
        ```

    *   **Exemplo de requisição para Inglês:**
        ```
        GET /api/users
        Accept-Language: en
        ```

**Exemplo de Resposta com i18n (para `pt-BR`):**

Se a requisição for feita com `Accept-Language: pt-BR` e houver um erro de validação:

```json
{
  "timestamp": "2023-10-27T10:30:00.123456789Z",
  "message": "Erro de validação detectado.",
  "errors": [
    {
      "field": "name",
      "message": "Nome obrigatório"
    },
    {
      "field": "email",
      "message": "E-mail inválido"
    }
  ]
}
```
*(Note que as mensagens dos campos individuais (`name`, `email`) vêm das anotações `@NotBlank(message="Nome obrigatório")`, e também podem ser internacionalizadas usando chaves de mensagens em `messages.properties` se você não quiser hardcodar a mensagem diretamente na anotação.)*

---

## 📊 Observabilidade e Logs

A biblioteca foi desenhada para facilitar a vida da equipe de SRE/DevOps:
- **`WARN`**: Logado para erros de validação (422) e Bad Request (400). Útil para identificar bugs no cliente.
- **`ERROR`**: Logado para erros genéricos (500). O stacktrace completo é enviado para o log, mas **não** é exposto no JSON de resposta por segurança.

---

## 🛠 Instalação (Ainda não disponível em repositório Maven remoto)

```xml
<dependency>
    <groupId>io.github.restvalidation</groupId>
    <artifactId>rest-validation-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Para testar localmente:** Para utilizar este starter em outros projetos, você precisará executar `mvn clean install` no diretório raiz deste projeto. Isso instalará o artefato no seu repositório Maven local.

---

## 📄 Licença
Distribuído sob a licença **MIT**. Sinta-se livre para usar, modificar e distribuir.