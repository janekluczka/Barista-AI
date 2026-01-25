-- policy maintenance: remove user-owned rls policies
-- purpose: drop access policies for user-owned tables
-- affected: public.generation_requests, public.recipes, public.recipe_action_logs
-- notes: rls stays enabled; this only removes policies for anon/authenticated

-- generation_requests: remove user-owned policies
-- destructive: dropping policies revokes access until restored
drop policy if exists "generation_requests_select_anon" on public.generation_requests;
drop policy if exists "generation_requests_select_authenticated" on public.generation_requests;
drop policy if exists "generation_requests_insert_anon" on public.generation_requests;
drop policy if exists "generation_requests_insert_authenticated" on public.generation_requests;
drop policy if exists "generation_requests_update_anon" on public.generation_requests;
drop policy if exists "generation_requests_update_authenticated" on public.generation_requests;
drop policy if exists "generation_requests_delete_anon" on public.generation_requests;
drop policy if exists "generation_requests_delete_authenticated" on public.generation_requests;

-- recipes: remove user-owned policies
-- destructive: dropping policies revokes access until restored
drop policy if exists "recipes_select_anon" on public.recipes;
drop policy if exists "recipes_select_authenticated" on public.recipes;
drop policy if exists "recipes_insert_anon" on public.recipes;
drop policy if exists "recipes_insert_authenticated" on public.recipes;
drop policy if exists "recipes_update_anon" on public.recipes;
drop policy if exists "recipes_update_authenticated" on public.recipes;
drop policy if exists "recipes_delete_anon" on public.recipes;
drop policy if exists "recipes_delete_authenticated" on public.recipes;

-- recipe_action_logs: remove user-owned policies
-- destructive: dropping policies revokes access until restored
drop policy if exists "recipe_action_logs_select_anon" on public.recipe_action_logs;
drop policy if exists "recipe_action_logs_select_authenticated" on public.recipe_action_logs;
drop policy if exists "recipe_action_logs_insert_anon" on public.recipe_action_logs;
drop policy if exists "recipe_action_logs_insert_authenticated" on public.recipe_action_logs;
drop policy if exists "recipe_action_logs_update_anon" on public.recipe_action_logs;
drop policy if exists "recipe_action_logs_update_authenticated" on public.recipe_action_logs;
drop policy if exists "recipe_action_logs_delete_anon" on public.recipe_action_logs;
drop policy if exists "recipe_action_logs_delete_authenticated" on public.recipe_action_logs;
