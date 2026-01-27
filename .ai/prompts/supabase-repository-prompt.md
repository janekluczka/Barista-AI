Jestes doswiadczonym Android (Kotlin) architektem i masz przygotowac repozytorium (interfejs + implementacja) do obslugi Supabase w aplikacji. Repozytorium ma byc zgodne z Clean Architecture i MVI oraz gotowe do uzycia z `SupabaseClient`. Zapoznaj sie dokladnie z dokumentami wejsciowymi i przygotuj plan oraz propozycje API repozytorium.

Najpierw dokladnie przejrzyj nastepujace dane wejsciowe:

<database_models>
@supabase/migrations/create_db_all.sql
@supabase/migrations/create_db_tables.sql
@supabase/migrations/create_db_policies.sql
@.ai/db-details.md
@.ai/db-plan.md
</database_models>

<tech_stack>
@.ai/tech-stack.md
</tech_stack>

<requirements>
Zastosuj Clean Architecture, MVI, Kotlin, Hilt oraz Supabase (PostgREST + RLS).
Zaimplementuj interfejsy repository w warstwie domenowej oraz ich implementacje w warstwie data.
Repozytoria maja wspierac operacje: listowanie, pobieranie szczegolow, tworzenie, aktualizacje, usuwanie,
oraz dodatkowe zapytania wynikajace z biznesu (np. podobne przepisy, logi akcji).
Uwzglednij autoryzacje i polityki RLS w kontraktach metod.
</requirements>

Twoje zadanie:
1. Przeanalizuj schemat bazy danych, relacje i ograniczenia.
2. Okresl potrzebne repozytoria i ich odpowiedzialnosci (np. RecipesRepository, GenerationRequestsRepository).
3. Zaprojektuj interfejsy repository w warstwie domenowej:
   - nazwy metod (z czasownikami)
   - sygnatury (parametry, typy zwracane, Result/Outcome)
   - metody do filtrowania, paginacji i sortowania
   - obsluga bledow i przypadkow brzegowych
4. Zaprojektuj implementacje repository w warstwie data:
   - mapowanie DTO <-> modele domenowe
   - zapytania Supabase (select/insert/update/delete) i parametry
   - uwzglednij RLS i auth.uid() w praktyce uzycia
5. Zaproponuj warstwe abstrakcji nad SupabaseClient:
   - np. DataSource lub Gateway, jesli to upraszcza testy
6. Zaproponuj organizacje pakietow i plikow zgodnie ze struktura projektu.

Przed dostarczeniem ostatecznego wyniku, pracuj wewnatrz tagow <repo_analysis> w swoim bloku myslenia:
1. Wymien kluczowe encje i relacje ze schematu (z cytatami).
2. Wypisz wymagane operacje repozytorium per encja.
3. Zmapuj operacje na konkretne zapytania Supabase (select/insert/update/delete/RPC).
4. Wskaz potencjalne ryzyka (RLS, ograniczenia, walidacja, wydajnosc).

Ostateczny wynik ma byc sformatowany w markdown i zawierac sekcje:

```markdown
# Supabase Repository Plan

## 1. Repository List and Responsibilities

## 2. Domain Interfaces (Kotlin)
- Method signatures with input/output types
- Error handling strategy

## 3. Data Implementations (Kotlin)
- Supabase queries per method
- DTO/domain mapping notes

## 4. Data Sources and DI
- Proposed data sources or gateways
- Hilt bindings

## 5. Edge Cases and Validation
- Constraints, RLS, pagination, sorting

## 6. Package Structure
```

Upewnij sie, ze wynik jest kompletny i spójny z dokumentami wejsciowymi. Nie dodawaj dodatkowych wyjasnien poza wynikiem w markdown.
