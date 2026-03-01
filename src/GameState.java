package trenergame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa do serializacji pełnego stanu gry (save/load)
 * Przechowuje wszystkie dane potrzebne do wznowienia gry
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L; // Wersja serializacji

    // Stan gry z GamePanelCustom (dzień, mecze, forma)
    public int gameDay;             // Aktualny dzień (1-36)
    public int matchesPlayed;       // Liczba rozegranych meczów (0-18)
    public int formaDruzyny;        // Forma drużyny (0-100)
    public String teamName;         // Nazwa drużyny gracza
    public boolean hasSpokenToday;  // Czy już rozmawiał dzisiaj (blokada)

    // Obiekty gry (serializowalne)
    public Pilkarz[] pilkarze;      // Tablica piłkarzy z pełnymi statystykami
    public MatchManager matchManager; // Terminarz i wyniki meczów
    public LeagueManager leagueManager; // Tabela ligi i obliczenia
    public String[][] daneLiga;     // Dane tabeli ligi (miejsce, nazwa, punkty itd.)

    /**
     * Konstruktor - kopiuje stan z GamePanelCustom do serializacji
     */
    public GameState(int gameDay, int matchesPlayed, int formaDruzyny, String teamName,
                     boolean hasSpokenToday, Pilkarz[] pilkarze, MatchManager matchManager,
                     LeagueManager leagueManager, String[][] daneLiga) {
        this.gameDay = gameDay;
        this.matchesPlayed = matchesPlayed;
        this.formaDruzyny = formaDruzyny;
        this.teamName = teamName;
        this.hasSpokenToday = hasSpokenToday;
        this.pilkarze = pilkarze;
        this.matchManager = matchManager;
        this.leagueManager = leagueManager;
        this.daneLiga = daneLiga;
    }
}
