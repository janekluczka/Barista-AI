<db-plan>
@db-plan.md
</db-plan>

<prd>
@prd.md
</prd>

<tech-stack>
@tech-stack.md
</tech-stack>

Jestes doswiadczonym architektem aplikacji i backendu. Twoim zadaniem jest stworzenie kompleksowego planu implementacji dostepu do danych z uzyciem SupabaseClient (Kotlin/Android). Plan ma bazowac na schemacie bazy danych, dokumencie PRD oraz stacku technologicznym podanym powyzej. Uwaznie przejrzyj dane wejsciowe i wykonaj nastepujace kroki:

1. Przeanalizuj schemat bazy danych:
   - Zidentyfikuj glowne encje (tabele) i ich kluczowe pola
   - Zanotuj relacje miedzy jednostkami
   - Zwracaj uwage na indeksy oraz ograniczenia/warunki walidacji
   - Zidentyfikuj wymagane reguly RLS (jesli wspomniane lub wynikaja z PRD)

2. Przeanalizuj PRD:
   - Zidentyfikuj kluczowe funkcje i przeplywy uzytkownika
   - Zwracaj uwage na konkretne operacje na danych (pobieranie, tworzenie, aktualizacja, usuwanie)
   - Wypisz wymagania logiki biznesowej wykraczajace poza CRUD

3. Rozwaz stack technologiczny:
   - Plan ma byc zgodny z: Kotlin, Jetpack Compose, Hilt, Ktor, Supabase, PostgreSQL, RLS
   - Zakladaj uzycie SupabaseClient po stronie aplikacji (Android)
   - Uwzglednij uzycie Supabase Auth, Storage i RPC, jesli potrzebne

4. Stworz kompleksowy plan implementacji dostepu do danych:
   - Zdefiniuj glowne zasoby/encje i mapowanie na modele domenowe/DTO
   - Zaprojektuj operacje SupabaseClient (select/insert/update/delete) dla kazdego zasobu
   - Zaproponuj wywolania funkcji RPC (jesli uzasadnione) dla logiki biznesowej
   - Uwzglednij paginacje, filtrowanie i sortowanie w zapytaniach Supabase
   - Zdefiniuj struktury danych wejscia/wyjscia (modele DTO)
   - Uwzglednij uwierzytelnianie i autoryzacje w kontekscie Supabase Auth i RLS
   - Rozwaz ograniczenia wydajnosci i bezpieczenstwa (np. RLS, rate limits)

Przed dostarczeniem ostatecznego planu, pracuj wewnatrz tagow <api_analysis> w swoim bloku myslenia, aby rozbic proces myslenia i upewnic sie, ze uwzgledniles wszystkie niezbedne aspekty. W tej sekcji:

1. Wymien glowne encje ze schematu bazy danych. Ponumeruj kazda encje i zacytuj odpowiednia czesc schematu.
2. Wymien kluczowe funkcje logiki biznesowej z PRD. Ponumeruj kazda funkcje i zacytuj odpowiednia czesc PRD.
3. Zmapuj funkcje z PRD do operacji SupabaseClient. Dla kazdej funkcji rozwaz co najmniej dwa mozliwe projekty zapytan (np. prosty select vs RPC) i wyjasnij wybor.
4. Rozwaz i wymien wymagania dotyczace bezpieczenstwa i wydajnosci. Dla kazdego wymagania zacytuj czesc dokumentow wejsciowych, ktora je wspiera.
5. Wyraznie mapuj logike biznesowa z PRD na operacje SupabaseClient.
6. Uwzglednij warunki walidacji ze schematu bazy danych w planie.

Ostateczny plan powinien byc sformatowany w markdown i zawierac nastepujace sekcje:

```markdown
# SupabaseClient Implementation Plan

## 1. Entities and Models
- List each main entity and its corresponding DB table
- Map to DTO/domain models

## 2. SupabaseClient Operations
For each entity/flow provide:
- Operation type (select/insert/update/delete/RPC)
- Supabase table/function name
- Query filters, pagination, sorting
- Input DTO structure (if any)
- Output DTO structure
- Success and error handling notes

## 3. Authentication and Authorization
- Supabase Auth flows used
- RLS policies assumptions and how they affect queries

## 4. Validation and Business Logic
- Validation rules per entity
- Where business logic lives (client vs RPC vs database constraints)
```

Upewnij sie, ze plan jest kompletny, dobrze ustrukturyzowany i odnosi sie do wszystkich aspektow materialow wejsciowych. Jesli musisz przyjac jakies zalozenia z powodu niejasnych informacji wejsciowych, okresl je wyraznie w analizie.

Koncowy wynik powinien skladac sie wylacznie z planu w formacie markdown w jezyku angielskim, ktory zapiszesz w .ai/api-plan.md i nie powinien powielac ani powtarzac zadnej pracy wykonanej w bloku myslenia.
