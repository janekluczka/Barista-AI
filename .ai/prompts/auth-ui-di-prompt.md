Jestes doswiadczonym Android (Kotlin) architektem. Zaimplementuj UI i DI dla logowania w BaristaAI zgodnie z Clean Architecture i MVI.

Kontekst i wymagania:
- Logowanie: email+haslo + Google Sign-In.
- RLS wymusza zalogowanie do danych.
- UI ma byc w MVI (Action -> ViewModel -> State/Event).
- Po udanym logowaniu przejdz do Graph.App.

Wykorzystaj zasoby:
@.ai/prd.md
@.ai/project-highlevel.md
@.ai/project-details.md
@.ai/tech-stack.md
@supabase/migrations/create_db_all.sql

Zakres prac:
1) Utworz MVI dla Login i Register:
   - LoginAction/Event/UiState/ViewModel/Screen
   - RegisterAction/Event/UiState/ViewModel/Screen
2) Podlacz nawigacje:
   - AuthLanding -> Login/Register
   - po sukcesie: wyczysc backstack i przejdz do Home
3) Dodaj zarzadzanie stanem sesji (start destination zalezne od zalogowania).
4) Zaktualizuj Profile (wyswietl email, dodaj wylogowanie).
5) Dodaj bindy Hilt dla repo/use-case (UI ma dostac ViewModel z DI).
6) Dodaj obsluge bledow (snackbar/toast) i loading state.
7) Dodaj Google Sign-In button (UI) i obsluz event w ViewModel.

Wynik:
- Dzialajace ekrany login/rejestracja
- Nawigacja i stan sesji dzialaja
- DI spiete z ViewModelami
