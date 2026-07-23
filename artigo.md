# Cansado de Erros de Validação Inconsistentes nas Suas APIs? Conheça o `rest-validation-starter`!

Olá, comunidade de desenvolvimento! 👋

Se você já construiu APIs REST com Spring Boot, sabe o desafio de garantir que os erros de validação sejam claros e padronizados para o **client da requisição**. Muitas vezes, isso significa código repetitivo (`boilerplate`) só para formatar mensagens.

**Atenção: Este projeto foi desenvolvido via Vibecoding com Gemini!**
Sim, você leu certo! Em vez de programar linha por linha, eu "conversei" com a inteligência artificial, orientando-a para criar, refinar e depurar o código. Uma experiência de desenvolvimento fascinante e muito produtiva!

**A Solução: `rest-validation-starter`**
Este "pacote" pronto para usar cuida de toda a parte chata de formatar e apresentar os erros de validação das suas APIs. Ele garante que, não importa qual campo deu problema ou qual tipo de erro ocorreu, o **client da requisição** sempre receberá uma resposta padronizada e fácil de entender.

**Na prática, o que ele resolve?**
*   **Consistência:** O client sempre recebe o mesmo formato de erro, com o campo e a mensagem clara.
*   **Foco:** Você foca na lógica de negócio, e o starter cuida dos detalhes da validação.
*   **Auto-Documentação:** Já vem pronto para integrar com o Swagger, mostrando os erros na documentação da API.

**Veja como o erro aparece para o client:**

**1. Erro de Validação (HTTP 422):**
Se o nome e e-mail são obrigatórios e vêm vazios:

```json
{
  "timestamp": "...",
  "message": "Erro de validação detectado.",
  "errors": [
    { "field": "name", "message": "Nome obrigatório" },
    { "field": "email", "message": "E-mail inválido" }
  ]
}
```

**2. Com Metadados Dinâmicos (HTTP 422):**
Você pode adicionar informações extras, como um ID de pedido, sem complicar seu código:

```json
{
  "timestamp": "...",
  "message": "Erro de validação detectado.",
  "order_ref": "ORD-2023-001",
  "user_tier": "GOLD",
  "errors": [
    { "field": "productCode", "message": "Código do produto inválido" }
  ]
}
```

**[IMAGEM AQUI: Print do Swagger mostrando a definição de erro ou um exemplo de erro retornado]**
**[IMAGEM AQUI: Print de um DTO com anotações de validação e um controller usando `@Valid`]**

**Sua Contribuição é Bem-Vinda!**
Acredito que as melhores soluções nascem da colaboração. Convido você a explorar o projeto no GitHub. Faça um *fork*, traga novas ideias, sugira melhorias, ajuste eventuais bugs ou simplesmente use e dê seu feedback. Sua contribuição é muito valiosa!

Para os detalhes técnicos, como instalar, usar e configurar a internacionalização (i18n), confira o `README.md` completo no repositório:
[Link para o seu repositório GitHub]

Vamos juntos construir APIs mais robustas e amigáveis!

#SpringBoot #RESTAPI #Java #Vibecoding #InteligenciaArtificial #DesenvolvimentoDeSoftware #OpenSource #APIValidation
