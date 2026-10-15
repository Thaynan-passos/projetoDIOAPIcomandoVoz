# Budgeting AI — API de orçamento controlada por voz

API em **Spring Boot + Spring AI** que entende comandos de voz sobre finanças
("gastei 32 reais no mercado hoje", "quanto gastei este mês?"), executa funções
reais da aplicação e responde em áudio.

## O que o projeto faz

1. Recebe um arquivo de áudio.
2. Transcreve o áudio em texto (`TranscriptionModel`).
3. O modelo de linguagem (`ChatClient`) decide qual **tool** chamar (Tool Calling).
4. A tool executa um caso de uso que cria ou consulta transações.
5. A resposta final é convertida em voz (`TextToSpeechModel`) e devolvida em MP3.

## Arquitetura

```
domain/           modelo (Transaction) + contrato do repositório + regras de negócio
application/      casos de uso (usados pelo REST e pelas tools da IA)
infrastructure/
  persistence/    adaptador JPA (H2)
  web/            controllers REST e tratamento de erros
  ai/             BudgetingTools (@Tool) e AssistantService (fluxo de voz)
```

A IA **não acessa o banco**: ela só chama os mesmos casos de uso do REST.

## Melhorias implementadas

- **Validações antes de salvar** (`domain/Transaction`): valor > 0, descrição obrigatória,
  data não pode ser futura, categoria padrão "outros". Erros viram HTTP 400 no REST e
  são devolvidos ao modelo nas tools, que explica o problema para a pessoa usuária.
- **Nova consulta financeira** (`SummarizeSpendingUseCase` + tool `summarizeSpending`):
  total de receitas, total de gastos, saldo e gastos agrupados por categoria em um período.
- **Testes** sem dependência da OpenAI (`TransactionTest`, `SummarizeSpendingUseCaseTest`).

## Tecnologias

Java 21+, Spring Boot 3.5, Spring AI 1.0 (OpenAI), Spring Data JPA, H2, Gradle, JUnit 5.

## Como executar

Pré-requisitos: JDK 21 ou superior e uma chave da OpenAI.

```bash
# Linux/macOS
export OPENAI_API_KEY="sua_chave"
# Windows (PowerShell)
$env:OPENAI_API_KEY="sua_chave"

./gradlew bootRun
```

O banco é H2 em memória (não precisa de Docker). Console em `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:budgeting`).

Testes (não precisam da chave):

```bash
./gradlew test
```

## Como testar o fluxo principal

Os exemplos prontos estão em [`requests.http`](requests.http) (IntelliJ ou VS Code).

**Sem áudio (mais fácil para começar):**

```bash
curl -X POST http://localhost:8080/api/assistant/text \
  -H "Content-Type: application/json" \
  -d '{"message":"Gastei 32 reais no mercado hoje"}'
```

**Com áudio (fluxo completo):**

```bash
curl -X POST http://localhost:8080/api/assistant/voice \
  -F "audio=@comando.mp3" --output resposta.mp3
```

**REST direto:**

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/transactions` | cria transação |
| GET | `/api/transactions?from=&to=` | lista (padrão: mês atual) |
| GET | `/api/transactions/summary?from=&to=` | resumo por categoria |
| POST | `/api/assistant/text` | assistente por texto |
| POST | `/api/assistant/voice` | assistente por voz (entrada e saída em áudio) |

## O que aprendi

> Escreva aqui com as suas palavras: o que foi mais difícil, o que você entendeu sobre
> Tool Calling, por que a IA chama casos de uso em vez de acessar o banco, e o que
> pretende melhorar depois. Inclua prints e exemplos de requisição.
