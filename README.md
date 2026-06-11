
**Projekt:** Sieciowa gra w statki (Battleships)  
**Język:** Java 17+ 

---

## 1. Zakres projektu

Aplikacja desktopowa implementująca grę w statki dla **dwóch graczy w sieci lokalnej (LAN)**. Jeden gracz pełni rolę hosta (serwer TCP), drugi łączy się jako klient podając adres IP i numer portu. Interfejs graficzny zrealizowany w JavaFX.

---

## 2. Reguły gry

### 2.1 Plansza

- Plansza 10×10, kolumny oznaczone literami A–J, wiersze cyframi 1–10.
- Każdy gracz układa flotę **samodzielnie** na swojej planszy przed rozpoczęciem rozgrywki.
- Plansza gracza **nigdy nie jest przesyłana przez sieć** – pełna izolacja stanów.

### 2.2 Flota (standardowa)

| Nazwa statku      | Rozmiar | Liczba |
|-------------------|---------|--------|
| Lotniskowiec      | 5       | 1      |
| Pancernik         | 4       | 1      |
| Krążownik         | 3       | 1      |
| Niszczyciel       | 3       | 1      |
| Łódź podwodna     | 2       | 1      |

Łącznie: 5 statków, 16 pól.

### 2.3 Przebieg tury

1. Aktywny gracz wybiera współrzędne strzału na planszy przeciwnika.
2. Współrzędne są przesyłane do przeciwnika przez sieć.
3. Przeciwnik oblicza wynik lokalnie i odsyła jedną z trzech odpowiedzi: `HIT`, `MISS`, `SUNK`.
4. Interfejs aktualizuje planszę śledząca trafienia/pudła.
5. Zmiana tury następuje po każdym strzale (niezależnie od wyniku).

### 2.4 Warunki zakończenia

- Gra kończy się, gdy wszystkie statki jednego z graczy zostaną zatopione.
- Wyświetlany jest ekran końcowy z informacją o zwycięzcy.
- Możliwość powrotu do menu głównego lub zamknięcia aplikacji.

---

## 3. Architektura sieciowa (P2P over TCP)

### 3.1 Model połączenia

```
[Gracz A – Host]          [Gracz B – Klient]
  ServerSocket              Socket
  port: wybierany           IP + port hosta
  przez użytkownika
```

- Host uruchamia `ServerSocket` i czeka na połączenie.
- Klient podaje IP i port hosta, nawiązuje połączenie TCP.
- Po nawiązaniu połączenia obaj gracze są równorzędni w toku gry.

### 3.2 Protokół komunikacji (tekstowy, UTF-8, linia = wiadomość)

| Kierunek              | Format wiadomości    | Opis                              |
|-----------------------|----------------------|-----------------------------------|
| Strzelający → Cel     | `SHOT A5`            | Strzał w pole A5                  |
| Cel → Strzelający     | `RESULT HIT`         | Trafienie                         |
| Cel → Strzelający     | `RESULT MISS`        | Pudło                             |
| Cel → Strzelający     | `RESULT SUNK`        | Zatopiony statek                  |
| Dowolny → Dowolny     | `READY`              | Gracz zakończył układanie statków |
| Dowolny → Dowolny     | `GAMEOVER`           | Informacja o końcu gry            |
| Dowolny → Dowolny     | `DISCONNECT`         | Graceful shutdown                 |

### 3.3 Zasada izolacji stanów

> **Przez sieć przesyłane są wyłącznie współrzędne strzału i wynik.**  
> Żadna reprezentacja planszy (rozmieszczenie statków) nigdy nie opuszcza klienta gracza.  
> Każdy klient samodzielnie waliduje strzał i generuje odpowiedź `HIT/MISS/SUNK`.

---

## 4. Wymagania funkcjonalne

| ID   | Wymaganie                                                                                  | Priorytet |
|------|--------------------------------------------------------------------------------------------|-----------|
| FR-1 | Gracz może uruchomić grę jako host (podając port)                                          | Must Have |
| FR-2 | Gracz może dołączyć do gry jako klient (podając IP i port)                                | Must Have |
| FR-3 | Interfejs graficzny umożliwia rozmieszczenie statków metodą przeciągnij-upuść lub kliknięć | Must Have |
| FR-4 | Aplikacja waliduje poprawność rozmieszczenia statków (brak nakładania, brak sąsiedztwa)   | Must Have |
| FR-5 | Gracz może oddać strzał przez kliknięcie pola na planszy przeciwnika                      | Must Have |
| FR-6 | Aplikacja wizualnie oznacza trafienia (czerwony) i pudła (szary) na obu planszach         | Must Have |
| FR-7 | Przy zatopieniu statku wszystkie jego pola są oznaczane                                   | Must Have |
| FR-8 | Po zakończeniu gry wyświetlany jest ekran z wynikiem                                       | Must Have |
| FR-9 | Aplikacja obsługuje rozłączenie przeciwnika (komunikat i powrót do menu)                  | Should Have |
| FR-10| Gracz może losowo rozmieścić statki jednym przyciskiem                                    | Should Have |

---

## 5. Definicja ukończenia (DoD – Definition of Done)

Zadanie jest uznane za ukończone, gdy:

1. Kod jest napisany i działa zgodnie z kryterium akceptacji.
2. Napisane są testy jednostkowe (TDD) weryfikujące zachowanie.
3. Wszystkie testy w projekcie przechodzą (`mvn test` = SUCCESS).
4. Kod jest zacommitowany na feature branchu i Pull Request jest otwarty.
5. Co najmniej jeden inny członek zespołu zatwierdził PR (code review).
6. PR jest zmergowany do `main`.
7. CI pipeline jest zielony po merge.
