# SupabaseClient Implementation Plan

## 1. Entities and Models
- `brew_methods` -> `BrewMethodDto` (id, name, slug, createdAt)
- `generation_requests` -> `GenerationRequestDto` (id, userId, brewMethodId, coffeeAmount, canAdjustTemperature, userComment, createdAt)
- `recipes` -> `RecipeDto` (id, userId, generationRequestId?, brewMethodId, coffeeAmount, waterAmount, ratioCoffee, ratioWater, temperature, assistantTip?, status, createdAt, updatedAt)
- `recipe_action_logs` -> `RecipeActionLogDto` (id, userId, recipeId, generationRequestId?, action, createdAt)
- Enums:
  - `recipe_status_enum` -> `RecipeStatus` (draft, saved, edited, rejected, deleted)
  - `recipe_action_enum` -> `RecipeAction` (accepted, edited, rejected)

## 2. SupabaseClient Operations
For each flow provide operation options, recommended approach, and DTOs.

### 2.1 Brew Methods (for selection lists)
- Operation type: `select`
- Table: `brew_methods`
- Query: `select("*")` with ordering by `name` or `created_at`
- Input DTO: none
- Output DTO: `BrewMethodDto[]`
- Success/error: cache in memory; handle network errors with retry/backoff

### 2.2 Create Generation Request (US-001, US-002, US-014)
- Operation type: `insert`
- Table: `generation_requests`
- Query: `insert(CreateGenerationRequestCommand)` returning inserted row
- Input DTO: `CreateGenerationRequestCommand` (brewMethodId, coffeeAmount, canAdjustTemperature, userComment?)
- Output DTO: `GenerationRequestDto`
- Notes:
  - Validate required fields and numeric constraints client-side before insert
  - Ensure `user_id` set to `auth.uid()` via RLS (no client write of user_id)

### 2.3 Generate Recipes (AI flow)
There is no direct DB operation to generate recipes; generation happens via AI (Openrouter) and then drafts are optionally persisted.

Option A (recommended): Insert AI drafts into `recipes` with status `draft`
- Operation type: `insert` (multiple)
- Table: `recipes`
- Query: `insert(CreateRecipeCommand[])` returning rows
- Input DTO: `CreateRecipeCommand` (generationRequestId, brewMethodId, coffeeAmount, waterAmount, ratioCoffee, ratioWater, temperature, assistantTip?, status="draft")
- Output DTO: `RecipeDto[]`
- Notes:
  - Keep max 3 recipes per request in client logic
  - For canAdjustTemperature=false, set temperature to 100

Option B: Keep drafts only in memory and create only on accept
- No DB operation for drafts
- Only `insert` on accept/edited accept
- Lower storage but loses cross-session drafts

### 2.4 Accept Recipe (US-004, US-005)
Option A (recommended): Update existing draft to saved + log action
- Operation type: `update` + `insert`
- Table: `recipes`, `recipe_action_logs`
- Query: `update` by `id` set `status="saved"`, update fields if edited
- Input DTOs:
  - `UpdateRecipeCommand` (id, waterAmount, ratioCoffee, ratioWater, temperature, assistantTip?, status)
  - `CreateRecipeActionLogCommand` (recipeId, generationRequestId?, action="accepted" or "edited")
- Output DTO: `RecipeDto`, `RecipeActionLogDto`
- Notes:
  - Wrap in client-side transaction-like flow; if log insert fails, surface error and retry

Option B: Insert new saved recipe and log action
- Operation type: `insert` + `insert`
- Table: `recipes`, `recipe_action_logs`
- Query: `insert` for saved recipe; `insert` log
- Use if drafts are not stored

### 2.5 Reject Recipe (US-006)
Option A (recommended): Update draft to rejected + log action
- Operation type: `update` + `insert`
- Table: `recipes`, `recipe_action_logs`
- Query: `update` by `id` set `status="rejected"`
- Input DTOs:
  - `UpdateRecipeCommand` (id, status="rejected")
  - `CreateRecipeActionLogCommand` (recipeId, generationRequestId?, action="rejected")
- Output DTO: `RecipeDto`, `RecipeActionLogDto`

Option B: Delete draft and only log action
- Operation type: `delete` + `insert`
- Removes draft row for lighter storage

### 2.6 Manual Recipe Create (US-007)
- Operation type: `insert`
- Table: `recipes`
- Query: `insert(CreateRecipeCommand)` with `generationRequestId=null`, `status="saved"`
- Input DTO: `CreateRecipeCommand`
- Output DTO: `RecipeDto`
- Notes:
  - Run similar-recipe detection before insert (see 2.10)

### 2.7 List Saved Recipes (US-008)
- Operation type: `select`
- Table: `recipes`
- Query: `select("*")` where `status="saved"` order by `created_at` desc
- Pagination: keyset or limit/offset (limit 20, cursor by `created_at`)
- Output DTO: `RecipeDto[]`
- Notes: exclude drafts/rejected/deleted

### 2.8 Read Single Recipe (US-008)
- Operation type: `select`
- Table: `recipes`
- Query: `select("*")` where `id=...` and `status="saved"`
- Output DTO: `RecipeDto`

### 2.9 Update Saved Recipe (US-009)
Option A (recommended): `update` on `recipes`
- Operation type: `update`
- Table: `recipes`
- Query: `update(UpdateRecipeCommand)` where `id=...`
- Input DTO: `UpdateRecipeCommand`
- Output DTO: `RecipeDto`

Option B: RPC for validation + update
- Operation type: `RPC`
- Function: `update_recipe_with_validation`
- Use if server-side validation needs to be centralized

### 2.10 Delete Saved Recipe (US-010)
Option A (recommended): soft delete via status
- Operation type: `update`
- Table: `recipes`
- Query: set `status="deleted"`
- Keeps history for analytics/logs

Option B: hard delete
- Operation type: `delete`
- Table: `recipes`
- Use if storage needs to be minimized

### 2.11 Similar Recipe Detection (US-011)
Option A (recommended): `select` with composite filters
- Operation type: `select`
- Table: `recipes`
- Query: filter by `brew_method_id`, `coffee_amount`, `ratio_coffee`, `ratio_water`, `temperature`, `status="saved"`
- Pagination: limit small (e.g., 5)
- Output DTO: `RecipeDto[]`

Option B: RPC for similarity scoring
- Operation type: `RPC`
- Function: `find_similar_recipes` (if complex scoring or tolerance range needed)

## 3. Authentication and Authorization
- Use Supabase Auth for:
  - Email/password sign-up and sign-in
  - Google Sign-In
  - Sign-out (clear session and local cache)
- RLS policies:
  - `generation_requests`, `recipes`, `recipe_action_logs` must be restricted by `user_id = auth.uid()`
  - Ensure all client queries rely on RLS (no explicit `user_id` filters required but safe to include)
- Anonymous access:
  - `brew_methods` can be public `select` if desired (no `user_id` field)

## 4. Validation and Business Logic
- DB constraints to respect on client:
  - `coffee_amount`, `water_amount` >= 0
  - `ratio_coffee`, `ratio_water` >= 1
  - `temperature` between 0 and 100
- Business logic in client:
  - Validate required fields before generation and before insert
  - Enforce max 3 recipes per generation request
  - If `can_adjust_temperature=false`, set temperature to 100
  - Ensure alternatives differ from base in at least one field
  - Block generation/save when offline
- Business logic in DB / optional RPC:
  - Similar recipe detection can move to RPC if tolerance or ranking needed
- Assumptions:
  - Draft retention is handled either by app cleanup or a scheduled DB job if required
