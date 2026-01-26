# Navigation Plan

## 1. Assumptions and Constraints
- MVP is Android-only and online-only; no offline flow or onboarding.
- Auth is required for any user data (recipes, generation requests, logs).
- Generation creates up to 3 AI draft recipes, shown as cards with accept/edit/reject actions.
- Manual recipe creation uses the same fields as edit.
- Similar-recipe detection runs before saving a manual recipe (and before saving an edited draft, if treated as a new save).
- Draft retention is ambiguous in docs; treat drafts as transient unless persisted.

## 2. Screen List
- AuthLanding: choose login or register; actions: go to Login, go to Register; dependencies: none
- Login: email/password and Google Sign-In; actions: sign in, switch to Register; dependencies: Supabase Auth
- Register: email/password; actions: sign up, switch to Login; dependencies: Supabase Auth
- Home (Recipes): saved recipes list with filters at top; actions: open recipe, apply filters, open profile, FAB to Generate or Manual; dependencies: saved recipes query
- GeneratedRecipes: cards for up to 3 drafts; actions: accept, reject, edit; dependencies: generation request + draft recipes
- EditRecipe: edit all recipe fields; actions: save (accept), cancel; dependencies: recipe draft or saved recipe
- GenerateRecipe: input method, coffee amount, can-adjust-temperature, optional comment; actions: generate, back; dependencies: brew methods list, online
- RecipeDetail: view single saved recipe; actions: edit, delete; dependencies: recipe by id
- Profile: show account info; actions: logout; dependencies: auth session

## 3. Navigation Map
- Auth flow: AuthLanding -> Login -> (success) Home; AuthLanding -> Register -> (success) Home
- Generate flow: Home -> GenerateRecipe -> GeneratedRecipes (after successful generation) -> EditRecipe (optional) -> Home (after save)
- Accept draft: GeneratedRecipes -> Home (on accept) with success message
- Reject draft: GeneratedRecipes -> stay, remove card, show toast
- Manual create: Home -> EditRecipe (mode=manual) -> (confirm similar) -> Home
- Saved list flow: Home -> RecipeDetail -> EditRecipe -> Home
- Delete: RecipeDetail or Home -> confirm dialog -> Home
- Logout: Profile -> AuthLanding

## 4. Routes and Arguments
- auth_landing (entry, unauthenticated)
- login (source: AuthLanding)
- register (source: AuthLanding)
- home (entry, authenticated)
- generate_recipe (source: Home)
- generated_recipes(requestId: String) (source: GenerateRecipe)
- edit_recipe(mode: String, recipeId: String?, requestId: String?) where mode = draft | saved | manual
- recipe_detail(recipeId: String) (source: Home)
- profile (source: Home)

## 5. Entry Points
- Start destination: auth_landing if no session; else home
- Guards:
- If offline, block generation and saving; show offline dialog and stay on current screen
- If a recipe id is missing or invalid, fall back to home with error toast
- If session expires, redirect to auth_landing and clear back stack

## 6. One-off Effects and Dialogs
- Offline dialog: when user attempts generation or save while offline
- Validation errors: inline field errors for missing/invalid input
- Similar recipe dialog: before saving manual or edited recipe; options: continue save / cancel
- Delete confirmation dialog: for saved recipe deletion
- Discard changes dialog: when leaving EditRecipe with unsaved changes
- Toasts/snackbars: save success, reject logged, deletion success, auth errors

## 7. Risks / Open Questions
- Draft retention: PRD mentions 7 days, DB notes say no retention; affects whether GeneratedRecipes can be reopened
- No password reset/email verification decision; impacts Auth flow completeness
- No explicit rule for alternative recipe difference; affects user expectations on GeneratedRecipes
- Similar-recipe detection tolerance/ranking is unspecified; impacts dialog usefulness
- Spaced repetition is referenced but out of MVP; no navigation for it yet
