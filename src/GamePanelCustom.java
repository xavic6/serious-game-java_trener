package trenergame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;
import java.util.Random;
import java.io.*;
import trenergame.SaveLoadManager;
import trenergame.GameState;

public class GamePanelCustom extends JPanel {
    private static final String[] KOLUMNY = {
            "Imię i nazwisko", "Morale", "Relacje z trenerem", "Pozycja", "Wiek",
            "Umiejętności", "Pozycja w szatni", "Forma", "Kondycja", "Dyscyplina",
            "Doświadczenie", "Popularność", "Um. dowódcze", "Kontuzje"
    };

    private JTable tabelaPilkarzy;
    private Pilkarz[] pilkarze;
    private JTable tabelaLiga;
    public String[][] daneLiga;
    public JTextArea poleEvent;
    public int gameDay = 1;
    public int matchesPlayed = 0;
    public boolean hasSpokenToday = false;
    private JProgressBar barZarzad, barAtmosfera, barPoparcie, barForma, barSezon;
    private JLabel labelSezon;
    private Random random = new Random();
    public int formaDruzyny = 50;
    public String teamName;

    public MatchManager matchManager;
    private JTextArea lblNextMatch;
    public LeagueManager leagueManager;

    private static class NumericComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            try {
                double num1 = Double.parseDouble(o1.toString().trim());
                double num2 = Double.parseDouble(o2.toString().trim());
                return Double.compare(num1, num2);
            } catch (NumberFormatException e) {
                return o1.toString().compareTo(o2.toString());
            }
        }
    }

    private static class BramkiComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            try {
                String s1 = o1.toString().trim();
                String s2 = o2.toString().trim();
                int num1 = Integer.parseInt(s1.replace("+", ""));
                int num2 = Integer.parseInt(s2.replace("+", ""));
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                return o1.toString().compareTo(o2.toString());
            }
        }
    }

    private static class TextComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }
    }

    public GamePanelCustom(Runnable onMenu, Runnable onSave, Runnable onNext, String teamName, String[][] customLeagueData, Pilkarz[] pilkarze) {
        this.teamName = teamName;
        this.daneLiga = customLeagueData;
        this.pilkarze = pilkarze;
        
        this.matchManager = new MatchManager();
        
        String[] teamNames = new String[customLeagueData.length];
        int[] teamStrengths = new int[customLeagueData.length];
        for (int i = 0; i < customLeagueData.length; i++) {
            teamNames[i] = customLeagueData[i][1].toString();
            teamStrengths[i] = Integer.parseInt(customLeagueData[i][5].toString());
        }
        this.leagueManager = new LeagueManager(teamNames, teamStrengths);
        
        initializeMatches();
        init(onMenu, onSave, onNext);
    }

    private void initializeMatches() {
        String[] teamNames = new String[daneLiga.length];
        for (int i = 0; i < daneLiga.length; i++) {
            teamNames[i] = daneLiga[i][1].toString();
        }

        int[][] schedule1 = {
            {0, 1}, {2, 9}, {3, 8}, {4, 7}, {5, 6},
            {0, 2}, {1, 9}, {3, 7}, {4, 8}, {5, 6},
            {0, 3}, {1, 2}, {4, 9}, {5, 8}, {6, 7},
            {0, 4}, {1, 3}, {2, 9}, {5, 7}, {6, 8},
            {0, 5}, {1, 4}, {2, 3}, {6, 9}, {7, 8},
            {0, 6}, {1, 5}, {2, 4}, {3, 9}, {7, 8},
            {0, 7}, {1, 6}, {2, 5}, {3, 4}, {8, 9},
            {0, 8}, {1, 7}, {2, 6}, {3, 5}, {4, 9},
            {0, 9}, {1, 8}, {2, 7}, {3, 6}, {4, 5}
        };

        int[][] schedule2 = {
            {1, 0}, {9, 2}, {8, 3}, {7, 4}, {6, 5},
            {2, 0}, {9, 1}, {7, 3}, {8, 4}, {6, 5},
            {3, 0}, {2, 1}, {9, 4}, {8, 5}, {7, 6},
            {4, 0}, {3, 1}, {9, 2}, {7, 5}, {8, 6},
            {5, 0}, {4, 1}, {3, 2}, {9, 6}, {8, 7},
            {6, 0}, {5, 1}, {4, 2}, {9, 3}, {8, 7},
            {7, 0}, {6, 1}, {5, 2}, {4, 3}, {9, 8},
            {8, 0}, {7, 1}, {6, 2}, {5, 3}, {9, 4},
            {9, 0}, {8, 1}, {7, 2}, {6, 3}, {5, 4}
        };

        int myIdx = findTeamIndex(teamName, teamNames);

        for (int round = 0; round < 9; round++) {
            for (int i = 0; i < 5; i++) {
                int homeIdx = schedule1[round * 5 + i][0];
                int awayIdx = schedule1[round * 5 + i][1];

                MatchManager.Match match = new MatchManager.Match(
                    teamNames[homeIdx], teamNames[awayIdx], round + 1,
                    null, null
                );
                matchManager.addMatch(match);
            }
        }

        for (int round = 0; round < 9; round++) {
            for (int i = 0; i < 5; i++) {
                int homeIdx = schedule2[round * 5 + i][0];
                int awayIdx = schedule2[round * 5 + i][1];

                MatchManager.Match match = new MatchManager.Match(
                    teamNames[homeIdx], teamNames[awayIdx], round + 10,
                    null, null
                );
                matchManager.addMatch(match);
            }
        }
    }

    private int findTeamIndex(String teamName, String[] teamNames) {
        for (int i = 0; i < teamNames.length; i++) {
            if (teamNames[i].equals(teamName)) {
                return i;
            }
        }
        return -1;
    }

    // ✅ ROZMOWA PRZEDMECZOWA - 16 OPCJI
    private void showPreMatchTalk(MatchManager.Match match) {
        String[] talkOptions = {
            "Inspirująca mowa", "Wiara w zespół", "Oczekuję zwycięstwa",
            "Agresywna taktyka", "Defensywna postawa", "Zbalansowane wytyczne",
            "Relaksująca pogawędka", "Kontynuujcie tak",
            "Solidna obrona", "Kontrola środka",
            "Surowa krytyka", "Zbyt duże ciśnienie", "Publiczne upomnienie",
            "Zbyt luźne podejście", "Winę na nich", "Ignorowanie problemów",
            "Nie romawiaj"
        };

        int choice = JOptionPane.showOptionDialog(
            this,
            "ROZMOWA PRZEDMECZOWA\n\nJutra grasz z: " + match.awayTeam + "\n\nJak chcesz zmotywować drużynę?",
            "Przygotowanie do meczu",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            talkOptions,
            1
        );

        if (choice >= 0 && choice < talkOptions.length - 1) {
            StringBuilder effects = new StringBuilder("Efekty rozmowy przedmeczowej:\n\n");
            
            switch (choice) {
                case 0:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            p.setMorale(Math.min(100, p.getMorale() + 15));
                            if (p.getPozycjaWSzatni().equals("Junior")) {
                                p.setMorale(Math.max(0, p.getMorale() - 5));
                            }
                        }
                    }
                    formaDruzyny = Math.min(100, formaDruzyny + 8);
                    effects.append("Inspirująca mowa:\n+ Morale: +15\n+ Forma: +8\n- Junior: -5");
                    break;
                case 1:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            p.setMorale(Math.min(100, p.getMorale() + 12));
                            p.setRelacjeZTrenerem(Math.min(100, p.getRelacjeZTrenerem() + 10));
                        }
                    }
                    effects.append("Wiara w zespół:\n+ Morale: +12\n+ Relacje: +10");
                    break;
                case 2:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            if (p.getPozycja().contains("Napastnik")) {
                                p.setMorale(Math.min(100, p.getMorale() + 10));
                            } else if (p.getPozycja().contains("Obrońca")) {
                                p.setMorale(Math.max(0, p.getMorale() - 5));
                            }
                        }
                    }
                    formaDruzyny = Math.min(100, formaDruzyny + 10);
                    effects.append("Oczekuję zwycięstwa:\n+ Forma: +10\n- Obrona: -5");
                    break;
                case 3:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            if (p.getPozycja().contains("Napastnik")) {
                                p.setMorale(Math.min(100, p.getMorale() + 13));
                            } else if (p.getPozycja().contains("Obrońca")) {
                                p.setMorale(Math.max(0, p.getMorale() - 8));
                            }
                        }
                    }
                    formaDruzyny = Math.min(100, formaDruzyny + 10);
                    effects.append("Agresywna taktyka:\n+ Napastnicy: +13\n+ Forma: +10\n- Obrońcy: -8");
                    break;
                case 4:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            if (p.getPozycja().contains("Obrońca")) {
                                p.setMorale(Math.min(100, p.getMorale() + 13));
                            }
                        }
                    }
                    formaDruzyny = Math.min(100, formaDruzyny + 8);
                    effects.append("Defensywna postawa:\n+ Obrona: +13\n+ Forma: +8");
                    break;
                case 5:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            p.setMorale(Math.min(100, p.getMorale() + 4));
                        }
                    }
                    formaDruzyny = Math.min(100, formaDruzyny + 6);
                    effects.append("Zbalansowane:\n+ Forma: +6\n+ Morale: +4");
                    break;
                case 6:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            p.setMorale(Math.min(100, p.getMorale() + 10));
                        }
                    }
                    effects.append("Relaksująca pogawędka:\n+ Morale: +10");
                    break;
                case 7:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            p.setMorale(Math.min(100, p.getMorale() + 5));
                        }
                    }
                    effects.append("Kontynuujcie tak:\n+ Morale: +5");
                    break;
                case 8:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            if (p.getPozycja().contains("Obrońca")) {
                                p.setMorale(Math.min(100, p.getMorale() + 12));
                            }
                        }
                    }
                    effects.append("Solidna obrona:\n+ Obrona: +12");
                    break;
                case 9:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            if (p.getPozycja().contains("Pomocnik")) {
                                p.setMorale(Math.min(100, p.getMorale() + 10));
                            }
                        }
                    }
                    formaDruzyny = Math.min(100, formaDruzyny + 10);
                    effects.append("Kontrola środka:\n+ Midfield: +10\n+ Forma: +10");
                    break;
                case 10:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            if (p.getUmiejetnosci() < 50) {
                                p.setMorale(Math.max(0, p.getMorale() - 15));
                            }
                        }
                    }
                    formaDruzyny = Math.max(0, formaDruzyny - 10);
                    effects.append("Surowa krytyka:\n- Słabi: -15\n- Forma: -10");
                    break;
                case 11:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            p.setMorale(Math.max(0, p.getMorale() - 12));
                            p.setRelacjeZTrenerem(Math.max(0, p.getRelacjeZTrenerem() - 10));
                        }
                    }
                    formaDruzyny = Math.max(0, formaDruzyny - 8);
                    effects.append("Zbyt duże ciśnienie:\n- Morale: -12\n- Relacje: -10");
                    break;
                case 12:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje() && p.getForma() < 40) {
                            p.setMorale(Math.max(0, p.getMorale() - 20));
                        }
                    }
                    formaDruzyny = Math.max(0, formaDruzyny - 5);
                    effects.append("Publiczne upomnienie:\n- Słabi: -20\n⚠️ Ryzyko!");
                    break;
                case 13:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            if (p.getDyscyplina() < 50) {
                                p.setMorale(Math.min(100, p.getMorale() + 5));
                            } else {
                                p.setMorale(Math.max(0, p.getMorale() - 10));
                            }
                        }
                    }
                    formaDruzyny = Math.max(0, formaDruzyny - 7);
                    effects.append("Zbyt luźne:\n- Forma: -7\n+ Rozluźnieni: +5");
                    break;
                case 14:
                    for (Pilkarz p : pilkarze) {
                        if (!p.isKontuzje()) {
                            p.setRelacjeZTrenerem(Math.max(0, p.getRelacjeZTrenerem() - 15));
                            p.setMorale(Math.max(0, p.getMorale() - 10));
                        }
                    }
                    effects.append("Winę na nich:\n- Relacje: -15\n⚠️ Ryzyko odejść!");
                    break;
                case 15:
                    formaDruzyny = Math.max(0, formaDruzyny - 5);
                    effects.append("Ignorowanie:\n⚠️ Brak efektów\n- Forma: -5");
                    break;
            }

            hasSpokenToday = true;
            refreshPlayerTable();
            updateProgressBars();
            poleEvent.setText(effects.toString());
        }
    }

    private void showPostMatchTalk(MatchManager.Match lastMatch) {
        boolean weWon = false;
        String homeTeamForDisplay = lastMatch.homeTeam;
        boolean isHomeTeam = homeTeamForDisplay.equals(teamName);
        
        if (isHomeTeam) {
            weWon = lastMatch.goalsHome > lastMatch.goalsAway;
        } else {
            weWon = lastMatch.goalsAway > lastMatch.goalsHome;
        }
        
        boolean isDraw = lastMatch.goalsHome == lastMatch.goalsAway;

        String[] talkOptions;
        String title;
        
        if (weWon) {
            talkOptions = new String[] {
                "Świetna gra, tak trzymać!",
                "Czemu nie byliśmy bardziej agresywni?",
                "Obrona była słaba",
                "Nie romawiaj"
            };
            title = "ROZMOWA POMECZOWA - Zwycięstwo!";
        } else if (isDraw) {
            talkOptions = new String[] {
                "Mogliśmy wygrać",
                "Dobry rezultat",
                "Za mało agresji",
                "Nie romawiaj"
            };
            title = "ROZMOWA POMECZOWA - Remis";
        } else {
            talkOptions = new String[] {
                "Wszyscy zawinili",
                "Lepiej następnym razem",
                "Zbyt słaba obrona",
                "Nie romawiaj"
            };
            title = "ROZMOWA POMECZOWA - Porażka";
        }

        int choice = JOptionPane.showOptionDialog(
            this,
            title + "\n\n" + lastMatch.getResult() + "\n\nCo chcesz powiedzieć drużynie?",
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            talkOptions,
            0
        );

        if (choice >= 0 && choice < talkOptions.length - 1) {
            StringBuilder effects = new StringBuilder("Efekty rozmowy pomeczowej:\n\n");
            
            if (weWon) {
                switch (choice) {
                    case 0:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                p.setMorale(Math.min(100, p.getMorale() + 8));
                                p.setRelacjeZTrenerem(Math.min(100, p.getRelacjeZTrenerem() + 4));
                            }
                        }
                        effects.append("Drużyna czuje się doceniana!\n+ Morale: +8\n+ Relacje: +4");
                        break;
                    case 1:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                p.setMorale(Math.max(0, p.getMorale() - 3));
                            }
                        }
                        effects.append("Drużyna czuje się skrytykowana\n- Morale: -3");
                        break;
                    case 2:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                if (p.getPozycja().contains("Obrońca")) {
                                    p.setMorale(Math.max(0, p.getMorale() - 5));
                                } else {
                                    p.setMorale(Math.min(100, p.getMorale() + 3));
                                }
                            }
                        }
                        effects.append("Obrońcy czują się źle\n- Morale obrońców: -5");
                        break;
                }
            } else if (isDraw) {
                switch (choice) {
                    case 0:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                p.setMorale(Math.max(0, p.getMorale() - 4));
                            }
                        }
                        effects.append("Drużyna rozczarowana\n- Morale: -4");
                        break;
                    case 1:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                p.setMorale(Math.min(100, p.getMorale() + 5));
                                p.setRelacjeZTrenerem(Math.min(100, p.getRelacjeZTrenerem() + 3));
                            }
                        }
                        effects.append("Drużyna zadowolona\n+ Morale: +5\n+ Relacje: +3");
                        break;
                    case 2:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                p.setMorale(Math.max(0, p.getMorale() - 3));
                            }
                        }
                        effects.append("Drużyna przyznaje rację\n- Morale: -3");
                        break;
                }
            } else {
                switch (choice) {
                    case 0:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                p.setMorale(Math.max(0, p.getMorale() - 8));
                            }
                        }
                        effects.append("Drużyna zniszczona\n- Morale: -8");
                        break;
                    case 1:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                p.setMorale(Math.max(0, p.getMorale() - 2));
                                p.setRelacjeZTrenerem(Math.min(100, p.getRelacjeZTrenerem() + 3));
                            }
                        }
                        effects.append("Drużyna zmotywowana\n- Morale: -2\n+ Relacje: +3");
                        break;
                    case 2:
                        for (Pilkarz p : pilkarze) {
                            if (!p.isKontuzje()) {
                                if (p.getPozycja().contains("Obrońca")) {
                                    p.setMorale(Math.max(0, p.getMorale() - 10));
                                } else {
                                    p.setMorale(Math.max(0, p.getMorale() - 3));
                                }
                            }
                        }
                        effects.append("Obrońcy czują się winni\n- Morale obrońców: -10");
                        break;
                }
            }

            hasSpokenToday = true;
            refreshPlayerTable();
            updateProgressBars();
            poleEvent.setText(effects.toString());
        }
    }

    private void init(Runnable onMenu, Runnable onSave, Runnable onNext) {
        setLayout(null);
        setPreferredSize(new Dimension(1280, 1024));

        barZarzad = new JProgressBar(0, 100);
        barZarzad.setStringPainted(true);
        barAtmosfera = new JProgressBar(0, 100);
        barAtmosfera.setStringPainted(true);
        barPoparcie = new JProgressBar(0, 100);
        barPoparcie.setStringPainted(true);
        barForma = new JProgressBar(0, 100);
        barForma.setStringPainted(true);
        barSezon = new JProgressBar(0, 100);
        barSezon.setStringPainted(true);
        barSezon.setString("0%");
        barSezon.setForeground(Color.CYAN.darker());

        JLabel labelZarzad = new JLabel("Zadowolenie zarządu", SwingConstants.CENTER);
        JLabel labelAtmosfera = new JLabel("Atmosfera w szatni", SwingConstants.CENTER);
        JLabel labelPoparcie = new JLabel("Poparcie trenera", SwingConstants.CENTER);
        JLabel labelForma = new JLabel("Forma drużyny", SwingConstants.CENTER);

        int barWidth = 280, barHeight = 30, barGap = 20, topMargin = 18;

        labelZarzad.setBounds(10, topMargin, barWidth, 20);
        barZarzad.setBounds(10, topMargin + 20, barWidth, barHeight);
        labelAtmosfera.setBounds(10 + barWidth + barGap, topMargin, barWidth, 20);
        barAtmosfera.setBounds(10 + barWidth + barGap, topMargin + 20, barWidth, barHeight);
        labelPoparcie.setBounds(10 + 2 * (barWidth + barGap), topMargin, barWidth, 20);
        barPoparcie.setBounds(10 + 2 * (barWidth + barGap), topMargin + 20, barWidth, barHeight);
        labelForma.setBounds(10 + 3 * (barWidth + barGap), topMargin, barWidth, 20);
        barForma.setBounds(10 + 3 * (barWidth + barGap), topMargin + 20, barWidth, barHeight);

        add(labelZarzad);
        add(barZarzad);
        add(labelAtmosfera);
        add(barAtmosfera);
        add(labelPoparcie);
        add(barPoparcie);
        add(labelForma);
        add(barForma);

        tabelaPilkarzy = createSortablePlayerTable();
        JScrollPane scrollPanePilkarzy = new JScrollPane(tabelaPilkarzy);
        scrollPanePilkarzy.setBounds(10, 80, 1245, 320);
        add(scrollPanePilkarzy);

        JButton btnInterakcja = new JButton("Interakcja z piłkarzem");
        btnInterakcja.setBounds(10, 410, 260, 36);
        add(btnInterakcja);

        btnInterakcja.addActionListener(e -> {
            MatchManager.Match tomorrowMatch = matchManager.getTodayMatch(gameDay + 1);
            MatchManager.Match yesterdayMatch = matchManager.getTodayMatch(gameDay - 1);
            
            if (tomorrowMatch != null && !tomorrowMatch.played) {
                showPreMatchTalk(tomorrowMatch);
            } else if (yesterdayMatch != null && yesterdayMatch.played && !hasSpokenToday) {
                showPostMatchTalk(yesterdayMatch);
            } else {
                int selectedRow = tabelaPilkarzy.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(this, "Najpierw wybierz piłkarza z tabeli.");
                    return;
                }
                if (hasSpokenToday) {
                    JOptionPane.showMessageDialog(this, "Dzisiaj już prowadziłeś rozmowę.");
                    return;
                }

                int playerIndex = tabelaPilkarzy.convertRowIndexToModel(selectedRow);
                Pilkarz p = pilkarze[playerIndex];
                String[] options = p.getDialogOptions();
                int choice = JOptionPane.showOptionDialog(
                        this,
                        "Jak chcesz porozmawiać z " + p.getImieINazwisko() + "?",
                        "Rozmowa z piłkarzem",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice >= 0) {
                    int oldMorale = p.getMorale();
                    int oldRel = p.getRelacjeZTrenerem();
                    p.applyDialogEffect(choice);
                    hasSpokenToday = true;
                    refreshPlayerTable();

                    StringBuilder feedback = new StringBuilder("Rozmowa z " + p.getImieINazwisko() + ":\n");
                    if (p.getMorale() != oldMorale) {
                        int diff = p.getMorale() - oldMorale;
                        feedback.append(String.format("Morale: %s%d\n", diff > 0 ? "+" : "", diff));
                    }
                    if (p.getRelacjeZTrenerem() != oldRel) {
                        int diff = p.getRelacjeZTrenerem() - oldRel;
                        feedback.append(String.format("Relacje: %s%d\n", diff > 0 ? "+" : "", diff));
                    }

                    poleEvent.setText(feedback.toString());
                    updateProgressBars();
                }
            }
        });

        JButton btnKalendarz = new JButton("📅 Kalendarz");
        btnKalendarz.setBounds(280, 410, 120, 36);
        btnKalendarz.setFont(new Font("Arial", Font.BOLD, 14));
        btnKalendarz.addActionListener(e -> openCalendarWindow());
        add(btnKalendarz);

        lblNextMatch = new JTextArea("Następny mecz\nBrak zaplanowanych");
        lblNextMatch.setFont(new Font("Arial", Font.BOLD, 14));
        lblNextMatch.setEditable(false);
        lblNextMatch.setLineWrap(true);
        lblNextMatch.setWrapStyleWord(true);
        lblNextMatch.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblNextMatch.setBounds(980, 410, 260, 60);
        add(lblNextMatch);
        
        updateNextMatchDisplay();

        String[] kolumnyLiga = {"Miejsce", "Nazwa", "Mecze", "Punkty", "Bramki", "Siła"};
        DefaultTableModel ligaModel = new DefaultTableModel(daneLiga, kolumnyLiga) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaLiga = new JTable(ligaModel);
        tabelaLiga.setFont(new Font("Arial", Font.PLAIN, 13));
        tabelaLiga.setRowHeight(21);
        tabelaLiga.setAutoCreateRowSorter(true);

        TableRowSorter<DefaultTableModel> ligaSorter = new TableRowSorter<>(ligaModel);
        tabelaLiga.setRowSorter(ligaSorter);

        ligaSorter.setComparator(0, new NumericComparator());
        ligaSorter.setComparator(1, new TextComparator());
        ligaSorter.setComparator(2, new NumericComparator());
        ligaSorter.setComparator(3, new NumericComparator());
        ligaSorter.setComparator(4, new BramkiComparator());
        ligaSorter.setComparator(5, new NumericComparator());

        ligaSorter.setSortKeys(java.util.List.of(
                new javax.swing.RowSorter.SortKey(0, javax.swing.SortOrder.ASCENDING)
        ));
        ligaSorter.sort();

        JScrollPane scrollPaneLiga = new JScrollPane(tabelaLiga);
        scrollPaneLiga.setBounds(980, 480, 300, 235);
        add(scrollPaneLiga);

        // ✅ SEZON PASEK - nowy
        labelSezon = new JLabel("Sezon: 0/18");
        labelSezon.setBounds(980, 725, 140, 20);
        labelSezon.setFont(new Font("Arial", Font.BOLD, 12));
        add(labelSezon);

        barSezon.setBounds(980, 747, 300, 22);
        add(barSezon);

        poleEvent = new JTextArea("Ostatnie wydarzenia: rozgrywka rozpoczęta.");
        poleEvent.setEditable(false);
        poleEvent.setFont(new Font("Arial", Font.PLAIN, 15));
        poleEvent.setLineWrap(true);
        poleEvent.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        poleEvent.setBounds(10, 900, 1230, 70);
        add(poleEvent);

        JButton btnDalej = new JButton("DALEJ");
        btnDalej.setFont(new Font("Arial", Font.BOLD, 32));
        btnDalej.setBounds(10, 800, 220, 60);
        btnDalej.addActionListener(e -> {
            gameDay++;
            
            MatchManager.Match todayMatch = matchManager.getTodayMatch(gameDay);
            
            if (todayMatch != null) {
                final JDialog teamSelectDialog = new JDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this),
                        "Wybór składu przed meczem",
                        true
                );
                teamSelectDialog.setSize(1200, 900);
                teamSelectDialog.setLocationRelativeTo(this);
                teamSelectDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                final MatchManager.Match finalMatch = todayMatch;
                final TeamSelectionPanel selectionPanel = new TeamSelectionPanel(pilkarze, () -> {
                    Thread matchThread = new Thread(() -> {
                        java.util.List<MatchManager.Match> roundMatches = matchManager.getMatchesByRound(finalMatch.round);
                        
                        MatchViewWindowLive matchWindow = new MatchViewWindowLive(
                            (JFrame) SwingUtilities.getWindowAncestor(GamePanelCustom.this),
                            finalMatch.homeTeam,
                            finalMatch.awayTeam,
                            pilkarze,
                            new Pilkarz[0],
                            leagueManager.getTeam(finalMatch.homeTeam),
                            leagueManager.getTeam(finalMatch.awayTeam),
                            leagueManager,
                            roundMatches
                        );

                        SwingUtilities.invokeLater(() -> {
                            finalMatch.goalsHome = matchWindow.getFinalGoalsHome();
                            finalMatch.goalsAway = matchWindow.getFinalGoalsAway();
                            finalMatch.played = true;
                            matchesPlayed++; // ✅ ZWIĘKSZAMY LICZNIK

                            poleEvent.setText("MECZ ROZEGRANY!\n" + finalMatch.getResult());
                            
                            Object[][] newLeagueData = leagueManager.getTableData();
                            DefaultTableModel model = new DefaultTableModel(newLeagueData, new String[]{"Miejsce", "Nazwa", "Mecze", "Punkty", "Bramki", "Siła"}) {
                                @Override
                                public boolean isCellEditable(int row, int column) {
                                    return false;
                                }
                            };
                            tabelaLiga.setModel(model);
                            tabelaLiga.repaint();

                            teamSelectDialog.dispose();

                            String homeTeamForDisplay = finalMatch.homeTeam;
                            boolean isHomeTeam = homeTeamForDisplay.equals(teamName);
                            
                            boolean weWon = isHomeTeam ? 
                                (finalMatch.goalsHome > finalMatch.goalsAway) : 
                                (finalMatch.goalsAway > finalMatch.goalsHome);
                            boolean isDraw = finalMatch.goalsHome == finalMatch.goalsAway;

                            if (weWon) {
                                formaDruzyny += 12 + random.nextInt(9);
                            } else if (isDraw) {
                                formaDruzyny += 2 + random.nextInt(5);
                            } else {
                                formaDruzyny -= (8 + random.nextInt(7));
                            }
                            formaDruzyny = Math.max(5, Math.min(95, formaDruzyny));

                            for (Pilkarz p : pilkarze) {
                                if (!p.isKontuzje()) {
                                    int zmiana = -3 + random.nextInt(7);
                                    p.setMorale(Math.max(0, Math.min(100, p.getMorale() + zmiana)));
                                }
                            }

                            updateProgressBars();
                            refreshPlayerTable();
                            updateNextMatchDisplay();
                            updateSeasonProgress(); // ✅ AKTUALIZUJEMY PASEK

                            onNext.run();
                        });
                    });
                    
                    matchThread.start();
                });

                teamSelectDialog.add(selectionPanel);
                teamSelectDialog.setVisible(true);
            } else {
                poleEvent.setText("Dzień " + gameDay + " - brak meczów.");
            }
            
            hasSpokenToday = false;
        });
        add(btnDalej);

        JButton btnMenu = new JButton("Wróć do menu");
        btnMenu.setBounds(1080, 800, 170, 40);
        btnMenu.addActionListener(e -> onMenu.run());
        add(btnMenu);

        JButton btnZapisz = new JButton("Zapisz grę");
        btnZapisz.setBounds(880, 800, 170, 40);
        btnZapisz.addActionListener(e -> {
            GameState state = new GameState(
                gameDay, matchesPlayed, formaDruzyny, teamName,
                hasSpokenToday, pilkarze, matchManager, leagueManager, daneLiga
            );
            SaveLoadManager.showSaveDialog(state);
        });
        add(btnZapisz);


        updateProgressBars();
    }

    // ✅ AKTUALIZACJA PASKA SEZONU
    private void updateSeasonProgress() {
        int percentProgress = Math.round((matchesPlayed * 100.0f) / 18.0f);
        percentProgress = Math.min(100, percentProgress);
        barSezon.setValue(percentProgress);
        barSezon.setString(percentProgress + "%");
        labelSezon.setText("Sezon: " + matchesPlayed + "/18");
    }

    private void updateNextMatchDisplay() {
        MatchManager.Match nextMatch = matchManager.getNextMatch(gameDay);
        MatchManager.Match tomorrowMatch = matchManager.getTodayMatch(gameDay + 1);
        MatchManager.Match yesterdayMatch = matchManager.getTodayMatch(gameDay - 1);
        
        StringBuilder info = new StringBuilder();
        
        if (nextMatch != null) {
            info.append("Następny mecz\n").append(nextMatch.homeTeam).append(" vs ").append(nextMatch.awayTeam)
                .append("\nDzień ").append(nextMatch.dayScheduled);
        } else {
            info.append("Następny mecz\nSezon zakończony!");
        }
        
        if (tomorrowMatch != null && !tomorrowMatch.played) {
            info.append("\n\nMożna rozmowę przedmeczową");
        }
        
        if (yesterdayMatch != null && yesterdayMatch.played) {
            info.append("\n\nMożna rozmowę pomeczową");
        }
        
        lblNextMatch.setText(info.toString());
    }

    private void openCalendarWindow() {
        JDialog calendarDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Kalendarz", true);
        calendarDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        calendarDialog.setSize(1000, 700);
        calendarDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        int currentRound = ((gameDay - 14) / 7) + 1;
        currentRound = Math.max(1, Math.min(18, currentRound));
        final int[] displayRound = {currentRound};

        JButton btnPrevRound = new JButton("← Poprzednia kolejka");
        JLabel lblRound = new JLabel("Kolejka " + displayRound[0] + "/18");
        lblRound.setFont(new Font("Arial", Font.BOLD, 18));
        JButton btnNextRound = new JButton("Następna kolejka →");

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Tabela ligi"));

        String[] kolumnyLiga = {"Miejsce", "Nazwa", "Mecze", "Punkty", "Bramki", "Siła"};
        DefaultTableModel tableModel = new DefaultTableModel(leagueManager.getTableData(), kolumnyLiga) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tableLiga = new JTable(tableModel);
        tableLiga.setFont(new Font("Arial", Font.PLAIN, 12));
        tableLiga.setRowHeight(20);
        tableLiga.setAutoCreateRowSorter(true);

        TableRowSorter<DefaultTableModel> ligaSorter = new TableRowSorter<>(tableModel);
        tableLiga.setRowSorter(ligaSorter);

        ligaSorter.setComparator(0, new NumericComparator());
        ligaSorter.setComparator(1, new TextComparator());
        ligaSorter.setComparator(2, new NumericComparator());
        ligaSorter.setComparator(3, new NumericComparator());
        ligaSorter.setComparator(4, new BramkiComparator());
        ligaSorter.setComparator(5, new NumericComparator());

        ligaSorter.setSortKeys(java.util.List.of(
                new javax.swing.RowSorter.SortKey(0, javax.swing.SortOrder.ASCENDING)
        ));
        ligaSorter.sort();

        JScrollPane tableScroll = new JScrollPane(tableLiga);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        btnPrevRound.addActionListener(e -> {
            if (displayRound[0] > 1) {
                displayRound[0]--;
                lblRound.setText("Kolejka " + displayRound[0] + "/18");
                updateCalendarMatches(contentPanel, displayRound[0]);
            }
        });

        btnNextRound.addActionListener(e -> {
            if (displayRound[0] < 18) {
                displayRound[0]++;
                lblRound.setText("Kolejka " + displayRound[0] + "/18");
                updateCalendarMatches(contentPanel, displayRound[0]);
            }
        });

        topPanel.add(btnPrevRound);
        topPanel.add(lblRound);
        topPanel.add(btnNextRound);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        JPanel matchesPanel = new JPanel();
        matchesPanel.setLayout(new BoxLayout(matchesPanel, BoxLayout.Y_AXIS));
        matchesPanel.setBorder(BorderFactory.createTitledBorder("Mecze kolejki " + displayRound[0]));

        updateCalendarMatches(contentPanel, displayRound[0]);

        JScrollPane matchesScroll = new JScrollPane(matchesPanel);
        contentPanel.add(matchesScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, contentPanel, tablePanel);
        splitPane.setDividerLocation(400);

        calendarDialog.add(splitPane);
        calendarDialog.setVisible(true);
    }

    private void updateCalendarMatches(JPanel contentPanel, int round) {
        java.util.List<MatchManager.Match> roundMatches = matchManager.getMatchesByRound(round);
        
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                JPanel matchesPanel = (JPanel) scroll.getViewport().getView();
                matchesPanel.removeAll();
                
                for (MatchManager.Match match : roundMatches) {
                    JPanel matchPanel = new JPanel();
                    matchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));
                    matchPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    matchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                    if (match.played) {
                        JLabel resultLabel = new JLabel(match.getResult());
                        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
                        resultLabel.setForeground(new Color(0, 100, 0));
                        matchPanel.add(resultLabel);
                    } else {
                        JLabel homeLabel = new JLabel(match.homeTeam);
                        homeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        matchPanel.add(homeLabel);
                        
                        JLabel vsLabel = new JLabel(" vs ");
                        vsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        matchPanel.add(vsLabel);
                        
                        JLabel awayLabel = new JLabel(match.awayTeam);
                        awayLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        matchPanel.add(awayLabel);
                    }

                    matchesPanel.add(matchPanel);
                }
                
                matchesPanel.revalidate();
                matchesPanel.repaint();
                break;
            }
        }
    }

    private JTable createSortablePlayerTable() {
        DefaultTableModel model = buildPlayerTableModel();
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.setAutoCreateRowSorter(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        sorter.setComparator(0, new TextComparator());
        for (int i = 1; i < 13; i++) {
            sorter.setComparator(i, new NumericComparator());
        }
        sorter.setComparator(13, new TextComparator());

        return table;
    }

    private void refreshPlayerTable() {
        DefaultTableModel model = buildPlayerTableModel();
        tabelaPilkarzy.setModel(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tabelaPilkarzy.setRowSorter(sorter);

        sorter.setComparator(0, new TextComparator());
        for (int i = 1; i < 13; i++) {
            sorter.setComparator(i, new NumericComparator());
        }
        sorter.setComparator(13, new TextComparator());
    }

    private DefaultTableModel buildPlayerTableModel() {
        DefaultTableModel model = new DefaultTableModel(new Object[pilkarze.length][KOLUMNY.length], KOLUMNY) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (int i = 0; i < pilkarze.length; i++) {
            Pilkarz p = pilkarze[i];
            model.setValueAt(p.getImieINazwisko(), i, 0);
            model.setValueAt(p.getMorale(), i, 1);
            model.setValueAt(p.getRelacjeZTrenerem(), i, 2);
            model.setValueAt(p.getPozycja(), i, 3);
            model.setValueAt(p.getWiek(), i, 4);
            model.setValueAt(p.getUmiejetnosci(), i, 5);
            model.setValueAt(p.getPozycjaWSzatni(), i, 6);
            model.setValueAt(p.getForma(), i, 7);
            model.setValueAt(p.getKondycja(), i, 8);
            model.setValueAt(p.getDyscyplina(), i, 9);
            model.setValueAt(p.getDoswiadczenie(), i, 10);
            model.setValueAt(p.getPopularnosc(), i, 11);
            model.setValueAt(p.getUmiejetnosciDowodcze(), i, 12);
            model.setValueAt(p.isKontuzje() ? "tak" : "nie", i, 13);
        }

        return model;
    }

    private void updateProgressBars() {
        double sumaMorali = 0;
        double sumaWag = 0;
        for (Pilkarz p : pilkarze) {
            double waga = PositionUtil.getWagaPozycji(p.getPozycjaWSzatni());
            sumaMorali += p.getMorale() * waga;
            sumaWag += waga;
        }
        int atmosfera = (int) Math.round(sumaMorali / sumaWag);
        barAtmosfera.setValue(atmosfera);
        barAtmosfera.setForeground(getColorForValue(atmosfera));

        double sumaRelacji = 0;
        sumaWag = 0;
        for (Pilkarz p : pilkarze) {
            double waga = PositionUtil.getWagaPozycji(p.getPozycjaWSzatni());
            sumaRelacji += p.getRelacjeZTrenerem() * waga;
            sumaWag += waga;
        }
        int poparcie = (int) Math.round(sumaRelacji / sumaWag);
        barPoparcie.setValue(poparcie);
        barPoparcie.setForeground(getColorForValue(poparcie));

        barForma.setValue(formaDruzyny);
        barForma.setForeground(getColorForValue(formaDruzyny));

        int losowaPenalizacja = 5 + random.nextInt(11);
        int zadowolenie = (atmosfera * 2 + poparcie * 2 + formaDruzyny) / 5 - losowaPenalizacja;
        zadowolenie = Math.max(0, Math.min(100, zadowolenie));
        barZarzad.setValue(zadowolenie);
        barZarzad.setForeground(getColorForValue(zadowolenie));
    }

    private Color getColorForValue(int value) {
        if (value >= 80) return new Color(0, 180, 0);
        else if (value >= 60) return new Color(150, 200, 0);
        else if (value >= 40) return new Color(255, 200, 0);
        else if (value >= 20) return new Color(255, 100, 0);
        else return new Color(200, 0, 0);
    }
        // **WAŻNE** - Metoda do wczytywania stanu z GameState
    public void loadGameState(GameState state) {
        gameDay = state.gameDay;
        matchesPlayed = state.matchesPlayed;
        formaDruzyny = state.formaDruzyny;
        teamName = state.teamName;
        hasSpokenToday = state.hasSpokenToday;
        pilkarze = state.pilkarze;
        matchManager = state.matchManager;
        leagueManager = state.leagueManager;
        daneLiga = state.daneLiga;
        
        refreshPlayerTable();
        updateProgressBars();
        updateNextMatchDisplay();
        updateSeasonProgress();
        poleEvent.setText("✅ Gra wczytana! Gracz: " + teamName);
    }
}