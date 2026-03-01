# serious-game-java_trener
Trener. to serious game napisana w Javie, symulujaca zarzadzanie szatnia druzyny pilkarskiej. Gracz wciela sie w role trenera, podejmujac decyzje wplywajace na wyniki sportowe, atmosfere w zespole i relacje interpersonalne.


OPIS PROJEKTU
-------------
Gra edukacyjna skupia sie na balansowaniu czterech kluczowych wskaznikow:
- Zadowolenie zarzadu
- Atmosfera w szatni  
- Poparcie trenera
- Forma druzyny

Zaniedbanie ktoregokolwiek prowadzi do zwolnienia trenera i konca gry. Projekt opiera sie na badaniach naukowych dotyczacych serious games.

Aplikacja desktopowa 2D dziala w rozdzielczosci do 1280x1024, obsuguje GUI, zdarzenia i operacje na plikach.


GLÓWNE CECHY
------------
- Tworzenie ligi (10 druzyn) i wlasnej druzyny (23 zawodników)
- Model pilkarza z 14 atrybutami (morale, relacje z trenerem, forma, kontuzje)
- Sortowalna tabela zawodnikow i kolorowe paski wskaznikow
- Decyzje dialogowe z graczami
- Walidacja skladu (max 1 kapitan, junior max 19 lat, weteran min 33 lata)
- Obliczanie wskaznikow i generowanie atrybutow


TECHNOLOGIE I WYMAGANIA
-----------------------
Język:       Java (desktopowa aplikacja)
Interfejs:   Graficzny, menu z animacjami
Grafika:     Tla i ikony na darmowych licencjach
Przechowywanie: RAM (GameCreationManager, GamePanel); plan: JSON/saves


INSTALACJA I URUCHOMIENIE
-------------------------
1. git clone https://github.com/xavic6/serious-game-java_trener.git
2. Zbuduj projekt (IntelliJ/Eclipse)
3. Uruchom glowna klase (Main.java/GamePanel)
4. Rozdzielczosc: max 1280x1024

Brak zewnetrznych zaleznosci - czysta Java.


ŹRÓDŁA INSPIRACJI
------------------
- "Using Serious Games... Co-Operative Decision-making" (Ștefan i in., 2019)
- "Interactive Serious Games for Cognitive Training" (Canapa i in., 2025)[file:1]
