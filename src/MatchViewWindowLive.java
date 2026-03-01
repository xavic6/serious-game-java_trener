package trenergame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Okno LIVE meczu: animacja wydarzeń co 800ms + symulacja reszty rundy po meczu gracza
 * Ciemne tło, duże nazwy, timer zdarzeń z MatchSimulator
 */
public class MatchViewWindowLive extends JDialog {
    private JPanel contentPanel;
    private MatchSimulator simulator;      // Symulator wydarzeń (20-25/mecz)
    private String homeTeamName, awayTeamName;
    private JLabel minuteLabel;            // "45'" aktualna minuta
    private JTextArea eventsArea;          // Lista live: "⚽ BRAMKA! 2-1"
    private JButton closeButton;           // Aktywny po 90'
    private int currentEventIndex = 0;     // Indeks animacji
    private javax.swing.Timer swingTimer;  // Timer 800ms/event
    private TeamStats homeTeamStats;       // Stats home (do zapisu)
    private TeamStats awayTeamStats;       // Stats away
    private LeagueManager leagueManager;   // Do innych meczów
    private List<MatchManager.Match> otherMatches; // Pozostałe mecze rundy

    /**
     * Live symulacja + reszta rundy, modalne okno
     */
    public MatchViewWindowLive(JFrame parent, String homeTeamName, String awayTeamName, 
                              Pilkarz[] homeTeam, Pilkarz[] awayTeam, 
                              TeamStats homeStats, TeamStats awayStats, 
                              LeagueManager leagueManager, 
                              List<MatchManager.Match> otherMatches) {
        super(parent, "LIVE: " + homeTeamName + " vs " + awayTeamName, true);
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.homeTeamStats = homeStats;
        this.awayTeamStats = awayStats;
        this.leagueManager = leagueManager;
        this.otherMatches = otherMatches;

        simulator = new MatchSimulator(homeTeamName, awayTeamName);
        simulator.setTeams(homeTeam, awayTeam); // Prawdziwe nazwiska strzelców

        setSize(1280, 720);
        setMinimumSize(new Dimension(1024, 576));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(true);

        simulator.simulateMatch(); // Generuje 20-25 wydarzeń
        initComponents();
        startLiveAnimation();      // Timer start
        setVisible(true);
    }

    /**
     * Ciemne tło + header (duże nazwy) + minuta + events + close
     */
    private void initComponents() {
        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Ciemne tło stadionu
                g.setColor(new Color(0, 0, 20, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header: HOME     AWAY (48pt white)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 15));
        headerPanel.setOpaque(false);
        
        JLabel homeLabel = new JLabel(homeTeamName);
        homeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        homeLabel.setForeground(Color.WHITE);
        
        JLabel awayLabel = new JLabel(awayTeamName);
        awayLabel.setFont(new Font("Arial", Font.BOLD, 48));
        awayLabel.setForeground(Color.WHITE);
        
        headerPanel.add(homeLabel);
        headerPanel.add(awayLabel);
        contentPanel.add(headerPanel);

        // Minuta: "45'" (36pt orange)
        minuteLabel = new JLabel("0'");
        minuteLabel.setFont(new Font("Arial", Font.BOLD, 36));
        minuteLabel.setForeground(Color.ORANGE);
        minuteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(minuteLabel);

        // Events: Courier 13pt, dark bg, auto-scroll
        eventsArea = new JTextArea("Mecz się rozpoczyna...\n");
        eventsArea.setEditable(false);
        eventsArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        eventsArea.setBackground(new Color(20, 20, 40));
        eventsArea.setForeground(Color.WHITE);
        eventsArea.setLineWrap(true);
        eventsArea.setWrapStyleWord(true);
        
        JScrollPane eventsScroll = new JScrollPane(eventsArea);
        eventsScroll.setPreferredSize(new Dimension(1200, 380));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(eventsScroll);

        // Close: szary → zielony po 90'
        closeButton = new JButton("Oczekiwanie na koniec meczu...");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setEnabled(false);
        closeButton.setForeground(Color.GRAY);
        closeButton.addActionListener(e -> closeMatch());
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(closeButton);

        JScrollPane mainScroll = new JScrollPane(contentPanel);
        add(mainScroll);
    }

    /**
     * Timer 800ms: next event → update minuta + text + scroll
     */
    private void startLiveAnimation() {
        swingTimer = new javax.swing.Timer(800, e -> {
            List<MatchSimulator.MatchEvent> events = simulator.getMatchEvents();
            if (currentEventIndex < events.size()) {
                MatchSimulator.MatchEvent event = events.get(currentEventIndex);
                minuteLabel.setText(event.minute + "'");                    // Update minuta
                eventsArea.append(event.getEventDisplay() + "\n");          // Append event
                eventsArea.setCaretPosition(eventsArea.getDocument().getLength()); // Auto-scroll
                currentEventIndex++;
            } else {
                enableCloseButton();                                        // Aktywuj close
                swingTimer.stop();                                          // Stop timer
                
                simulateOtherMatches();                                     // Symuluj resztę rundy
            }
        });
        swingTimer.setInitialDelay(500);    // Pierwszy po 0.5s
        swingTimer.start();
    }

    /**
     * Close: zielony + "Zamknij i wróć"
     */
    private void enableCloseButton() {
        closeButton.setEnabled(true);
        closeButton.setText("Zamknij i wróć do gry");
        closeButton.setForeground(Color.GREEN);
    }

    /**
     * Zapis wyniku + dispose
     */
    private void closeMatch() {
        if (swingTimer != null) swingTimer.stop();
        recordMatchResult();
        dispose();
    }

    /**
     * Zapisuje bramki do TeamStats (home/away)
     */
    private void recordMatchResult() {
        int homeGoals = simulator.getGoalsHome();
        int awayGoals = simulator.getGoalsAway();
        
        System.out.println("📊 Zapisywanie wyniku: " + homeTeamName + " " + homeGoals + "-" + awayGoals + " " + awayTeamName);
        
        homeTeamStats.recordMatch(homeGoals, awayGoals);   // Home stats
        awayTeamStats.recordMatch(awayGoals, homeGoals);   // Away stats
        
        System.out.println("✅ " + homeTeamName + ": " + homeTeamStats.getMatches() + 
                          " meczów, " + homeTeamStats.getPoints() + " pkt, " + 
                          homeTeamStats.getGoalsFor() + "-" + homeTeamStats.getGoalsAgainst() + " bramki");
        System.out.println("✅ " + awayTeamName + ": " + awayTeamStats.getMatches() + 
                          " meczów, " + awayTeamStats.getPoints() + " pkt, " + 
                          awayTeamStats.getGoalsFor() + "-" + awayTeamStats.getGoalsAgainst() + " bramki");
    }

    /**
     * Symuluje pozostałe mecze rundy (pomija gracza)
     */
    private void simulateOtherMatches() {
        if (otherMatches == null || otherMatches.isEmpty()) return;

        System.out.println("\n🎮 Symulowanie pozostałych meczów w turze...");
        Random rand = new Random();

        for (MatchManager.Match match : otherMatches) {
            if (match.played) continue; // Już rozegrany

            // Pomija mecz gracza
            if (match.homeTeam.equals(homeTeamName) || match.awayTeam.equals(homeTeamName)) {
                System.out.println("⏭️ Pomijam mecz gracza: " + match.homeTeam + " vs " + match.awayTeam);
                continue;
            }

            TeamStats homeTeam = leagueManager.getTeam(match.homeTeam);
            TeamStats awayTeam = leagueManager.getTeam(match.awayTeam);
            if (homeTeam == null || awayTeam == null) continue;

            // Szansa gola wg siły (2.0 ± różnica*0.015, max 0.5-4.0)
            int homeStrength = homeTeam.getStrength();
            int awayStrength = awayTeam.getStrength();
            double homeChance = 2.0 + (homeStrength - awayStrength) * 0.015;
            double awayChance = 2.0 + (awayStrength - homeStrength) * 0.015;
            homeChance = Math.max(0.5, Math.min(4.0, homeChance));
            awayChance = Math.max(0.5, Math.min(4.0, awayChance));

            // Gole: średnia + los ±0.5
            int homeGoals = (int) Math.round(homeChance + (rand.nextDouble() - 0.5));
            int awayGoals = (int) Math.round(awayChance + (rand.nextDouble() - 0.5));
            homeGoals = Math.max(0, homeGoals);
            awayGoals = Math.max(0, awayGoals);

            // Zapis
            homeTeam.recordMatch(homeGoals, awayGoals);
            awayTeam.recordMatch(awayGoals, homeGoals);
            match.goalsHome = homeGoals;
            match.goalsAway = awayGoals;
            match.played = true;

            System.out.println("⚽ " + match.homeTeam + " " + homeGoals + "-" + awayGoals + " " + match.awayTeam);
        }
        System.out.println("✅ Wszystkie mecze tury symulowane!\n");
    }

    // Gettery wyniku (do GamePanelCustom)
    public int getFinalGoalsHome() { return simulator.getGoalsHome(); }
    public int getFinalGoalsAway() { return simulator.getGoalsAway(); }
    public TeamStats getHomeTeamStats() { return homeTeamStats; }
    public TeamStats getAwayTeamStats() { return awayTeamStats; }
}
