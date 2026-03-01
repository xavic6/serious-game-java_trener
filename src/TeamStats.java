package trenergame;

import java.io.Serializable;

/**
 * Statystyki drużyny: mecze, wygrane/remisy/porażki, bramki, punkty
 * Używa w LeagueManager (sortowanie tabeli) + MatchManager.recordMatch
 */
public class TeamStats implements Serializable {
    private String teamName;       // Nazwa (z CreateLeaguePanel)
    private int strength;          // Początkowa siła (1-99, stała)
    
    // Statystyki meczowe (aktualizowane po symulacjach)
    private int matches = 0;       // Rozegrane (0-18)
    private int wins = 0;          // Zwycięstwa
    private int draws = 0;         // Remisy
    private int losses = 0;        // Porażki
    private int goalsFor = 0;      // Strzelone
    private int goalsAgainst = 0;  // Stracone

    /**
     * Inicjalizuje z nazwy i siły (liga start)
     */
    public TeamStats(String teamName, int strength) {
        this.teamName = teamName;
        this.strength = strength;
    }

    /**
     * Rejestruje wynik meczu: +1 match + bramki + W/D/L
     */
    public void recordMatch(int goalsScored, int goalsConceded) {
        matches++;                    // +1 mecz
        goalsFor += goalsScored;      // Strzelone
        goalsAgainst += goalsConceded;// Stracone

        if (goalsScored > goalsConceded) {
            wins++;                   // 3 pkt
        } else if (goalsScored < goalsConceded) {
            losses++;                 // 0 pkt
        } else {
            draws++;                  // 1 pkt
        }
    }

    /**
     * Punkty: 3*W + 1*D (do sortowania tabeli)
     */
    public int getPoints() {
        return wins * 3 + draws * 1;
    }

    /**
     * Różnica bramek: GF - GA (tiebreaker #2)
     */
    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    // ========== GETTERY ==========
    public String getTeamName() { return teamName; }
    public int getStrength() { return strength; }     // Stała
    public int getMatches() { return matches; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }

    // ========== SETTERY (do load/save) ==========
    public void setMatches(int matches) { this.matches = matches; }
    public void setWins(int wins) { this.wins = wins; }
    public void setDraws(int draws) { this.draws = draws; }
    public void setLosses(int losses) { this.losses = losses; }
    public void setGoalsFor(int goalsFor) { this.goalsFor = goalsFor; }
    public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }
}
