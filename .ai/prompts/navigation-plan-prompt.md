Jestes doswiadczonym architektem UX i nawigacji w aplikacjach mobilnych. Twoim zadaniem jest przygotowanie kompletnego planu nawigacji dla aplikacji opisanej w materialach kontekstowych.

<prd>
@.ai/prd.md
</prd>

<project_highlevel>
@.ai/project-highlevel.md
</project_highlevel>

<project_details>
@.ai/project-details.md
</project_details>

<tech_stack>
@.ai/tech-stack.md
</tech_stack>

<api_plan>
@.ai/api-plan.md
</api_plan>

<db_plan>
@.ai/db-plan.md
</db_plan>

<db_details>
@.ai/db-details.md
</db_details>

Masz przygotowac plan nawigacji dla aplikacji mobilnej bazujac wylacznie na powyzszych materialach. To dokumenty kontekstowe maja determinowac zakres funkcjonalny i zawartosc ekranow. Twoj plan ma byc minimalny, spojny i kompletny dla MVP.

Wymagania (opis procesu):
1. Przeanalizuj dokumenty kontekstowe i wyodrebnij kluczowe potrzeby uzytkownika oraz funkcje produktu.
2. Zidentyfikuj wszystkie przeplywy uzytkownika wynikajace z dokumentow, wraz z ich wejsciami/wyjsciami.
3. Zdefiniuj minimalny zestaw ekranow, ktory pokrywa wszystkie przeplywy MVP.
4. Okresl reguly nawigacji i guardy (np. brak sesji, brak polaczenia, brak danych).
5. Wskaz miejsca wymagajace potwierdzen, dialogow lub one-off efektow.
6. Opisz trasy i argumenty w sposob spójny z architektura aplikacji.
7. Wymien ryzyka i otwarte kwestie wynikajace bezposrednio z dokumentow.
8. Stosuj wzorce nawigacyjne charakterystyczne dla danej platformy (np. Android).

Przed dostarczeniem ostatecznego planu, pracuj wewnatrz tagow <navigation_analysis> w swoim bloku myslenia, aby uporzadkowac decyzje i upewnic sie, ze nie pominales niczego istotnego. W tej sekcji:
1. Zmapuj wymagania funkcjonalne na przeplywy uzytkownika i ekrany.
2. Uzasadnij minimalny zestaw ekranow MVP cytatami z dokumentow.
3. Okresl entry points i warunki przejsc wynikajace z ograniczen i zaleznosci.
4. Wypisz zasady obslugi bledow i sytuacji brzegowych.
5. Wskaz otwarte kwestie i ryzyka wplywajace na nawigacje.

Ostateczny plan powinien byc sformatowany w markdown i zawierac nastepujace sekcje:

```markdown
# Navigation Plan

## 1. Assumptions and Constraints
- ...

## 2. Screen List
- Screen name: purpose, key actions, dependencies

## 3. Navigation Map
- Flow A: step -> step -> step (with conditions)

## 4. Routes and Arguments
- route_name(argument1, argument2) + source/target

## 5. Entry Points
- start destination + guards and fallback

## 6. One-off Effects and Dialogs
- toasts, dialogs, navigation effects and triggers

## 7. Risks / Open Questions
- ...
```

Koncowy wynik powinien skladac sie wylacznie z planu w formacie markdown w jezyku angielskim.
