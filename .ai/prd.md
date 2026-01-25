# Dokument wymagań produktu (PRD) - BaristaAI
## 1. Przegląd produktu
BaristaAI to aplikacja Android (min. API 30), która pomaga początkującym użytkownikom przygotowywać kawę metodami alternatywnymi (np: V60, Chemex, Aeropress, Moka, itp.). Kluczową wartością jest szybkie generowanie przepisów na podstawie prostych danych wejściowych i ich zapis do późniejszego wykorzystania. MVP jest minimalne, nastawione na szybkie wdrożenie i działa wyłącznie online. Aplikacja oferuje generowanie do 3 przepisów (bazowy i dwa alternatywne), manualne tworzenie przepisów, ich edycję i usuwanie oraz prosty system kont użytkowników.

## 2. Problem użytkownika
Parzenie kawy alternatywnej jest trudne, szczególnie dla początkujących. Wysoki próg wejścia i brak pewności co do parametrów parzenia zniechęca do eksperymentowania z metodami alternatywnymi. Użytkownik potrzebuje prostego sposobu na uzyskanie poprawnych, zrozumiałych przepisów oraz możliwości ich zapisu i powtórek bez konieczności zgłębiania zaawansowanej wiedzy baristycznej.

## 3. Wymagania funkcjonalne
1. Generowanie do 3 przepisów na podstawie danych wejściowych: metoda, waga ziaren, informacja o możliwości regulacji temperatury.
2. Generowane przepisy obejmują co najmniej: ilość kawy, ilość wody, proporcję, temperaturę.
3. Temperatura w przepisie jest rekomendowana przez model; gdy użytkownik nie może regulować temperatury, domyślnie 100°C.
4. Reguła różnicowania trzech przepisów w MVP:
   - Model generuje jeden przepis bazowy i dwie alternatywy według własnej logiki.
   - Alternatywy muszą być wyraźnie różne od bazowego co najmniej w jednym z pól: ilość kawy, ilość wody, proporcja, temperatura.
5. Prezentacja przepisów w formie kart, z akcjami: zaakceptuj (zapisz), odrzuć, edytuj.
6. Akceptacja przepisu oznacza zapis przepisu lub zapis po edycji.
7. Zapis przepisu tworzy log akceptacji w osobnej tabeli do oceny jakości modeli.
8. Odrzucenie przepisu tworzy log w osobnej tabeli do oceny jakości modeli.
9. Manualne wprowadzanie przepisu z tym samym zestawem pól co w edycji.
10. Przeglądanie listy zapisanych przepisów z możliwością edycji i usuwania.
11. W MVP brak algorytmu powtórek; zamiast tego wykrywanie podobnych przepisów przy dodawaniu.
12. Prosty system kont użytkowników: rejestracja i logowanie przez email i hasło oraz Google Sign-In.
13. Dostęp do danych wyłącznie online, bez trybu offline.
14. Żądanie generowania może zawierać komentarz użytkownika.
15. Przepis może zawierać poradę asystenta.
16. Zapisany przepis ma status; szkic AI nie ma statusu.
17. Retencja szkiców AI wynosi 7 dni.

## 4. Granice produktu
1. Brak algorytmu powtórek w MVP; brak planowania powtórek.
2. Brak zapisywania i powiązania ziaren kawy z przepisem.
3. Brak współdzielenia zestawów przepisów między użytkownikami.
4. Brak aplikacji webowej i iOS w MVP.
5. Brak trybu offline.
6. Brak onboardingu.
7. Brak decyzji o resetach haseł i weryfikacji email w MVP.
8. Brak predefiniowanej reguły różnicowania alternatyw poza decyzją modelu w MVP.

## 5. Historyjki użytkowników
- ID: US-001
  Tytuł: Wprowadzenie danych do generowania przepisu
  Opis: Jako początkujący użytkownik chcę wprowadzić metodę, wagę ziaren i informację o możliwości regulacji temperatury, aby system mógł wygenerować przepis.
  Kryteria akceptacji:
  - Formularz przyjmuje metodę, wagę ziaren i informację o regulacji temperatury.
  - Nie można uruchomić generowania bez kompletu wymaganych danych.

- ID: US-002
  Tytuł: Generowanie trzech propozycji przepisu
  Opis: Jako użytkownik chcę otrzymać do 3 przepisów (bazowy i dwa alternatywne), aby porównać propozycje.
  Kryteria akceptacji:
  - System generuje maksymalnie 3 przepisy na jedno żądanie.
  - Każdy przepis zawiera kawę, wodę, proporcję i temperaturę.
  - Dwie alternatywy różnią się od bazowego co najmniej jednym parametrem.

- ID: US-003
  Tytuł: Prezentacja przepisów w kartach
  Opis: Jako użytkownik chcę przeglądać wygenerowane przepisy na kartach, aby łatwo ocenić propozycje.
  Kryteria akceptacji:
  - Każdy przepis jest pokazany na osobnej karcie.
  - Na karcie widoczne są wszystkie parametry przepisu.

- ID: US-004
  Tytuł: Akceptacja przepisu bez edycji
  Opis: Jako użytkownik chcę zaakceptować przepis jednym kliknięciem, aby szybko go zapisać.
  Kryteria akceptacji:
  - Akcja akceptacji zapisuje przepis do kolekcji użytkownika.
  - Zapisany przepis jest dostępny na liście przepisów.
  - Akceptacja tworzy log zapisu do oceny jakości modeli.

- ID: US-005
  Tytuł: Edycja przepisu przed zapisem
  Opis: Jako użytkownik chcę edytować wszystkie parametry przepisu przed zapisem, aby dopasować go do preferencji.
  Kryteria akceptacji:
  - Użytkownik może edytować każde pole przepisu.
  - Zapis po edycji jest traktowany jako akceptacja przepisu.
  - Zapis po edycji tworzy log zapisu do oceny jakości modeli.

- ID: US-006
  Tytuł: Odrzucenie przepisu
  Opis: Jako użytkownik chcę odrzucić przepis, aby nie zapisywać go w kolekcji.
  Kryteria akceptacji:
  - Odrzucony przepis nie jest zapisywany w kolekcji.
  - Odrzucenie jest logowane w osobnej tabeli do oceny jakości modeli.

- ID: US-007
  Tytuł: Manualne dodanie przepisu
  Opis: Jako użytkownik chcę samodzielnie wprowadzić przepis, aby dodać własne receptury.
  Kryteria akceptacji:
  - Formularz manualny przyjmuje te same pola co edycja przepisu.
  - Zapisany manualnie przepis pojawia się na liście przepisów.

- ID: US-008
  Tytuł: Przeglądanie listy zapisanych przepisów
  Opis: Jako użytkownik chcę przeglądać zapisane przepisy, aby łatwo do nich wracać.
  Kryteria akceptacji:
  - Lista pokazuje wszystkie zapisane przepisy użytkownika.
  - Z listy można przejść do podglądu pojedynczego przepisu.

- ID: US-009
  Tytuł: Edycja zapisanego przepisu
  Opis: Jako użytkownik chcę edytować zapisany przepis, aby poprawić jego parametry.
  Kryteria akceptacji:
  - Edycja jest dostępna dla każdego zapisanego przepisu.
  - Zmiany są zapisane i widoczne po powrocie na listę.

- ID: US-010
  Tytuł: Usuwanie zapisanego przepisu
  Opis: Jako użytkownik chcę usunąć zapisany przepis, aby utrzymać porządek w kolekcji.
  Kryteria akceptacji:
  - Usunięty przepis znika z listy.
  - Usuniętego przepisu nie da się otworzyć z aplikacji.

- ID: US-011
  Tytuł: Wykrywanie podobnych przepisów
  Opis: Jako użytkownik chcę zobaczyć podobne przepisy przy dodawaniu, aby uniknąć duplikatów.
  Kryteria akceptacji:
  - System porównuje dodawany przepis z zapisanymi na podstawie metody, ilości kawy, ratio i temperatury.
  - Użytkownik widzi podobne przepisy i może kontynuować lub anulować zapis.

- ID: US-012
  Tytuł: Rejestracja konta przez email i hasło
  Opis: Jako użytkownik chcę założyć konto przez email i hasło, aby zapisywać przepisy.
  Kryteria akceptacji:
  - Formularz rejestracji przyjmuje email i hasło.
  - Po rejestracji użytkownik może zalogować się do aplikacji.

- ID: US-013
  Tytuł: Logowanie przez email i hasło
  Opis: Jako użytkownik chcę zalogować się emailem i hasłem, aby mieć dostęp do swoich przepisów.
  Kryteria akceptacji:
  - Formularz logowania akceptuje email i hasło.
  - Po poprawnym logowaniu użytkownik widzi swoje przepisy.

- ID: US-014
  Tytuł: Logowanie przez Google Sign-In
  Opis: Jako użytkownik chcę zalogować się przez Google, aby szybciej uzyskać dostęp do aplikacji.
  Kryteria akceptacji:
  - Dostępna jest opcja logowania przez Google Sign-In.
  - Po logowaniu użytkownik widzi swoje przepisy.

- ID: US-015
  Tytuł: Wylogowanie z aplikacji
  Opis: Jako użytkownik chcę się wylogować, aby zabezpieczyć dostęp do mojego konta.
  Kryteria akceptacji:
  - Dostępna jest opcja wylogowania.
  - Po wylogowaniu użytkownik nie ma dostępu do danych konta.

- ID: US-016
  Tytuł: Brak połączenia podczas generowania
  Opis: Jako użytkownik chcę otrzymać czytelny komunikat, gdy nie ma połączenia, aby wiedzieć, że generowanie nie zadziała.
  Kryteria akceptacji:
  - Przy braku połączenia pojawia się komunikat o niedostępności generowania.
  - Aplikacja nie zapisuje pustych lub niekompletnych przepisów.

- ID: US-017
  Tytuł: Niepełne lub niepoprawne dane wejściowe
  Opis: Jako użytkownik chcę dostać informację o błędnych danych, aby poprawnie uruchomić generowanie.
  Kryteria akceptacji:
  - System blokuje generowanie przy brakujących danych.
  - Użytkownik widzi wskazanie brakujących pól.

## 6. Metryki sukcesu
1. 75% przepisów wygenerowanych przez AI jest akceptowane przez użytkownika (akceptacja oznacza zapis lub zapis po edycji).
2. Użytkownicy tworzą 50% przepisów z wykorzystaniem AI.

Weryfikacja checklisty:
1. Każdą historię użytkownika można przetestować.
2. Kryteria akceptacji są jasne i konkretne.
3. Zakres historyjek obejmuje pełną funkcjonalność MVP.
4. Uwzględniono wymagania dotyczące uwierzytelniania i autoryzacji.
