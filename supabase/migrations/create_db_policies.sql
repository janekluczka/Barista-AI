-- create db: rls enablement and policies for core tables
-- affected: public.brew_methods, public.generation_requests, public.recipes, public.recipe_action_logs

-- enable rls even for public tables (required by project standards)
alter table public.brew_methods enable row level security;
alter table public.generation_requests enable row level security;
alter table public.recipes enable row level security;
alter table public.recipe_action_logs enable row level security;

-- public read access for brew methods
create policy "brew_methods_select_anon"
  on public.brew_methods
  for select
  to anon
  using (true);

create policy "brew_methods_select_authenticated"
  on public.brew_methods
  for select
  to authenticated
  using (true);

-- user-owned access policies for generation requests
create policy "generation_requests_select_anon"
  on public.generation_requests
  for select
  to anon
  using (user_id = auth.uid());

create policy "generation_requests_select_authenticated"
  on public.generation_requests
  for select
  to authenticated
  using (user_id = auth.uid());

create policy "generation_requests_insert_anon"
  on public.generation_requests
  for insert
  to anon
  with check (user_id = auth.uid());

create policy "generation_requests_insert_authenticated"
  on public.generation_requests
  for insert
  to authenticated
  with check (user_id = auth.uid());

create policy "generation_requests_update_anon"
  on public.generation_requests
  for update
  to anon
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

create policy "generation_requests_update_authenticated"
  on public.generation_requests
  for update
  to authenticated
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

create policy "generation_requests_delete_anon"
  on public.generation_requests
  for delete
  to anon
  using (user_id = auth.uid());

create policy "generation_requests_delete_authenticated"
  on public.generation_requests
  for delete
  to authenticated
  using (user_id = auth.uid());

-- user-owned access policies for recipes
create policy "recipes_select_anon"
  on public.recipes
  for select
  to anon
  using (user_id = auth.uid());

create policy "recipes_select_authenticated"
  on public.recipes
  for select
  to authenticated
  using (user_id = auth.uid());

create policy "recipes_insert_anon"
  on public.recipes
  for insert
  to anon
  with check (user_id = auth.uid());

create policy "recipes_insert_authenticated"
  on public.recipes
  for insert
  to authenticated
  with check (user_id = auth.uid());

create policy "recipes_update_anon"
  on public.recipes
  for update
  to anon
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

create policy "recipes_update_authenticated"
  on public.recipes
  for update
  to authenticated
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

create policy "recipes_delete_anon"
  on public.recipes
  for delete
  to anon
  using (user_id = auth.uid());

create policy "recipes_delete_authenticated"
  on public.recipes
  for delete
  to authenticated
  using (user_id = auth.uid());

-- user-owned access policies for recipe action logs
create policy "recipe_action_logs_select_anon"
  on public.recipe_action_logs
  for select
  to anon
  using (user_id = auth.uid());

create policy "recipe_action_logs_select_authenticated"
  on public.recipe_action_logs
  for select
  to authenticated
  using (user_id = auth.uid());

create policy "recipe_action_logs_insert_anon"
  on public.recipe_action_logs
  for insert
  to anon
  with check (user_id = auth.uid());

create policy "recipe_action_logs_insert_authenticated"
  on public.recipe_action_logs
  for insert
  to authenticated
  with check (user_id = auth.uid());

create policy "recipe_action_logs_update_anon"
  on public.recipe_action_logs
  for update
  to anon
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

create policy "recipe_action_logs_update_authenticated"
  on public.recipe_action_logs
  for update
  to authenticated
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

create policy "recipe_action_logs_delete_anon"
  on public.recipe_action_logs
  for delete
  to anon
  using (user_id = auth.uid());

create policy "recipe_action_logs_delete_authenticated"
  on public.recipe_action_logs
  for delete
  to authenticated
  using (user_id = auth.uid());
