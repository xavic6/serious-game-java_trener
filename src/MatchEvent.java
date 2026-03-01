package trenergame;

import java.io.Serializable;

/**
 * Wydarzenie meczowe do symulacji live (bramka, kartka, zmiana)
 * Używa w MatchViewWindow do wyświetlania tekstowego "live"
 */
public class MatchEvent implements Serializable {
    private static final long serialVersionUID = 1L; // Serializacja (save/load)

    // Typy wydarzeń meczu
    public enum EventType {
        GOAL,         // Bramka
        HALFTIME,     // Przerwa
        END,          // Koniec
        FOUL,         // Faul
        YELLOW_CARD,  // Żółta
        RED_CARD,     // Czerwona
        SUBSTITUTION  // Zmiana
    }

    // Dane wydarzenia
    public EventType type;         // Typ (GOAL, YELLOW_CARD itd.)
    public int minute;             // Minuta (1-90+)
    public Pilkarz player;         // Piłkarz (dla kartek/zmian)
    public String teamName;        // Drużyna (home/away)
    public String description;     // Opis (np. "Strzał Messiego!")
    public int goalsHome;          // Bramki gospodarzy w tym momencie
    public int goalsAway;          // Bramki gości w tym momencie

    /**
     * Konstruktor - tworzy pełne wydarzenie z danymi
     */
    public MatchEvent(EventType type, int minute, Pilkarz player, String teamName, 
                      String description, int goalsHome, int goalsAway) {
        this.type = type;
        this.minute = minute;
        this.player = player;
        this.teamName = teamName;
        this.description = description;
        this.goalsHome = goalsHome;
        this.goalsAway = goalsAway;
    }

    /**
     * Formatuje do tekstu z emoji (do JLabel/JTextArea live)
     */
    public String getEventDisplay() {
        switch (type) {
            case GOAL:
                // "⚽ BRAMKA! Real - Ronaldo (2-1)"
                return "⚽ BRAMKA! " + teamName + " - " + description + 
                       " (Wynik: " + goalsHome + "-" + goalsAway + ")";
            case HALFTIME:
                return "🏁 POŁOWA " + goalsHome + "-" + goalsAway;
            case END:
                return "🏁 KONIEC MECZU " + goalsHome + "-" + goalsAway;
            case FOUL:
                return "⚠️ FAUL - " + description;
            case YELLOW_CARD:
                return "🟨 ŻÓŁTA KARTKA - " + 
                       (player != null ? player.getImieINazwisko() : "Zawodnik");
            case RED_CARD:
                return "🔴 CZERWONA KARTKA - " + 
                       (player != null ? player.getImieINazwisko() : "Zawodnik");
            case SUBSTITUTION:
                return "🔄 ZMIANA - " + description;
            default:
                return description; // Fallback
        }
    }
}
