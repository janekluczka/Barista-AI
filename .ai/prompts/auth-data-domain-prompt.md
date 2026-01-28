Jestes doswiadczonym Android (Kotlin) architektem. Zaimplementuj warstwe Data+Domain dla logowania w BaristaAI zgodnie z Clean Architecture i MVI. Uzyj Supabase Auth.

Kontekst i wymagania:
- Logowanie i rejestracja email+haslo oraz Google Sign-In (PRD).
- RLS w bazie wymaga auth.uid() dla danych uzytkownika.
- Stosuj RepositoryResult/RepositoryError jak w supabase-repository-plan.
- Utrzymaj strukture pakietow projektu.

Wykorzystaj zasoby:
@.ai/prd.md
@.ai/project-highlevel.md
@.ai/project-details.md
@.ai/tech-stack.md
@.ai/db-details.md
@.ai/db-plan.md
@supabase/migrations/create_db_all.sql
@.ai/supabase-repository-plan.md
@.ai/prompts/supabase-repository-prompt.md
@.ai/prompts/supabase-repository-implementation-prompt.md

Zakres prac:
1) Wlacz supabase-auth-kt (gradle/libs) i plugin Auth w SupabaseModule.
2) Dodaj domenowy model AuthUser.
3) Dodaj AuthRepository (signIn, signUp, signOut, getCurrentUser, observeAuthState).
4) Dodaj use case'y: SignIn/SignUp/SignOut/GetCurrentUser/ObserveAuthState.
5) Dodaj implementacje AuthRepositoryImpl (SupabaseClient.auth, mapowanie bledow).
6) Zaktualizuj SupabaseDataSource (currentUserId, signOut).
7) Dodaj bezpieczne mapowanie bledow i walidacje wejsciowe.

Wynik:
- Kod gotowy do uzycia przez UI
- Bez zmian UI
- Bez zmiany struktury pakietow
