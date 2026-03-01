package trenergame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

/**
 * Menedżer meczów: terminarz, symulacja wyników, statystyki drużyn
 * Obsługuje 18 meczów na drużynę (round-robin 2x), dni zaplanowane
 */
public class MatchManager implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Internal klasa meczu - dane + symulacja
     */
    public static class Match implements Serializable {
        public String homeTeam;     // Gospodarze
        public String awayTeam;     // Goście
        public int goalsHome;       // Bramki home (-1 = nie rozegrany)
        public int goalsAway;       // Bramki away
        public boolean played;      // Czy symulowany
        public int round;           // Numer rundy (1-18)
        public int dayScheduled;    // Dzień gry (14 + round*7)
        public Pilkarz[] homeSquad; // Skład home (opcjonalny)
        public Pilkarz[] awaySquad; // Skład away
        public Pilkarz[] selectedTeam; // Skład gracza (jeśli home)

        /**
         * Ustawia skład gracza przed symulacją
         */
        public void setSelectedTeam(Pilkarz[] team) {
            this.selectedTeam = team;
        }

        /**
         * Tworzy mecz z danymi (day = 14 + round*7)
         */
        public Match(String homeTeam, String awayTeam, int round, 
                     Pilkarz[] homeSquad, Pilkarz[] awaySquad) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.round = round;
            this.goalsHome = -1;
            this.goalsAway = -1;
            this.played = false;
            this.homeSquad = homeSquad;
            this.awaySquad = awaySquad;
            this.dayScheduled = 14 + (round * 7); // Co tydzień rundy
        }

        /**
         * Symuluje wynik (0-3 : 0-3, losowo)
         */
        public void simulateResult() {
            if (!played) {
                Random random = new Random();
                this.goalsHome = random.nextInt(4);  // 0-3 home
                this.goalsAway = random.nextInt(4);  // 0-3 away
                this.played = true;
            }
        }

        /**
         * Zwraca wynik jako string (np. "Real 2-1 Barca")
         */
        public String getResult() {
            if (played) {
                return homeTeam + " " + goalsHome + "-" + goalsAway + " " + awayTeam;
            } else {
                return homeTeam + " vs " + awayTeam;
            }
        }

        /**
         * Punkty dla home (3/1/0)
         */
        public int getHomePoints() {
            if (!played) return 0;
            if (goalsHome > goalsAway) return 3;
            if (goalsHome == goalsAway) return 1;
            return 0;
        }

        /**
         * Punkty dla away (3/1/0)
         */
        public int getAwayPoints() {
            if (!played) return 0;
            if (goalsAway > goalsHome) return 3;
            if (goalsAway == goalsHome) return 1;
            return 0;
        }
    }

    private List<Match> allMatches;    // Lista wszystkich meczów ligi
    private int currentGameDay;        // Bieżący dzień gry

    /**
     * Pusta lista meczów
     */
    public MatchManager() {
        this.allMatches = new ArrayList<>();
        this.currentGameDay = 1;
    }

    /**
     * Dodaje mecz do terminarza
     */
    public void addMatch(Match match) {
        allMatches.add(match);
    }

    /**
     * Symuluje mecze zaplanowane na dany dzień
     */
    public void updateMatchesForDay(int gameDay) {
        this.currentGameDay = gameDay;
        for (Match match : allMatches) {
            if (!match.played && match.dayScheduled == gameDay) {
                match.simulateResult(); // Losuje wynik
            }
        }
    }

    /**
     * Pierwszy nierozgrywany mecz na dzień
     */
    public Match getTodayMatch(int gameDay) {
        for (Match match : allMatches) {
            if (match.dayScheduled == gameDay && !match.played) {
                return match;
            }
        }
        return null;
    }

    /**
     * Mecze danej rundy (do kalendarza)
     */
    public List<Match> getMatchesByRound(int round) {
        List<Match> roundMatches = new ArrayList<>();
        for (Match match : allMatches) {
            if (match.round == round) {
                roundMatches.add(match);
            }
        }
        return roundMatches;
    }

    /**
     * Mecze na konkretny dzień
     */
    public List<Match> getMatchesForDay(int gameDay) {
        List<Match> dayMatches = new ArrayList<>();
        for (Match match : allMatches) {
            if (match.dayScheduled == gameDay) {
                dayMatches.add(match);
            }
        }
        return dayMatches;
    }

    /**
     * Wszystkie mecze (do save/load)
     */
    public List<Match> getAllMatches() {
        return allMatches;
    }

    // ========== STATYSTYKI DRUŻYN ==========
    // Oblicza na podstawie rozegranych meczów

    /**
     * Suma punktów drużyny (home + away)
     */
    public int getTeamPoints(String teamName) {
        int points = 0;
        for (Match match : allMatches) {
            if (!match.played) continue;
            if (match.homeTeam.equals(teamName)) {
                points += match.getHomePoints();
            } else if (match.awayTeam.equals(teamName)) {
                points += match.getAwayPoints();
            }
        }
        return points;
    }

    /**
     * Bramki strzelone przez drużynę
     */
    public int getTeamGoalsFor(String teamName) {
        int goals = 0;
        for (Match match : allMatches) {
            if (!match.played) continue;
            if (match.homeTeam.equals(teamName)) {
                goals += match.goalsHome;
            } else if (match.awayTeam.equals(teamName)) {
                goals += match.goalsAway;
            }
        }
        return goals;
    }

    /**
     * Bramki stracone przez drużynę
     */
    public int getTeamGoalsAgainst(String teamName) {
        int goals = 0;
        for (Match match : allMatches) {
            if (!match.played) continue;
            if (match.homeTeam.equals(teamName)) {
                goals += match.goalsAway;
            } else if (match.awayTeam.equals(teamName)) {
                goals += match.goalsHome;
            }
        }
        return goals;
    }

    /**
     * Różnica bramek (strzelone - stracone)
     */
    public int getTeamGoalDifference(String teamName) {
        return getTeamGoalsFor(teamName) - getTeamGoalsAgainst(teamName);
    }

    /**
     * Liczba rozegranych meczów drużyny
     */
    public int getTeamMatchesPlayed(String teamName) {
        int matches = 0;
        for (Match match : allMatches) {
            if (!match.played) continue;
            if (match.homeTeam.equals(teamName) || match.awayTeam.equals(teamName)) {
                matches++;
            }
        }
        return matches;
    }

    /**
     * Najbliższy nierozgrywany mecz
     */
    public Match getNextMatch(int currentGameDay) {
        for (Match match : allMatches) {
            if (!match.played && match.dayScheduled > currentGameDay) {
                return match;
            }
        }
        return null;
    }

    /**
     * Czy jest mecz dzisiaj
     */
    public boolean hasMatchToday(int gameDay) {
        for (Match match : allMatches) {
            if (match.dayScheduled == gameDay && !match.played) {
                return true;
            }
        }
        return false;
    }

    /**
     * Wynik meczu z danego dnia (jeśli rozegrany)
     */
    public String getMatchResultForDay(int gameDay) {
        for (Match match : allMatches) {
            if (match.dayScheduled == gameDay && match.played) {
                return match.getResult();
            }
        }
        return null;
    }

    /**
     * Unikalne nazwy wszystkich drużyn
     */
    public Set<String> getAllTeams() {
        Set<String> teams = new HashSet<>();
        for (Match match : allMatches) {
            teams.add(match.homeTeam);
            teams.add(match.awayTeam);
        }
        return teams;
    }
}
