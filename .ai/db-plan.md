1. Lista tabel z ich kolumnami, typami danych i ograniczeniami

**`brew_methods`**
- `id` uuid PK default gen_random_uuid()
- `name` text not null
- `slug` text not null unique
- `created_at` timestamptz not null default now()

**`generation_requests`**
- `id` uuid PK default gen_random_uuid()
- `user_id` uuid not null FK -> auth.users(id)
- `brew_method_id` uuid not null FK -> brew_methods(id)
- `coffee_amount` numeric(6,1) not null CHECK (coffee_amount >= 0)
- `can_adjust_temperature` boolean not null
- `user_comment` text null
- `created_at` timestamptz not null default now()

**`recipes`**
- `id` uuid PK default gen_random_uuid()
- `user_id` uuid not null FK -> auth.users(id)
- `generation_request_id` uuid null FK -> generation_requests(id)
- `brew_method_id` uuid not null FK -> brew_methods(id)
- `coffee_amount` numeric(6,1) not null CHECK (coffee_amount >= 0)
- `water_amount` numeric(6,1) not null CHECK (water_amount >= 0)
- `ratio_coffee` int not null CHECK (ratio_coffee >= 1)
- `ratio_water` int not null CHECK (ratio_water >= 1)
- `temperature` int not null CHECK (temperature between 0 and 100)
- `assistant_tip` text null
- `status` recipe_status_enum not null
- `created_at` timestamptz not null default now()
- `updated_at` timestamptz not null default now()

**`recipe_action_logs`**
- `id` uuid PK default gen_random_uuid()
- `user_id` uuid not null FK -> auth.users(id)
- `recipe_id` uuid not null FK -> recipes(id)
- `generation_request_id` uuid null FK -> generation_requests(id)
- `action` recipe_action_enum not null
- `created_at` timestamptz not null default now()

**Enumy**
- `recipe_status_enum`: `draft`, `saved`, `edited`, `rejected`, `deleted`
- `recipe_action_enum`: `accepted`, `edited`, `rejected`

2. Relacje między tabelami
- `auth.users` 1:N `generation_requests` (user_id)
- `auth.users` 1:N `recipes` (user_id)
- `brew_methods` 1:N `generation_requests`
- `brew_methods` 1:N `recipes`
- `generation_requests` 1:N `recipes` (max 3 szkice po stronie aplikacji)
- `recipes` 1:N `recipe_action_logs`

3. Indeksy
- `generation_requests`:
  - index on (`user_id`, `created_at`)
  - index on (`brew_method_id`)
- `recipes`:
  - index on (`user_id`, `created_at`)
  - index on (`generation_request_id`)
  - index on (`brew_method_id`, `coffee_amount`, `ratio_coffee`, `ratio_water`, `temperature`)
- `recipe_action_logs`:
  - index on (`user_id`, `created_at`)
  - index on (`recipe_id`, `created_at`)
  - index on (`action`)

4. Zasady PostgreSQL (jeśli dotyczy)
**RLS (dla Supabase)**
- Włącz RLS na wszystkich tabelach z `user_id`.
- Polityki przykładowe (wszystkie tabele użytkownikowe: `generation_requests`, `recipes`, `recipe_action_logs`):
  - `SELECT`: `user_id = auth.uid()`
  - `INSERT`: `user_id = auth.uid()`
  - `UPDATE`: `user_id = auth.uid()`
  - `DELETE`: `user_id = auth.uid()`

5. Dodatkowe uwagi lub wyjaśnienia dotyczące decyzji projektowych
- `recipes` przechowuje zarówno szkice AI, jak i zapisane przepisy; szkice są dostępne bez retencji.
- `generation_request_id` jest opcjonalne, aby wspierać manualne dodawanie przepisów.
- Logi akceptacji/edycji/odrzucenia są w jednej tabeli do oceny jakości modelu.
- Wykrywanie podobieństwa opiera się na indeksie złożonym metody, ilości kawy, ratio i temperatury.
- Brak retencji szkiców upraszcza model danych i pozwala na pełny dostęp do historii draftów.
