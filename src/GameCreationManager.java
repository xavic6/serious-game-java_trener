package trenergame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameCreationManager {
    private JFrame parentFrame;
    private CreateLeaguePanel leaguePanel;      // Panel tworzenia ligi
    private CreateTeamPanel teamPanel;          // Panel tworzenia składu drużyny
    private String ownTeamName;                 // Nazwa własnej drużyny
    private int ownTeamStrength;                // Siła własnej drużyny (1–99)
    private String[] otherTeams;                // Nazwy 9 drużyn przeciwnika
    private int[] otherTeamStrengths;           // Ich siły
    private String[] playerNames;               // Nazwiska piłkarzy
    private int[] playerAges;                   // Wiek piłkarzy
    private String[] playerRoles;               // Rola (np. Gwiazda, Lider, Kapitan)
    private String[] playerPositions;           // Pozycja na boisku

    public GameCreationManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        showLeaguePanel();                      // Start od panelu tworzenia ligi
    }

    // Pokazuje panel tworzenia ligi
    public void showLeaguePanel() {
        parentFrame.getContentPane().removeAll();
        leaguePanel = new CreateLeaguePanel(this);
        parentFrame.add(leaguePanel);
        parentFrame.pack();
        parentFrame.setSize(1280, 1024);
        parentFrame.setLocationRelativeTo(null);
        parentFrame.setVisible(true);
    }

    // Anuluje tworzenie i wraca do głównego menu
    public void cancelCreationAndShowMenu() {
        TrenerGame.showMenu();
    }

    // Zapisuje dane o drużynie własnej i przeciwnikach
    public void setLeagueData(String ownTeamName, int ownStrength, String[] otherTeams, int[] otherStrengths) {
        this.ownTeamName = ownTeamName;
        this.ownTeamStrength = ownStrength;
        this.otherTeams = otherTeams;
        this.otherTeamStrengths = otherStrengths;
    }

    // Pokazuje panel tworzenia drużyny (po ligi)
    public void showNextPanel() {
        parentFrame.getContentPane().removeAll();
        teamPanel = new CreateTeamPanel(this, ownTeamName);
        parentFrame.add(teamPanel);
        parentFrame.pack();
        parentFrame.setSize(1280, 1024);
        parentFrame.setLocationRelativeTo(null);
        parentFrame.setVisible(true);
    }

    // Zapisuje dane piłkarzy (bez pozycji GPS)
    public void setTeamData(String[] names, int[] ages, String[] roles) {
        this.playerNames = names;
        this.playerAges = ages;
        this.playerRoles = roles;
    }

    // Zapisuje pozycje na boisku (np. "LB", "CB", "ST")
    public void setTeamPositions(String[] positions) {
        this.playerPositions = positions;
    }

    // Finalizuje tworzenie i uruchamia grę
    public void finishCreationAndStartGame() {
        String[] leagueColumns = {"Miejsce", "Nazwa", "Mecze", "Punkty", "Bramki", "Siła"};
        List<String[]> leagueData = new ArrayList<>();

        // Dodaje własną drużynę na 1. miejsce
        leagueData.add(new String[]{"1", ownTeamName, "0", "0", "0", String.valueOf(ownTeamStrength)});

        // Dodaje 9 drużyn przeciwnika
        for (int i = 0; i < 9; i++) {
            leagueData.add(new String[]{String.valueOf(i + 2), otherTeams[i], "0", "0", "0", String.valueOf(otherTeamStrengths[i])});
        }

        // Generuje statystyki piłkarzy
        Pilkarz[] pilkarze = new Pilkarz[playerNames.length];
        Random r = new Random();

        for (int i = 0; i < playerNames.length; i++) {
            String name = playerNames[i];
            int wiek = playerAges[i];
            String pozycjaWSzatni = playerRoles[i];
            String pozycja = playerPositions[i];

            int umiejetnosci = generateUmiejetnosci(wiek, pozycjaWSzatni, ownTeamStrength, r);
            int morale = generateMorale(r);
            int relacje = generateRelacje(r);
            int forma = generateForma(pozycjaWSzatni, r);
            int kondycja = generateKondycja(pozycjaWSzatni, r);
            int dyscyplina = generateDyscyplina(pozycjaWSzatni, r);
            int doswiadczenie = generateDoswiadczenie(wiek, r);
            int popularnosc = generatePopularnosc(pozycjaWSzatni, r);
            int umDow = generateUmiejetnosciDowodcze(r);
            boolean kontuzje = r.nextDouble() < 0.1;

            pilkarze[i] = new Pilkarz(name, morale, relacje, pozycja, wiek, umiejetnosci, pozycjaWSzatni, 
                    forma, kondycja, dyscyplina, doswiadczenie, popularnosc, umDow, kontuzje);
        }

        GamePanelCustom gamePanel = new GamePanelCustom(
                () -> TrenerGame.showMenu(),
                () -> JOptionPane.showMessageDialog(parentFrame, "Zapis gry niezaimplementowany."),
                () -> {},
                ownTeamName,
                leagueData.toArray(new String[0][]),
                pilkarze
        );

        parentFrame.getContentPane().removeAll();
        parentFrame.add(gamePanel);
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    // ============ GENERATORY STATYSTYK ============

    /**
     * Generuje umiejętności na podstawie wieku, roli i SIŁY DRUŻYNY
     */
    private int generateUmiejetnosci(int wiek, String rola, int teamStrength, Random r) {
        int baseSkill = calculateSkillByAge(wiek);
        int roleModifier = getRoleSkillModifier(rola, r);
        int strengthBonus = Math.round(teamStrength * 0.3f); // 30% wpływu siły drużyny
        int finalSkill = baseSkill + roleModifier + strengthBonus;
        return Math.max(0, Math.min(100, finalSkill));
    }

    /**
     * Oblicza bazowe umiejętności na podstawie wieku
     */
    private int calculateSkillByAge(int wiek) {
        if (wiek >= 17 && wiek <= 27) {
            return 20 + (wiek - 17) * 4 + new Random().nextInt(20);
        } else if (wiek > 27 && wiek <= 33) {
            return 70 + new Random().nextInt(15);
        } else if (wiek > 33 && wiek <= 40) {
            int yearsAfter33 = wiek - 33;
            int decline = yearsAfter33 * 5;
            int baseAtPrime = 75;
            return Math.max(20, baseAtPrime - decline + new Random().nextInt(10));
        }
        return 30 + new Random().nextInt(20);
    }

    /**
     * Zwraca modyfikator umiejętności na podstawie roli piłkarza
     */
    private int getRoleSkillModifier(String rola, Random r) {
        if (rola == null || rola.isEmpty()) {
            return 0;
        }

        switch (rola) {
            case "Gwiazda":
                return 25 + r.nextInt(15);
            case "Lider":
                return 8 + r.nextInt(12);
            case "Kapitan":
                return 2 + r.nextInt(4);
            case "Junior":
                return -15 + r.nextInt(8);
            case "Weteran":
                return -20 + r.nextInt(10);
            case "Outsider":
                return -5 + r.nextInt(15);
            case "Przeciętny":
            default:
                return -5 + r.nextInt(15);
        }
    }

    /**
     * Morale – całkowicie losowo w zakresie 20–100
     */
    private int generateMorale(Random r) {
        return 20 + r.nextInt(81); // 20–100
    }

    /**
     * Relacje z trenerem – całkowicie losowo w zakresie 20–100
     */
    private int generateRelacje(Random r) {
        return 20 + r.nextInt(81); // 20–100
    }

    /**
     * Forma – losowo 30–100, z dodatkowym bonusem dla Lidera i Kapitana
     */
    private int generateForma(String rola, Random r) {
        int base = 30 + r.nextInt(71); // 30–100

        switch (rola) {
            case "Lider":
            case "Kapitan":
                base += 10 + r.nextInt(6); // +10 do +15
                break;
            default:
                break;
        }

        return Math.max(0, Math.min(100, base));
    }

    /**
     * Kondycja – bazowo 50–100, z korektą dla roli
     */
    private int generateKondycja(String rola, Random r) {
        int base = 50 + r.nextInt(51); // 50–100

        switch (rola) {
            case "Junior":
                base -= 15 + r.nextInt(10); // -15 do -25
                break;
            case "Weteran":
                base -= 15 + r.nextInt(10); // -15 do -25
                break;
            case "Kapitan":
            case "Lider":
                base += 10 + r.nextInt(10); // +10 do +19
                break;
            default:
                break;
        }

        return Math.max(0, Math.min(100, base));
    }

    /**
     * Dyscyplina – zależna od roli, z dużym bonus dla Weterana
     */
    private int generateDyscyplina(String rola, Random r) {
        int base = 40 + r.nextInt(51); // 40–90

        switch (rola) {
            case "Weteran":
                base += 25 + r.nextInt(10); // +25 do +34
                break;
            case "Gwiazda":
                base -= 10 + r.nextInt(8); // -10 do -17
                break;
            case "Lider":
            case "Kapitan":
                base += 8 + r.nextInt(7); // +8 do +14
                break;
            default:
                break;
        }

        return Math.max(0, Math.min(100, base));
    }

    /**
     * Doświadczenie – całkowicie zależne od wieku
     */
    private int generateDoswiadczenie(int wiek, Random r) {
        if (wiek >= 17 && wiek <= 20) {
            return 0 + r.nextInt(11); // 0–10 Junior
        } else if (wiek > 20 && wiek <= 25) {
            return 0 + r.nextInt(16); // 0–15 Young
        } else if (wiek > 25 && wiek <= 30) {
            return 15 + r.nextInt(21); // 15–35 Developing
        } else if (wiek > 30 && wiek <= 35) {
            return 35 + r.nextInt(26); // 35–60 Experienced
        } else if (wiek > 35 && wiek <= 40) {
            return 60 + r.nextInt(21); // 60–80 Veteran
        }
        return 0 + r.nextInt(101); // fallback
    }

    /**
     * Popularność – silna zależność od roli, z ekstremami dla Gwiazdy i Outsidera
     */
    private int generatePopularnosc(String rola, Random r) {
        int base = 40 + r.nextInt(51); // bazowa dla Przeciętnego

        switch (rola) {
            case "Gwiazda":
                return 100; // maksymalna popularność
            case "Kapitan":
            case "Lider":
                base += 20 + r.nextInt(10); // duży boost
                break;
            case "Outsider":
            case "Junior":
                base -= 25 + r.nextInt(15); // duża kara
                break;
            case "Weteran":
                base += 5 + r.nextInt(6); // mały bonus
                break;
            default:
                break;
        }

        return Math.max(0, Math.min(100, base));
    }

    /**
     * Umiejętności dowódcze – losowo 10–100
     */
    private int generateUmiejetnosciDowodcze(Random r) {
        return 10 + r.nextInt(91); // 10–100
    }
}
