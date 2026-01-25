<conversation_summary>
<decisions>
1. Docelowe segmenty: początkujący (minimalna wiedza, bazowy sprzęt) i zaawansowany (ma sprzęt, chce się rozwijać); na ten moment proces skupiony na początkującym.
2. AI generuje maksymalnie 3 przepisy: jeden bazowy i dwa alternatywne; mogą być całkowicie różne.
3. Dane wejściowe do generowania: metoda, waga ziaren, informacja czy użytkownik może regulować temperaturę.
4. Temperatura w przepisie: rekomendowana przez model; domyślnie 100°C, zależna od możliwości regulacji.
5. Prezentacja: karty z przepisami; użytkownik może zaakceptować, odrzucić lub edytować.
6. Akceptacja przepisu: zapis lub zapis po edycji.
7. Powtórki (spaced repetition): na poziomie przepisu.
8. Karta przepisu zawiera: ilość kawy, ilość wody, proporcja, temperatura.
9. Logowanie odrzuceń: logi zapisywane w osobnej tabeli do oceny jakości modeli.
10. Edycja przepisu: wszystkie parametry (mało pól).
11. Dostępność: Android minimum API 30.
12. Konta użytkowników: email+hasło oraz Google Sign-In.
13. Dostęp do danych: tylko online (bez offline).
14. Onboarding: brak.
15. Zespół i harmonogram: solo developer, bardzo minimalne MVP w 1 tydzień.
16. Metryki: tylko dwa cele sukcesu z opisu projektu; brak dodatkowych metryk.
</decisions>

<matched_recommendations>
1. Zdefiniować regułę różnicowania trzech przepisów, aby były zrozumiałe w porównaniu (istotne przy całkowicie różnych wariantach).
2. Ustalić minimalny zestaw pól na karcie przepisu, by zachować prostotę (zdefiniowane: kawa, woda, proporcja, temperatura).
3. Dodać prosty mechanizm logowania odrzuceń, aby mierzyć jakość modeli (potwierdzone: osobna tabela logów).
4. Ograniczyć proces do jednego trybu (początkujący) na MVP, aby skrócić czas realizacji (potwierdzone).
5. Doprecyzować definicję „akceptacji” dla KPI 75% (potwierdzone: zapis lub zapis po edycji).
6. Wybrać minimalny zakres funkcji kont (email+hasło + Google) z myślą o szybkim MVP (potwierdzone).
7. Utrzymać tylko online flow i uprościć architekturę na MVP (potwierdzone).
8. Ustalić realistyczny harmonogram i zakres „must-have” na 1 tydzień (potwierdzone).
</matched_recommendations>

<prd_planning_summary>
a. Główne wymagania funkcjonalne produktu: generowanie do 3 przepisów (bazowy + 2 alternatywne), dane wejściowe: metoda, waga ziaren, możliwość regulacji temperatury; prezentacja przepisów jako karty z możliwością akceptacji, odrzucenia lub edycji; pełna edycja wszystkich parametrów; zapis przepisów i konta użytkowników (email+hasło, Google Sign-In); powtórki na poziomie przepisu; logowanie odrzuceń i jakości modelu w osobnej tabeli; Android min API 30; online-only.

b. Kluczowe historie użytkownika i ścieżki korzystania: użytkownik wprowadza metodę, wagę i informację o temperaturze → system generuje 3 przepisy → użytkownik przegląda karty, akceptuje (zapisuje), odrzuca (log), lub edytuje i zapisuje → przepis trafia do kolekcji i może być używany w systemie powtórek.

c. Ważne kryteria sukcesu i pomiar: 75% przepisów wygenerowanych przez AI jest akceptowane (mierzone jako zapis lub zapis po edycji); użytkownicy tworzą 50% przepisów z wykorzystaniem AI. Brak dodatkowych metryk.

d. Nierozwiązane kwestie: brak doprecyzowanej reguły różnicowania przepisów (co jest „alternatywą” w praktyce); brak definicji zakresu logów odrzuceń (jakie pola i skala jakości); brak określenia minimalnych wymaganych ekranów i nawigacji w aplikacji (przy braku onboardingu); brak decyzji o resetach haseł/weryfikacji email w MVP.
</prd_planning_summary>

<unresolved_issues>
1. Brak precyzyjnej reguły różnicowania trzech przepisów i oczekiwanych zmian między nimi.
2. Brak definicji struktury logów odrzuceń (pola, przyczyny, skala).
3. Brak decyzji o minimalnym zakresie funkcji kont (reset hasła, weryfikacja email).
4. Brak określenia minimalnych ekranów i nawigacji (przepisy, zapis, powtórki) przy braku onboardingu.
</unresolved_issues>
</conversation_summary>
