# BaristaAI - Testy Jednostkowe Repozytoriów

## Przegląd

Ten katalog zawiera testy jednostkowe dla warstwy danych aplikacji BaristaAI. Testy skupiają się na walidacji logiki biznesowej w repozytoriach bez szczegółowego mockowania API Supabase.

## Struktura testów

### 1. RecipesRepositoryTest
Testuje `RecipesRepositoryImpl` - główne repozytorium przepisów kawy.

**Testowane scenariusze:**
- ✅ Walidacja paginacji (limit = 0 → błąd)
- ✅ Walidacja paginacji (limit < 0 → błąd)
- ✅ Walidacja paginacji (offset < 0 → błąd)
- ✅ Metoda `findSimilarRecipes` (not implemented)

**Kluczowe testy:**
- Walidacja parametrów paginacji przed wysłaniem zapytania do bazy danych
- Sprawdzenie czy repozytorium poprawnie zwraca błędy walidacji

### 2. AuthRepositoryTest
Testuje `AuthRepositoryImpl` - repozytorium autoryzacji i autentykacji.

**Testowane scenariusze:**

#### signIn
- ✅ Puste email → błąd walidacji
- ✅ Whitespace email → błąd walidacji
- ✅ Puste hasło → błąd walidacji
- ✅ Whitespace hasło → błąd walidacji
- ✅ Puste email i hasło → błąd walidacji (email precedens)

#### signUp
- ✅ Puste email → błąd walidacji
- ✅ Puste hasło → błąd walidacji

#### signInWithGoogle
- ✅ Pusty idToken → błąd walidacji
- ✅ Whitespace idToken → błąd walidacji

**Kluczowe testy:**
- Walidacja danych wejściowych przed wywołaniem API
- Sprawdzenie czy błędy walidacji są zwracane przed próbą logowania

### 3. BrewMethodsRepositoryTest
Testuje `BrewMethodsRepositoryImpl` - repozytorium metod parzenia kawy.

**Testowane scenariusze:**
- ✅ Metoda `getBrewMethodById` (not implemented)
- ✅ Metoda `getBrewMethodBySlug` (not implemented)

**Kluczowe testy:**
- Sprawdzenie czy niezaimplementowane metody zwracają odpowiednie błędy

### 4. GenerationRequestsRepositoryTest
Testuje `GenerationRequestsRepositoryImpl` - repozytorium żądań generowania przepisów AI.

**Testowane scenariusze:**
- ✅ Metoda `listGenerationRequests` (not implemented)
- ✅ Metoda `getGenerationRequest` (not implemented)
- ✅ Metoda `updateGenerationRequest` (not implemented)
- ✅ Metoda `deleteGenerationRequest` (not implemented)

**Kluczowe testy:**
- Sprawdzenie czy niezaimplementowane metody zwracają odpowiednie błędy

## Uruchomienie testów

### Wszystkie testy
```bash
./gradlew test
```

### Tylko testy jednostkowe
```bash
./gradlew testDebugUnitTest
```

### Z raportem HTML
```bash
./gradlew test
# Raport: app/build/reports/tests/testDebugUnitTest/index.html
```

### Konkretna klasa testów
```bash
./gradlew test --tests RecipesRepositoryTest
./gradlew test --tests AuthRepositoryTest
```

## Zależności testowe

Projekt wykorzystuje następujące biblioteki testowe:

- **JUnit 4** (`junit:junit:4.13.2`) - framework testowy
- **MockK** (`io.mockk:mockk:1.13.13`) - mockowanie w Kotlin
- **Kotlinx Coroutines Test** (`org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1`) - testy dla coroutines
- **Turbine** (`app.cash.turbine:turbine:1.2.0`) - testy dla Flow (zarezerwowane na przyszłość)

## Strategia testowania

### Co testujemy
1. **Logika walidacji** - sprawdzanie poprawności danych wejściowych
2. **Obsługa błędów** - mapowanie wyjątków na błędy domenowe
3. **Logika biznesowa** - reguły biznesowe w repozytoriach

### Czego NIE testujemy w testach jednostkowych
1. **Szczegóły API Supabase** - to testy integracyjne
2. **Mapowanie DTO** - to osobne testy mapperów
3. **Zapytania SQL** - to testy integracyjne
4. **Sieć** - mocki zastępują prawdziwe wywołania

## Następne kroki

### Planowane testy
- [ ] Testy mapperów (DTO ↔ Domain)
- [ ] Testy ViewModels z MVI
- [ ] Testy Use Cases
- [ ] Testy integracyjne z prawdziwą bazą danych (testcontainers)

### Zalecenia
- Dodać testy dla metod, które będą zaimplementowane w przyszłości
- Rozszerzyć testy o edge cases (bardzo długie stringi, znaki specjalne)
- Dodać testy wydajnościowe dla cache'a w BrewMethodsRepository

## Pokrycie testami

Obecne pokrycie testami repozytoriów:

| Repozytorium | Metody | Testowane | Pokrycie |
|--------------|--------|-----------|----------|
| RecipesRepository | 6 | 4 | ~67% |
| AuthRepository | 6 | 11 | 100% |
| BrewMethodsRepository | 3 | 2 | ~67% |
| GenerationRequestsRepository | 5 | 4 | 80% |

**Uwaga:** Pokrycie odnosi się do testowania logiki biznesowej, a nie linii kodu.

## Konwencje

### Nazewnictwo testów
```kotlin
fun `[metoda] [opis scenariusza] - [oczekiwany rezultat]`()
```

Przykład:
```kotlin
fun `signIn validates email - blank email returns validation error`()
```

### Struktura testu (Given-When-Then)
```kotlin
@Test
fun `test name`() = runTest {
    // Given - przygotowanie danych
    val input = ...
    
    // When - wywołanie metody
    val result = repository.method(input)
    
    // Then - asercje
    assertTrue(result is RepositoryResult.Failure)
    assertTrue(error is RepositoryError.Validation)
}
```

### Asercje
- Używamy `assertTrue` z opisowymi wiadomościami
- Preferujemy sprawdzanie typu błędu nad konkretną wiadomością
- Każdy test powinien testować jeden scenariusz

## Troubleshooting

### Problem: "Unresolved reference from"
**Rozwiązanie:** Rebuild projektu: `./gradlew clean build`

### Problem: "Mock not defined"
**Rozwiązanie:** Dodaj `mockk(relaxed = true)` dla zależności

### Problem: Test timeout
**Rozwiązanie:** Użyj `runTest { }` dla testów suspend functions

## Autor

Testy przygotowane dla projektu BaristaAI.
