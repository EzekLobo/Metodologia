# Pipeline de Conteúdo e Prompts

Use estes prompts fora do app para produzir novos itens. O app continua 100% offline e apenas consome `app/src/main/assets/study_content.json`.

## Extração fiel

```text
Você receberá trechos de PDFs de Metodologia Científica.
Extraia apenas conceitos, definições, distinções e relações explicitamente presentes no texto.
Não use conhecimento externo.
Para cada item, informe PDF, página e trecho de apoio.
Se algo não estiver claramente sustentado, marque como INSUFICIENTE.
```

## Resumo metacognitivo

```text
Resuma o trecho para estudo, mantendo apenas ideias necessárias para prova.
Preserve termos importantes do material original.
Não adicione exemplos externos.
Inclua: ideia central, termos-chave, possíveis confusões do aluno, pergunta de autoverificação e fonte textual.
```

## Geração de cards

```text
Crie flashcards de recuperação ativa.
Evite cards de simples reconhecimento.
Prefira perguntas como: explique, diferencie, relacione, caracterize e identifique confusões comuns.
Cada card deve ter front, back, topicId, difficulty, source.pdf, source.page e source.excerpt.
```

## Validação anti-alucinação

```text
Revise cada item.
Verifique se a resposta está totalmente apoiada no trecho citado.
Classifique como VÁLIDO, PARCIAL ou INVÁLIDO.
Não corrija inventando. Reduza a resposta ao que o PDF permite afirmar.
```
