<conversation_summary>
<decisions>
1. Przechowujemy żądania generowania i powiązania z przepisami; generowanie ma osobne rekordy.
2. Jest 1 tabela przepisów `recipes` obejmująca szkice i zapisane wersje.
3. Przepis ma status (enum) z 5 wartościami, w tym `draft`.
4. Metody parzenia w osobnej tabeli z `id` + `slug`.
6. Typy danych: kawa i woda jako liczby z 1 miejscem po przecinku; temperatura `int` 0..100; proporcje jako `int` (kawa/woda).
7. Ograniczenia: kawa i woda >= 0, proporcje od 1 wzwyż.
8. RLS: dostęp użytkownika tylko do swoich danych (zgodnie z rekomendacją).
9. Tylko aktualny stan przepisu (bez wersjonowania), z `created_at` i `updated_at`.
10. Przepisy są niezależne (brak unikalności/nazw).
11. Dodatkowe pola: komentarz użytkownika w żądaniu, porada asystenta w przepisie.
12. Algorytm powtórek ignorujemy; wykrywanie podobnych przepisów na bazie metody, ilości kawy, ratio i temperatury.
13. Relacja 1:N `generation_request` -> `recipes` (do 3 szkiców).
</decisions>

<matched_recommendations>
1. Oddziel `generation_requests` od `recipes`, aby łatwo logować decyzje i analizować jakość AI.
2. Statusy trzymać w `recipes` (enum 5 wartości).
3. Osobna tabela metod parzenia z `id` i unikalnym `slug`.
4. RLS na `user_id` dla wszystkich tabel użytkownikowych; logi tylko dla właściciela.
5. `numeric(6,1)` dla ilości kawy i wody; `int` dla temperatury i proporcji.
6. CHECK: `coffee_amount >= 0`, `water_amount >= 0`, `ratio_coffee >= 1`, `ratio_water >= 1`, `temperature between 0 and 100`.
7. Indeksy: `user_id` + `created_at` dla list, FK dla relacji, indeks po polach podobieństwa (metoda, ilość kawy, ratio, temperatura).
</matched_recommendations>

<database_planning_summary>
a. Główne wymagania:
- Zapis żądań generowania i do 3 szkiców AI w `recipes`.
- Statusy jako enum (5 wartości) w `recipes`.
- Dodatkowe pola: komentarz użytkownika w żądaniu i porada asystenta w przepisie.
- Brak wersjonowania; tylko `created_at`, `updated_at`.

b. Kluczowe encje i relacje:
- `users` (Supabase auth) -> 1:N `generation_requests`.
- `generation_requests` -> 1:N `recipes` (max 3 szkice).
- `users` -> 1:N `recipes`.
- `brew_methods` -> 1:N `generation_requests`, `recipes`.

c. Bezpieczeństwo i skalowalność:
- RLS: użytkownik ma dostęp tylko do swoich rekordów.
- Indeksy po `user_id`, `created_at`, FK i polach podobieństwa (metoda, ilość kawy, ratio, temperatura).
- Brak retencji szkiców; partycjonowanie niepotrzebne w MVP.

d. Nierozwiązane kwestie:
- Brak.
</database_planning_summary>

<unresolved_issues>
Brak.
</unresolved_issues>
</conversation_summary>
