Jestes doswiadczonym Android (Kotlin) architektem i masz wdrozyc repozytoria Supabase w aplikacji. Implementacja ma byc zgodna z Clean Architecture i MVI, gotowa do uzycia z `SupabaseClient`, oraz oparta o istniejacy plan.

Najpierw dokladnie przejrzyj nastepujace dane wejsciowe:

<repository_plan>
@.ai/supabase-repository-plan.md
</repository_plan>

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
Stosuj mapowanie DTO <-> modele domenowe, walidacje przed zapisem i bezpieczne mapowanie bledow.
</requirements>

Twoje zadanie:
1. Na bazie planu utworz brakujace modele domenowe, interfejsy repository i typy bledow.
2. Zaimplementuj repozytoria w warstwie data z zapytaniami Supabase (select/insert/update/delete).
3. Dodaj warstwe abstrakcji nad `SupabaseClient` (DataSource/Gateway) zgodnie z planem.
4. Dodaj bindy Hilt dla DataSource i repozytoriow.
5. Upewnij sie, ze paginacja, sortowanie, filtry i walidacje sa zgodne z planem.
6. Zachowaj wskazana strukture pakietow.

Wynik ma byc kompletny i gotowy do uruchomienia. Nie dodawaj dodatkowych wyjasnien poza kodem i komentarzami koniecznymi do zrozumienia.
