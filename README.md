# Metodologia Cards

App Android offline para estudar Metodologia Científica com flashcards, quizzes, revisão de erros e metacognição.

## Como abrir

1. Abra esta pasta no Android Studio.
2. Aguarde o Gradle sincronizar.
3. Rode o módulo `app` em um emulador ou aparelho Android.

## Garantias da v1

- Sem backend, login ou permissão de internet.
- Conteúdo embarcado em `app/src/main/assets/study_content.json`.
- Cada card, quiz e relação possui PDF, página e trecho-base.
- Progresso salvo localmente em `SharedPreferences`.

## Testes

Validação rápida de conteúdo/offline nesta pasta:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/validate-content.ps1
```

No Android Studio, rode a task Gradle `testDebugUnitTest` ou `test`.

Os testes validam que o conteúdo tem fonte obrigatória, quizzes estão completos e o app não solicita permissão de internet. Este ambiente atual não tem Java/Gradle/Android SDK instalados, então a build completa deve ser executada no Android Studio.
