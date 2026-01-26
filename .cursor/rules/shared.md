---
Rule Type: Always
---

# AI Rules for BaristaAI

BaristaAI is an Android app (min API 30) that helps beginners brew coffee with AI-generated recipes and lets them save, edit, and repeat recipes.

## Tech Stack

- Kotlin
- Jetpack Compose
- Hilt (DI)
- Supabase (backend)
- OpenRouter.ai (AI)
- GitHub Actions (CI/CD)

## Project Structure

Use Clean Architecture with MVI. When introducing changes to the project, follow the structure below:

- `app/src/main/java/com/luczka/baristaai` - app root package
- `app/src/main/java/com/luczka/baristaai/di` - Hilt modules
- `app/src/main/java/com/luczka/baristaai/data` - data sources, DTOs, mappers, repository impls
- `app/src/main/java/com/luczka/baristaai/domain` - models, repository interfaces, use cases
- `app/src/main/java/com/luczka/baristaai/presentation` - feature UI + MVI
- `app/src/main/java/com/luczka/baristaai/presentation/<feature>` - `ui`, `mvi`, `components`, `navigation`
- `app/src/main/res` - resources

When modifying the directory structure, always update this section.

## Coding practices

### Guidelines for clean code

- Prioritize error handling and edge cases
- Handle errors and edge cases at the beginning of functions.
- Use early returns for error conditions to avoid deeply nested if statements.
- Place the happy path last in the function for improved readability.
- Avoid unnecessary else statements; use if-return pattern instead.
- Use guard clauses to handle preconditions and invalid states early.
- Implement proper error logging and user-friendly error messages.
- Consider using custom error types or error factories for consistent error handling.

### MVI in Compose

- Use unidirectional flow: `Intent` -> `ViewModel` -> `State` / `Effect`.
- Keep `State` immutable data classes and use `StateFlow`.
- Use `Effect` for one-off events (navigation, toasts).
- Keep Composables stateless; pass state and event handlers from ViewModel.
