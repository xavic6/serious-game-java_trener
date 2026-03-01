package trenergame;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility do pozycji: odległość (BR=0..NP=6) + waga roli (Kapitan=2.5x)
 * Używa w GamePanelCustom (ważone średnie morale) + taktyka?
 */
public class PositionUtil {
    // Mapa pozycji liniowych: BR=0, PO=1, ŚO=2, LO=3, ŚP=4, LW=5, NP=6
    private static final Map<String, Integer> POZYCJE = new HashMap<>();
    static {
        POZYCJE.put("BR", 0);  // Bramkarz
        POZYCJE.put("PO", 1);  // Prawy obrońca
        POZYCJE.put("ŚO", 2);  // Środkowy obrońca
        POZYCJE.put("LO", 3);  // Lewy obrońca
        POZYCJE.put("ŚP", 4);  // Środkowy pomocnik
        POZYCJE.put("LW", 5);  // Lewy skrzydłowy
        POZYCJE.put("NP", 6);  // Napastnik
    }

    /**
     * Odległość pozycji liniowa (|p1 - p2|, max 3 jeśli nieznana)
     * Np. BR-NP=6, ŚP-ŚO=2 → do taktyki/rotacji?
     */
    public static int odlegloscPozycji(String p1, String p2) {
        if (!POZYCJE.containsKey(p1) || !POZYCJE.containsKey(p2)) return 3; // Default max
        return Math.abs(POZYCJE.get(p1) - POZYCJE.get(p2));
    }

    /**
     * Waga roli w średnich (np. Kapitan 2.5x waży więcej w atmosferze/poparciu)
     * Używa w GamePanelCustom.updateProgressBars (ważone morale/relacje)
     */
    public static double getWagaPozycji(String pozycjaWSzatni) {
        switch(pozycjaWSzatni) {
            case "Kapitan":    return 2.5;  // Najważniejszy
            case "Lider":      return 2.2;  // Wysoki wpływ
            case "Gwiazda":    return 2.0;  // Gwiazdorski
            case "Weteran":    return 1.8;  // Doświadczony
            case "Junior":     return 0.9;  // Mniejszy wpływ
            case "Outsider":   return 0.7;  // Minimalny
            case "Przeciętny":
            default:           return 1.0;  // Bazowa waga
        }
    }
}
