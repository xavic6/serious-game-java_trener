package trenergame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.Serializable;

/**
 * Menedżer ligi: tabela, statystyki drużyn, sortowanie
 * Obsługuje 10 drużyn, 9 rund (każdy z każdym raz)
 */
public class LeagueManager implements Serializable {
    private static final long serialVersionUID = 1L; // Serializacja save/load

    private List<TeamStats> teams;      // Lista statystyk wszystkich drużyn
    private int currentRound = 0;       // Aktualna runda (0-9)
    private int totalRounds = 9;        // 10 drużyn = 9 rund (round-robin)

    /**
     * Konstruktor - tworzy ligę z nazw i siły drużyn
     */
    public LeagueManager(String[] teamNames, int[] teamStrengths) {
        teams = new ArrayList<>();
        // Inicjalizuje TeamStats dla każdej drużyny
        for (int i = 0; i < teamNames.length; i++) {
            teams.add(new TeamStats(teamNames[i], teamStrengths[i]));
        }
    }

    /**
     * Znajduje drużynę po nazwie (używa w meczach)
     */
    public TeamStats getTeam(String teamName) {
        for (TeamStats team : teams) {
            if (team.getTeamName().equals(teamName)) {
                return team;
            }
        }
        return null; // Nie znaleziono
    }

    /**
     * Zwraca posortowaną tabelę ligową (punkty > bramki > name)
     */
    public List<TeamStats> getStandings() {
        List<TeamStats> standings = new ArrayList<>(teams);
        standings.sort(new Comparator<TeamStats>() {
            @Override
            public int compare(TeamStats t1, TeamStats t2) {
                // 1. Punkty malejąco
                if (t1.getPoints() != t2.getPoints()) {
                    return Integer.compare(t2.getPoints(), t1.getPoints());
                }
                // 2. Różnica bramek malejąco
                if (t1.getGoalDifference() != t2.getGoalDifference()) {
                    return Integer.compare(t2.getGoalDifference(), t1.getGoalDifference());
                }
                // 3. Bramki strzelone malejąco
                if (t1.getGoalsFor() != t2.getGoalsFor()) {
                    return Integer.compare(t2.getGoalsFor(), t1.getGoalsFor());
                }
                // 4. Nazwa alfabetycznie
                return t1.getTeamName().compareTo(t2.getTeamName());
            }
        });
        return standings;
    }

    /**
     * Konwertuje standings na tablicę do JTable (miejsce/nazwa/mecze/punkty/bramki/siła)
     */
    public Object[][] getTableData() {
        List<TeamStats> standings = getStandings();
        Object[][] data = new Object[standings.size()][6]; // 6 kolumn
        
        for (int i = 0; i < standings.size(); i++) {
            TeamStats team = standings.get(i);
            data[i][0] = (i + 1);                           // Miejsce (1-10)
            data[i][1] = team.getTeamName();                 // Nazwa drużyny
            data[i][2] = team.getMatches();                  // Mecze (0-18)
            data[i][3] = team.getPoints();                   // Punkty (0-54)
            data[i][4] = team.getGoalsFor() + "-" + team.getGoalsAgainst(); // Bramki (np. 25-15)
            data[i][5] = team.getStrength();                 // Siła początkowa (1-99)
        }
        
        return data;
    }

    /**
     * Zwraca aktualną rundę (do kalendarza/UI)
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Przechodzi do następnej rundy (update po meczach)
     */
    public void nextRound() {
        currentRound++; // Inkrementuje po rozegraniu rundy
    }

    /**
     * Całkowita liczba rund (stała dla 10 drużyn)
     */
    public int getTotalRounds() {
        return totalRounds;
    }

    /**
     * Kopia listy wszystkich drużyn (bezpieczna)
     */
    public List<TeamStats> getAllTeams() {
        return new ArrayList<>(teams);
    }
}
