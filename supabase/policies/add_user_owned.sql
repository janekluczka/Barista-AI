-- policy maintenance: add user-owned rls policies
-- purpose: restore access policies for user-owned tables
-- affected: public.generation_requests, public.recipes, public.recipe_action_logs
-- notes: assumes rls is enabled on these tables

-- generation_requests: user-owned access policies
-- select policies allow owners to read their rows
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

-- insert policies enforce ownership on writes
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

-- update policies restrict updates to owned rows and keep ownership consistent
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

-- delete policies allow owners to remove their rows
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

-- recipes: user-owned access policies
-- select policies allow owners to read their rows
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

-- insert policies enforce ownership on writes
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

-- update policies restrict updates to owned rows and keep ownership consistent
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

-- delete policies allow owners to remove their rows
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

-- recipe_action_logs: user-owned access policies
-- select policies allow owners to read their rows
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

-- insert policies enforce ownership on writes
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

-- update policies restrict updates to owned rows and keep ownership consistent
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

-- delete policies allow owners to remove their rows
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
