package trenergame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Symulator meczu live: generuje wydarzenia (bramki, kartki, faule) co ~5-10 min
 * Używa siły drużyn + losowość + polskie nazwiska, ~20-25 wydarzeń/mecz
 */
public class MatchSimulator {
    private String homeTeam;           // Gospodarze
    private String awayTeam;           // Goście
    private int goalsHome = 0;         // Bramki home
    private int goalsAway = 0;         // Bramki away
    private double homeTeamStrength;   // Siła home (40-70)
    private double awayTeamStrength;   // Siła away (40-70)
    private List<MatchEvent> matchEvents = new ArrayList<>(); // Lista wydarzeń
    private Random random = new Random();
    
    // Liczniki kartek (symulacja realizmu)
    private int yellowCardsHome = 0;
    private int yellowCardsAway = 0;
    private boolean redCardGivenHome = false;
    private boolean redCardGivenAway = false;
    
    // Składy (opcjonalne, dla prawdziwych nazw)
    private Pilkarz[] homeTeamPlayers;
    private Pilkarz[] awayTeamPlayers;

    // Polskie nazwiska dla AI drużyn
    private static final String[] POLISH_SURNAMES = {
        "Kowalski", "Nowak", "Wójcik", "Kamiński", "Lewandowski", "Milik", "Zieliński", 
        "Glik", "Bednarek", "Szczęsny", "Fabiański", "Szromnik", "Boruc", "Dudek",
        "Piszczek", "Grosicki", "Krychowiak", "Błaszczykowski", "Jędrzejczyk", "Pazdan",
        "Rybus", "Sousa", "Popek", "Duda", "Kostrzewa", "Szewczyk", "Świderski",
        "Piątek", "Puchacz", "Frankowski", "Vereš", "Kędziora", "Matuszyński", "Przybylski"
    };

    /**
     * Internal wydarzenie (minuta + opis + wynik)
     */
    public static class MatchEvent {
        public int minute;         // Minuta (0-90)
        public String description; // Opis z emoji
        public int goalsHome;      // Wynik home w momencie
        public int goalsAway;      // Wynik away w momencie

        public MatchEvent(int minute, String description, int goalsHome, int goalsAway) {
            this.minute = minute;
            this.description = description;
            this.goalsHome = goalsHome;
            this.goalsAway = goalsAway;
        }

        /**
         * Format do UI: "45' ⚽ BRAMKA!"
         */
        public String getEventDisplay() {
            return minute + "' " + description;
        }
    }

    /**
     * Inicjalizuje symulator z losową siłą (40-70)
     */
    public MatchSimulator(String homeTeam, String awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamStrength = 40 + random.nextDouble() * 30; // 40-70
        this.awayTeamStrength = 40 + random.nextDouble() * 30; // 40-70
    }

    /**
     * Ustawia składy (dla prawdziwych nazw strzelców)
     */
    public void setTeams(Pilkarz[] homeTeamPlayers, Pilkarz[] awayTeamPlayers) {
        this.homeTeamPlayers = homeTeamPlayers;
        this.awayTeamPlayers = awayTeamPlayers;
    }

    /**
     * Symuluje cały mecz: 0', połowa, 90' + 20-25 wydarzeń
     */
    public void simulateMatch() {
        matchEvents.clear();
        goalsHome = 0;
        goalsAway = 0;
        yellowCardsHome = 0;
        yellowCardsAway = 0;
        redCardGivenHome = false;
        redCardGivenAway = false;

        // 0' Rozpoczęcie
        matchEvents.add(new MatchEvent(0, "⚽ ROZPOCZĘCIE MECZU - " + homeTeam + " vs " + awayTeam, 0, 0));

        // Cele kartek (3-8/drużynę) + 20% na czerwoną
        int targetYellowHome = generateYellowCardTarget();
        int targetYellowAway = generateYellowCardTarget();
        boolean willHaveRedCardHome = random.nextDouble() < 0.20;
        boolean willHaveRedCardAway = random.nextDouble() < 0.20;

        // 1. połowa: co 6-14 min
        for (int minute = 2; minute <= 45; minute += 6 + random.nextInt(8)) {
            simulateEvent(minute, targetYellowHome, targetYellowAway, willHaveRedCardHome, willHaveRedCardAway);
        }

        // Połowa
        matchEvents.add(new MatchEvent(45, "🏁 KONIEC PIERWSZEJ POŁOWY: " + goalsHome + " - " + goalsAway, goalsHome, goalsAway));

        // 2. połowa
        for (int minute = 48; minute <= 88; minute += 6 + random.nextInt(8)) {
            simulateEvent(minute, targetYellowHome, targetYellowAway, willHaveRedCardHome, willHaveRedCardAway);
        }

        // 90' + podsumowanie
        matchEvents.add(new MatchEvent(90, "🏁 KONIEC MECZU: " + goalsHome + " - " + goalsAway, goalsHome, goalsAway));
        if (goalsHome > goalsAway) {
            matchEvents.add(new MatchEvent(90, "🥇 ZWYCIĘSTWO " + homeTeam + "!", goalsHome, goalsAway));
        } else if (goalsAway > goalsHome) {
            matchEvents.add(new MatchEvent(90, "🥇 ZWYCIĘSTWO " + awayTeam + "!", goalsHome, goalsAway));
        } else {
            matchEvents.add(new MatchEvent(90, "🤝 REMIS!", goalsHome, goalsAway));
        }
    }

    /**
     * Cel żółtych kartek (3-5: 80%, 5-8: 20%)
     */
    private int generateYellowCardTarget() {
        double rand = random.nextDouble();
        if (rand < 0.50) return 3 + random.nextInt(3); // 3-5 (50%)
        else if (rand < 0.80) return 4 + random.nextInt(3); // 4-6 (30%)
        else return 5 + random.nextInt(4); // 5-8 (20%)
    }

    /**
     * Jedno wydarzenie: 25% gol, 35% żółta, 15% czerwona, 25% inne
     */
    private void simulateEvent(int minute, int targetYellowHome, int targetYellowAway, 
                               boolean willHaveRedCardHome, boolean willHaveRedCardAway) {
        double eventRand = random.nextDouble();

        if (eventRand < 0.25) { // Gol
            simulateGoal(minute);
        } else if (eventRand < 0.60) { // Żółta
            simulateYellowCard(minute, targetYellowHome, targetYellowAway);
        } else if (eventRand < 0.75 && (willHaveRedCardHome || willHaveRedCardAway)) { // Czerwona
            simulateRedCard(minute, willHaveRedCardHome, willHaveRedCardAway);
        } else { // Inne (faul, spalony)
            simulateOtherEvent(minute);
        }
    }

    /**
     * Gol: szansa wg siły, max 5/drużynę, prawdziwe/polskie nazwisko
     */
    private void simulateGoal(int minute) {
        if (random.nextDouble() < 0.25) { // 25% sukces strzału
            boolean isHome = random.nextDouble() < (homeTeamStrength / (homeTeamStrength + awayTeamStrength));
            if (isHome && goalsHome < 5) {
                goalsHome++;
                String scorer = getHomeTeamScorer();
                matchEvents.add(new MatchEvent(minute, "⚽ BRAMKA! " + homeTeam + " - " + scorer + 
                                               " | " + goalsHome + "-" + goalsAway, goalsHome, goalsAway));
            } else if (!isHome && goalsAway < 5) {
                goalsAway++;
                String scorer = getAwayTeamScorer();
                matchEvents.add(new MatchEvent(minute, "⚽ BRAMKA! " + awayTeam + " - " + scorer + 
                                               " | " + goalsHome + "-" + goalsAway, goalsHome, goalsAway));
            }
        }
    }

    /**
     * Strzelec home (z składu lub losowy Polak)
     */
    private String getHomeTeamScorer() {
        if (homeTeamPlayers != null && homeTeamPlayers.length > 0) {
            return homeTeamPlayers[random.nextInt(homeTeamPlayers.length)].getImieINazwisko();
        }
        return POLISH_SURNAMES[random.nextInt(POLISH_SURNAMES.length)];
    }

    /**
     * Strzelec away (losowy Polak)
     */
    private String getAwayTeamScorer() {
        return POLISH_SURNAMES[random.nextInt(POLISH_SURNAMES.length)];
    }

    /**
     * Żółta kartka wg celu (nie przekracza target)
     */
    private void simulateYellowCard(int minute, int targetYellowHome, int targetYellowAway) {
        boolean isHome = random.nextDouble() < 0.5;
        if (isHome && yellowCardsHome < targetYellowHome) {
            yellowCardsHome++;
            String player = getHomeTeamPlayer();
            matchEvents.add(new MatchEvent(minute, "🟨 ŻÓŁTA KARTKA - " + homeTeam + " (" + player + ")", goalsHome, goalsAway));
        } else if (!isHome && yellowCardsAway < targetYellowAway) {
            yellowCardsAway++;
            String player = getAwayTeamPlayer();
            matchEvents.add(new MatchEvent(minute, "🟨 ŻÓŁTA KARTKA - " + awayTeam + " (" + player + ")", goalsHome, goalsAway));
        }
    }

    /**
     * Piłkarz home do kartki
     */
    private String getHomeTeamPlayer() {
        if (homeTeamPlayers != null && homeTeamPlayers.length > 0) {
            return homeTeamPlayers[random.nextInt(homeTeamPlayers.length)].getImieINazwisko();
        }
        return POLISH_SURNAMES[random.nextInt(POLISH_SURNAMES.length)];
    }

    /**
     * Piłkarz away do kartki
     */
    private String getAwayTeamPlayer() {
        return POLISH_SURNAMES[random.nextInt(POLISH_SURNAMES.length)];
    }

    /**
     * Czerwona kartka (jedna max/drużynę, jeśli zaplanowana)
     */
    private void simulateRedCard(int minute, boolean willHaveRedCardHome, boolean willHaveRedCardAway) {
        boolean isHome = random.nextDouble() < 0.5;
        if (isHome && willHaveRedCardHome && !redCardGivenHome) {
            redCardGivenHome = true;
            String player = getHomeTeamPlayer();
            matchEvents.add(new MatchEvent(minute, "🔴 CZERWONA KARTKA - " + homeTeam + " (" + player + ") Wydalenie!", goalsHome, goalsAway));
        } else if (!isHome && willHaveRedCardAway && !redCardGivenAway) {
            redCardGivenAway = true;
            String player = getAwayTeamPlayer();
            matchEvents.add(new MatchEvent(minute, "🔴 CZERWONA KARTKA - " + awayTeam + " (" + player + ") Wydalenie!", goalsHome, goalsAway));
        }
    }

    /**
     * Inne: faul, spalony, zmiana (7 szablonów)
     */
    private void simulateOtherEvent(int minute) {
        String[] eventTemplates = {
            "⚠️ FAUL - {team}",
            "📍 SPALONY - {team}",
            "🎯 ZAGROŻENIE! {team} prawie strzela!",
            "🛡️ OBRONA - {team} świetnie broni",
            "⏱️ PRZERWA - Chwila zastanowienia",
            "🔄 WYMIANA - {team} dokonuje zmian",
            "💥 ZDERZENIE - Gracze {team} w przewadze"
        };
        String eventTemplate = eventTemplates[random.nextInt(eventTemplates.length)];
        String team = random.nextDouble() < 0.5 ? homeTeam : awayTeam;
        String event = eventTemplate.replace("{team}", team);
        matchEvents.add(new MatchEvent(minute, event, goalsHome, goalsAway));
    }

    // ========== GETTERY ==========
    public List<MatchEvent> getMatchEvents() { return matchEvents; }
    public int getGoalsHome() { return goalsHome; }
    public int getGoalsAway() { return goalsAway; }
    public double getHomeTeamStrength() { return homeTeamStrength; }
    public double getAwayTeamStrength() { return awayTeamStrength; }
}
