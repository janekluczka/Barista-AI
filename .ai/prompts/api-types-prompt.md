Jesteś wykwalifikowanym programistą Android (Kotlin), którego zadaniem jest stworzenie biblioteki modeli DTO (Data Transfer Object) i Command Model dla aplikacji. Masz pracować w kontekście stacku: Kotlin, Jetpack Compose, Material 3, Hilt, Ktor oraz Supabase (PostgreSQL + RLS). Twoim zadaniem jest przeanalizowanie definicji modelu bazy danych oraz historyjek użytkowników (PRD), a następnie utworzenie odpowiednich modeli DTO, które dokładnie reprezentują struktury danych wymagane przez aplikację i zapytania do Supabase, zachowując jednocześnie bezpośrednie powiązanie z podstawowymi modelami bazy danych. Modele muszą być zgodne z użyciem `SupabaseClient` w aplikacji Androidowej.

Najpierw dokładnie przejrzyj następujące dane wejściowe:

1. Modele bazy danych:
<database_models>
@supabase/migrations/create_db_all.sql
@.ai/db-plan.md
</database_models>

2. Historyjki użytkowników (PRD):
<prd>
@.ai/prd.md
</prd>

3. Tech stack:
<tech_stack>
@.ai/tech-stack.md
</tech_stack>

Twoim zadaniem jest utworzenie definicji Kotlin dla DTO i Command Modeli wynikających z historyjek użytkowników, upewniając się, że pochodzą one z modeli bazy danych i są zgodne z `SupabaseClient`. Wykonaj następujące kroki:

1. Przeanalizuj modele bazy danych i historyjki użytkowników.
2. Zidentyfikuj wszystkie struktury danych potrzebne do realizacji historyjek (np. generowanie przepisów, zapis/edycja/usuwanie, logi akcji, listy).
3. Utwórz DTO i Command Modele bazujące na encjach bazy danych (Supabase/PostgreSQL).
4. Zapewnij zgodność między DTO/Command Modelami a wymaganiami aplikacji oraz strukturami Supabase.
5. Stosuj odpowiednie funkcje języka Kotlin i narzędzia serializacji (np. `kotlinx.serialization`) w celu mapowania pól, opcjonalności i enumów.
6. Wykonaj końcowe sprawdzenie, aby upewnić się, że wszystkie wymagane DTO są uwzględnione i poprawnie połączone z encjami.

Wymagania:
- Każdy DTO i Command Model musi bezpośrednio odnosić się do jednej lub więcej encji bazy danych.
- Uwzględnij typy pól zgodne z PostgreSQL/Supabase (np. UUID, timestamptz, numeric).
- Jeśli konieczne są transformacje typów, opisz je krótkim komentarzem przy polu.
- Obsłuż enumy jako `enum class`.
- Modele powinny być gotowe do użycia z `SupabaseClient` (PostgREST) w aplikacji Androidowej.
- Dodaj krótkie komentarze tylko tam, gdzie mapowanie typów lub relacje nie są oczywiste.

Końcowy wynik powinien składać się wyłącznie z definicji Kotlin (np. `data class`, `enum class`) i ma zostać zapisany w pliku `app/src/main/java/com/luczka/baristaai/types/Types.kt`. Nie dołączaj żadnych dodatkowych wyjaśnień poza komentarzami w kodzie.
