package trenergame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CreateTeamPanel extends JPanel {
    private static final int PLAYERS_COUNT = 23;

    private static final String ROLE_JUNIOR = "Junior";
    private static final String ROLE_KAPITAN = "Kapitan";
    private static final String ROLE_GWIAZDA = "Gwiazda";
    private static final String ROLE_LIDER = "Lider";
    private static final String ROLE_PRZECIETNY = "Przeciętny";
    private static final String ROLE_WETERAN = "Weteran";
    private static final String ROLE_OUTSIDER = "Outsider";

    private static final String[] ROLE_OPTIONS = {
            "", ROLE_JUNIOR, ROLE_KAPITAN, ROLE_GWIAZDA, ROLE_LIDER,
            ROLE_PRZECIETNY, ROLE_WETERAN, ROLE_OUTSIDER
    };

    // Pozycje na boisku
    private static final String[] POSITION_OPTIONS = {"", "BR", "PO", "ŚO", "LO", "ŚP", "PP", "LP", "ŚN"};

    private JTextField[] nameFields;
    private JSpinner[] ageSpinners;
    private JComboBox<String>[] roleCombos;
    private JComboBox<String>[] positionCombos;

    private boolean[] nameEdited;
    private boolean[] ageEdited;
    private boolean[] roleEdited;
    private boolean[] positionEdited;

    private GameCreationManager manager;

    public CreateTeamPanel(GameCreationManager manager, String teamName) {
        this.manager = manager;
        setLayout(new BorderLayout());
        initComponents(teamName);
    }

    private void initComponents(String teamName) {
        JPanel content = new JPanel();
        content.setLayout(null);
        content.setPreferredSize(new Dimension(1100, 980));

        JLabel title = new JLabel("UTWÓRZ SKŁAD DRUŻYNY: " + teamName);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(20, 20, 800, 30);
        content.add(title);

        JLabel info = new JLabel("Wprowadź lub wylosuj 23 zawodników: imię i nazwisko, wiek, pozycję i pozycję na boisku.");
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setBounds(20, 55, 850, 25);
        content.add(info);

        JLabel lpH = new JLabel("Lp.");
        JLabel nameH = new JLabel("Imię i nazwisko");
        JLabel ageH = new JLabel("Wiek");
        JLabel roleH = new JLabel("Pozycja w szatni");
        JLabel positionH = new JLabel("Pozycja");
        int startY = 90;
        lpH.setBounds(20, startY, 30, 20);
        nameH.setBounds(60, startY, 200, 20);
        ageH.setBounds(290, startY, 50, 20);
        roleH.setBounds(370, startY, 130, 20);
        positionH.setBounds(530, startY, 60, 20);
        content.add(lpH);
        content.add(nameH);
        content.add(ageH);
        content.add(roleH);
        content.add(positionH);

        nameFields = new JTextField[PLAYERS_COUNT];
        ageSpinners = new JSpinner[PLAYERS_COUNT];
        roleCombos = new JComboBox[PLAYERS_COUNT];
        positionCombos = new JComboBox[PLAYERS_COUNT];

        nameEdited = new boolean[PLAYERS_COUNT];
        ageEdited = new boolean[PLAYERS_COUNT];
        roleEdited = new boolean[PLAYERS_COUNT];
        positionEdited = new boolean[PLAYERS_COUNT];

        int rowHeight = 30;

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            int y = startY + 25 + i * rowHeight;

            JLabel lp = new JLabel(String.valueOf(i + 1) + ".");
            lp.setBounds(20, y, 30, 25);
            content.add(lp);

            nameFields[i] = new JTextField();
            nameFields[i].setBounds(60, y, 210, 25);
            nameFields[i].setDocument(new JTextFieldLimit(40));
            final int idx = i;
            nameFields[i].getDocument().addDocumentListener(simpleDocListener(() -> nameEdited[idx] = true));
            content.add(nameFields[i]);

            ageSpinners[i] = new JSpinner(new SpinnerNumberModel(20, 17, 40, 1));
            ageSpinners[i].setBounds(290, y, 60, 25);
            ageSpinners[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    ageEdited[idx] = true;
                }
            });
            content.add(ageSpinners[i]);

            roleCombos[i] = new JComboBox<>(ROLE_OPTIONS);
            roleCombos[i].setSelectedIndex(0);
            roleCombos[i].setBounds(370, y, 130, 25);
            roleCombos[i].addActionListener(e -> roleEdited[idx] = true);
            content.add(roleCombos[i]);

            positionCombos[i] = new JComboBox<>(POSITION_OPTIONS);
            positionCombos[i].setSelectedIndex(0);
            positionCombos[i].setBounds(520, y, 60, 25);
            positionCombos[i].addActionListener(e -> positionEdited[idx] = true);
            content.add(positionCombos[i]);
        }

        JButton btnBack = new JButton("← Wróć do menu");
        btnBack.setBounds(20, startY + 25 + PLAYERS_COUNT * rowHeight + 40, 150, 35);
        btnBack.addActionListener(e -> confirmBackToMenu());
        content.add(btnBack);

        JButton btnRandom = new JButton("Losuj 23 piłkarzy");
        btnRandom.setBounds(190, startY + 25 + PLAYERS_COUNT * rowHeight + 40, 180, 35);
        btnRandom.addActionListener(e -> randomizeAllPlayers());
        content.add(btnRandom);

        JButton btnNext = new JButton("ZAKOŃCZ I GRAJ →");
        btnNext.setFont(new Font("Arial", Font.BOLD, 16));
        btnNext.setBounds(650, startY + 25 + PLAYERS_COUNT * rowHeight + 35, 230, 40);
        btnNext.addActionListener(e -> finishCreation());
        content.add(btnNext);

        JScrollPane scroll = new JScrollPane(content);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private DocumentListener simpleDocListener(Runnable onChange) {
        return new DocumentListener() {
            @Override 
            public void insertUpdate(DocumentEvent e) { 
                onChange.run(); 
            }
            @Override 
            public void removeUpdate(DocumentEvent e) { 
                onChange.run(); 
            }
            @Override 
            public void changedUpdate(DocumentEvent e) { 
                onChange.run(); 
            }
        };
    }

    private void confirmBackToMenu() {
        Object[] options = {"Tak", "Nie"};
        int res = JOptionPane.showOptionDialog(
                this,
                "Wrócić do menu? Wprowadzone zmiany zostaną utracone.",
                "Powrót do menu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]
        );
        if (res == JOptionPane.YES_OPTION) {
            manager.cancelCreationAndShowMenu();
        }
    }

    private void randomizeAllPlayers() {
        Random r = new Random();
        String[] imiona = {"Jan", "Adam", "Michał", "Piotr", "Paweł", "Kamil", "Mateusz", "Tomasz", "Dawid", "Marek","Krzysztof", "Andrzej", "Marcin", "Jakub", "Łukasz", "Grzegorz", "Wojciech", "Mariusz", "Dariusz", "Zbigniew", "Jerzy", "Maciej", "Rafał", "Robert", "Józef", "Jacek", "Tadeusz", "Ryszard", "Szymon", "Kacper", "Bartosz", "Jarosław", "Artur", "Sebastian", "Damian", "Patryk", "Przemysław", "Daniel", "Karol", "Roman", "Marian", "Antoni", "Filip", "Nikodem", "Aleksander", "Leon", "Franciszek", "Mikołaj", "Stanisław", "Wiktor"};
        String[] nazwiska = {"Kowalski", "Nowak", "Wiśniewski", "Wójcik", "Kamiński", "Lewandowski", "Zieliński", "Szymański", "Woźniak", "Truchan", "Dąbrowski", "Kozłowski", "Jankowski", "Mazur", "Krawczyk", "Piotrowski", "Grabowski", "Nowakowski", "Pawłowski", "Michalski", "Król", "Wieczorek", "Jabłoński", "Wróbel", "Adamczyk", "Dudek", "Nowicki", "Majewski", "Olszewski", "Jaworski", "Malinowski", "Włodarczyk", "Pawlak", "Górski", "Rutkowski", "Michalak", "Sikora", "Ostrowski", "Baran", "Duda", "Zając", "Szczepański", "Chmielewski", "Piątek", "Czarnecki", "Sawicki", "Sokołowski", "Urbański", "Kubiak", "Maciejewski", "Kucharski", "Wilk", "Lis", "Sobczak", "Kozak", "Kaczmarek", "Borowski", "Polak", "Krupa", "Kaźmierczak", "Bąk", "Brzeziński", "Zawadzki", "Jasiński", "Wilczyński", "Walczak", "Markowski", "Wesołowski", "Tomaszewski", "Białek", "Ziółkowski", "Czerwiński", "Marciniak", "Bartkowiak", "Bednarek", "Pawlik", "Sadowski", "Sawczuk", "Wojciechowski", "Kalinowski", "Gajewski", "Mróz", "Olejniczak", "Mielczarek", "Pietrzak", "Romanowski", "Siwek", "Drozd", "Stankiewicz", "Łuczak", "Kulesza", "Raczkowski", "Stępień", "Kaczmarczyk", "Pająk", "Sienkiewicz", "Tokarski", "Fortuna", "Cieślak", "Grzelak", "Borkowski", "Tomasik", "Kubicki", "Czubak", "Radomski", "Sobolewski", "Wawrzyniak", "Sienicki", "Twardowski", "Bartoszewski", "Łukasik", "Karpiński", "Rogowski", "Frąckowiak", "Bartosz", "Leszczyński", "Madej", "Bryk", "Danielewicz", "Szewczyk", "Kosiński", "Nowosielski", "Lipski", "Świątek", "Chojnacki", "Orzechowski", "Winiarski", "Bednarczyk", "Zieleński", "Zych", "Rybak", "Bartnik", "Boruta", "Jurek", "Jaskulski", "Łoziński", "Dziuba", "Śliwiński", "Golec", "Zięba", "Majcher", "Wojtas", "Szulc", "Leśniak", "Witek", "Kurek", "Majka", "Bartoszak", "Pawłowicz", "Matysiak", "Pisarek", "Domagała", "Ślusarczyk", "Kordas", "Laskowski", "Dziadek", "Tokarz", "Borek", "Szafrański", "Niewiadomski", "Kwiecień", "Skowroński", "Tokaj", "Bober", "Bryła", "Stańczyk", "Porębski", "Mroczek", "Wójciak", "Ławniczak", "Socha", "Świderski", "Kurzawa", "Ciołek", "Filipiak", "Pyka", "Drobnik", "Walaszek", "Małek", "Burzyński", "Kruk", "Bieniek", "Krystek", "Żurek", "Pietruszka", "Barczak", "Gierak", "Kobyliński", "Migas", "Bartmiński", "Ratajczak", "Tobiasz", "Sobek", "Szopa", "Barczyk", "Kwiatkowski", "Witkowski", "Zalewski", "Wróblewski", "Kołodziej", "Konieczny", "Głowacki", "Zakrzewski", "Wasilewski", "Krajewski", "Adamski", "Sikorski", "Baranowski", "Szymczak", "Przybylski", "Błaszczyk", "Andrzejewski"};

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            if (!nameEdited[i] && nameFields[i].getText().trim().isEmpty()) {
                String name = imiona[r.nextInt(imiona.length)] + " " + nazwiska[r.nextInt(nazwiska.length)];
                nameFields[i].setText(name);
            }
        }

        List<Integer> freeIndexes = new ArrayList<>();
        for (int i = 0; i < PLAYERS_COUNT; i++) {
            if (!roleEdited[i]) {
                freeIndexes.add(i);
            }
        }
        if (!freeIndexes.isEmpty()) {
            assignRandomRolesWithLimitsRespectExisting(freeIndexes, r);
        }

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            if (!ageEdited[i]) {
                String role = (String) roleCombos[i].getSelectedItem();
                if (role != null && !role.isEmpty()) {
                    int age = generateAgeForRole(role, r);
                    ageSpinners[i].setValue(age);
                }
            }
        }

        // Losuj pozycje na boisku
        List<Integer> positionFreeIndexes = new ArrayList<>();
        for (int i = 0; i < PLAYERS_COUNT; i++) {
            if (!positionEdited[i]) {
                positionFreeIndexes.add(i);
            }
        }
        if (!positionFreeIndexes.isEmpty()) {
            assignRandomPositionsWithLimits(positionFreeIndexes, r);
        }
    }

    private int generateAgeForRole(String role, Random r) {
        if (ROLE_JUNIOR.equals(role)) {
            return 17 + r.nextInt(3);
        } else if (ROLE_WETERAN.equals(role)) {
            return 33 + r.nextInt(8);
        } else if (ROLE_GWIAZDA.equals(role)) {
            return 22 + r.nextInt(12);
        } else if (ROLE_LIDER.equals(role)) {
            return 19 + r.nextInt(22);
        } else if (ROLE_KAPITAN.equals(role)) {
            return 20 + r.nextInt(21);
        } else {
            return 17 + r.nextInt(24);
        }
    }

    private void assignRandomRolesWithLimitsRespectExisting(List<Integer> freeIndexes, Random r) {
        int countGwiazda = 0;
        int countLider = 0;
        int countKapitan = 0;
        int countJunior = 0;
        int countWeteran = 0;

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            String role = (String) roleCombos[i].getSelectedItem();
            if (role != null && !role.isEmpty()) {
                if (ROLE_GWIAZDA.equals(role)) countGwiazda++;
                else if (ROLE_LIDER.equals(role)) countLider++;
                else if (ROLE_KAPITAN.equals(role)) countKapitan++;
                else if (ROLE_JUNIOR.equals(role)) countJunior++;
                else if (ROLE_WETERAN.equals(role)) countWeteran++;
            }
        }

        int remainingKapitan = Math.max(0, 1 - countKapitan);
        int remainingGwiazda = Math.max(0, 1 - countGwiazda);
        int remainingLider = Math.max(0, 2 - countLider);
        int remainingJunior = Math.max(0, 5 - countJunior);
        int remainingWeteran = Math.max(0, 3 - countWeteran);

        Collections.shuffle(freeIndexes, r);
        int used = 0;

        if (remainingKapitan > 0 && used < freeIndexes.size()) {
            int idx = freeIndexes.get(used++);
            roleCombos[idx].setSelectedItem(ROLE_KAPITAN);
        }
        if (remainingGwiazda > 0 && used < freeIndexes.size()) {
            int idx = freeIndexes.get(used++);
            roleCombos[idx].setSelectedItem(ROLE_GWIAZDA);
        }
        while (remainingLider > 0 && used < freeIndexes.size()) {
            int idx = freeIndexes.get(used++);
            roleCombos[idx].setSelectedItem(ROLE_LIDER);
            remainingLider--;
        }
        while (remainingJunior > 0 && used < freeIndexes.size()) {
            int idx = freeIndexes.get(used++);
            roleCombos[idx].setSelectedItem(ROLE_JUNIOR);
            remainingJunior--;
        }
        while (remainingWeteran > 0 && used < freeIndexes.size()) {
            int idx = freeIndexes.get(used++);
            roleCombos[idx].setSelectedItem(ROLE_WETERAN);
            remainingWeteran--;
        }

        while (used < freeIndexes.size()) {
            int idx = freeIndexes.get(used++);
            roleCombos[idx].setSelectedItem(r.nextBoolean() ? ROLE_PRZECIETNY : ROLE_OUTSIDER);
        }
    }

    private void assignRandomPositionsWithLimits(List<Integer> freeIndexes, Random r) {
        int countBR = 0, countPO = 0, countŚO = 0, countLO = 0;
        int countŚP = 0, countPP = 0, countLP = 0, countŚN = 0;

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            String pos = (String) positionCombos[i].getSelectedItem();
            if (pos != null && !pos.isEmpty()) {
                if ("BR".equals(pos)) countBR++;
                else if ("PO".equals(pos)) countPO++;
                else if ("ŚO".equals(pos)) countŚO++;
                else if ("LO".equals(pos)) countLO++;
                else if ("ŚP".equals(pos)) countŚP++;
                else if ("PP".equals(pos)) countPP++;
                else if ("LP".equals(pos)) countLP++;
                else if ("ŚN".equals(pos)) countŚN++;
            }
        }

        int remainBR = Math.max(0, 2 - countBR);
        int remainPO = Math.max(0, 2 - countPO);
        int remainŚO = Math.max(0, 4 - countŚO);
        int remainLO = Math.max(0, 2 - countLO);
        int remainŚP = Math.max(0, 4 - countŚP);
        int remainPP = Math.max(0, 2 - countPP);
        int remainLP = Math.max(0, 2 - countLP);
        int remainŚN = Math.max(0, 4 - countŚN);

        Collections.shuffle(freeIndexes, r);
        int used = 0;

        while (remainBR > 0 && used < freeIndexes.size()) {
            positionCombos[freeIndexes.get(used++)].setSelectedItem("BR");
            remainBR--;
        }
        while (remainPO > 0 && used < freeIndexes.size()) {
            positionCombos[freeIndexes.get(used++)].setSelectedItem("PO");
            remainPO--;
        }
        while (remainŚO > 0 && used < freeIndexes.size()) {
            positionCombos[freeIndexes.get(used++)].setSelectedItem("ŚO");
            remainŚO--;
        }
        while (remainLO > 0 && used < freeIndexes.size()) {
            positionCombos[freeIndexes.get(used++)].setSelectedItem("LO");
            remainLO--;
        }
        while (remainŚP > 0 && used < freeIndexes.size()) {
            positionCombos[freeIndexes.get(used++)].setSelectedItem("ŚP");
            remainŚP--;
        }
        while (remainPP > 0 && used < freeIndexes.size()) {
            positionCombos[freeIndexes.get(used++)].setSelectedItem("PP");
            remainPP--;
        }
        while (remainLP > 0 && used < freeIndexes.size()) {
            positionCombos[freeIndexes.get(used++)].setSelectedItem("LP");
            remainLP--;
        }
        while (remainŚN > 0 && used < freeIndexes.size()) {
            positionCombos[freeIndexes.get(used++)].setSelectedItem("ŚN");
            remainŚN--;
        }
    }

    private boolean validateRoleLimits() {
        int countGwiazda = 0;
        int countLider = 0;
        int countKapitan = 0;
        int countJunior = 0;
        int countWeteran = 0;

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            String role = (String) roleCombos[i].getSelectedItem();
            if (role != null && !role.isEmpty()) {
                if (ROLE_GWIAZDA.equals(role)) countGwiazda++;
                else if (ROLE_LIDER.equals(role)) countLider++;
                else if (ROLE_KAPITAN.equals(role)) countKapitan++;
                else if (ROLE_JUNIOR.equals(role)) countJunior++;
                else if (ROLE_WETERAN.equals(role)) countWeteran++;
            }
        }

        if (countKapitan == 0) {
            JOptionPane.showMessageDialog(this, "Musi być dokładnie jeden kapitan.");
            return false;
        }
        if (countKapitan > 1) {
            JOptionPane.showMessageDialog(this, "Może być tylko jeden kapitan.");
            return false;
        }
        if (countGwiazda > 1) {
            JOptionPane.showMessageDialog(this, "Może być maksymalnie jedna gwiazda.");
            return false;
        }
        if (countLider > 2) {
            JOptionPane.showMessageDialog(this, "Może być maksymalnie 2 liderów.");
            return false;
        }
        if (countJunior > 5) {
            JOptionPane.showMessageDialog(this, "Może być maksymalnie 5 juniorów.");
            return false;
        }
        if (countWeteran > 3) {
            JOptionPane.showMessageDialog(this, "Może być maksymalnie 3 weteranów.");
            return false;
        }
        return true;
    }

    private boolean validatePositionLimits() {
        int countBR = 0, countPO = 0, countŚO = 0, countLO = 0;
        int countŚP = 0, countPP = 0, countLP = 0, countŚN = 0;

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            String pos = (String) positionCombos[i].getSelectedItem();
            if (pos != null && !pos.isEmpty()) {
                if ("BR".equals(pos)) countBR++;
                else if ("PO".equals(pos)) countPO++;
                else if ("ŚO".equals(pos)) countŚO++;
                else if ("LO".equals(pos)) countLO++;
                else if ("ŚP".equals(pos)) countŚP++;
                else if ("PP".equals(pos)) countPP++;
                else if ("LP".equals(pos)) countLP++;
                else if ("ŚN".equals(pos)) countŚN++;
            }
        }

        StringBuilder sb = new StringBuilder();

        if (countBR < 2 || countBR > 3) {
            sb.append("BR: musi być 2-3 zawodników (masz: ").append(countBR).append(")\n");
        }
        if (countPO < 2 || countPO > 3) {
            sb.append("PO: musi być 2-3 zawodników (masz: ").append(countPO).append(")\n");
        }
        if (countŚO < 4 || countŚO > 5) {
            sb.append("ŚO: musi być 4-5 zawodników (masz: ").append(countŚO).append(")\n");
        }
        if (countLO < 2 || countLO > 3) {
            sb.append("LO: musi być 2-3 zawodników (masz: ").append(countLO).append(")\n");
        }
        if (countŚP < 4 || countŚP > 5) {
            sb.append("ŚP: musi być 4-5 zawodników (masz: ").append(countŚP).append(")\n");
        }
        if (countPP < 2 || countPP > 3) {
            sb.append("PP: musi być 2-3 zawodników (masz: ").append(countPP).append(")\n");
        }
        if (countLP < 2 || countLP > 3) {
            sb.append("LP: musi być 2-3 zawodników (masz: ").append(countLP).append(")\n");
        }
        if (countŚN < 4 || countŚN > 5) {
            sb.append("ŚN: musi być 4-5 zawodników (masz: ").append(countŚN).append(")\n");
        }

        if (sb.length() > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Błędy w rozkładzie pozycji:\n\n" + sb.toString(),
                    "Błędy pozycji",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private boolean validateAgeVsRole() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            int wiek = (Integer) ageSpinners[i].getValue();
            String role = (String) roleCombos[i].getSelectedItem();
            String name = nameFields[i].getText().trim();
            if (name.isEmpty()) {
                name = "Zawodnik " + (i + 1);
            }

            if (role == null || role.isEmpty()) {
                sb.append("Zawodnik ").append(i + 1).append(". ").append(name)
                  .append(" - Pozycja w szatni nie może być pusta!\n");
            } else if (ROLE_JUNIOR.equals(role)) {
                if (wiek < 17 || wiek > 19) {
                    sb.append("Zawodnik ").append(i + 1).append(". ").append(name)
                      .append(" nie może być: Junior, ponieważ jest poza wiekiem 17-19 lat (wiek: ")
                      .append(wiek).append(").\n");
                }
            } else if (ROLE_WETERAN.equals(role)) {
                if (wiek < 33 || wiek > 40) {
                    sb.append("Zawodnik ").append(i + 1).append(". ").append(name)
                      .append(" nie może być: Weteran, ponieważ ma wiek spoza przedziału 33-40 lat (wiek: ")
                      .append(wiek).append(").\n");
                }
            } else if (ROLE_GWIAZDA.equals(role)) {
                if (wiek < 22 || wiek > 33) {
                    sb.append("Zawodnik ").append(i + 1).append(". ").append(name)
                      .append(" nie może być: Gwiazda, ponieważ jest poza wiekiem 22-33 lata (wiek: ")
                      .append(wiek).append(").\n");
                }
            } else if (ROLE_LIDER.equals(role)) {
                if (wiek < 19 || wiek > 40) {
                    sb.append("Zawodnik ").append(i + 1).append(". ").append(name)
                      .append(" nie może być: Lider, ponieważ jest poza wiekiem 19-40 lat (wiek: ")
                      .append(wiek).append(").\n");
                }
            } else if (ROLE_KAPITAN.equals(role)) {
                if (wiek < 17 || wiek > 40) {
                    sb.append("Zawodnik ").append(i + 1).append(". ").append(name)
                      .append(" nie może być: Kapitan, ponieważ jest poza wiekiem 17-40 lat (wiek: ")
                      .append(wiek).append(").\n");
                }
            }
        }

        if (sb.length() > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    sb.toString(),
                    "Błędy wieku i roli",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private boolean validateEmptyPositions() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            String position = (String) positionCombos[i].getSelectedItem();
            if (position == null || position.isEmpty()) {
                String name = nameFields[i].getText().trim();
                if (name.isEmpty()) {
                    name = "Zawodnik " + (i + 1);
                }
                sb.append("Zawodnik ").append(i + 1).append(". ").append(name)
                  .append(" - Pozycja na boisku nie może być pusta!\n");
            }
        }

        if (sb.length() > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    sb.toString(),
                    "Błędy pozycji na boisku",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private void finishCreation() {
        if (!validateAgeVsRole()) {
            return;
        }
        if (!validateEmptyPositions()) {
            return;
        }
        if (!validateRoleLimits()) {
            return;
        }
        if (!validatePositionLimits()) {
            return;
        }

        String[] names = new String[PLAYERS_COUNT];
        int[] ages = new int[PLAYERS_COUNT];
        String[] roles = new String[PLAYERS_COUNT];
        String[] positions = new String[PLAYERS_COUNT];

        for (int i = 0; i < PLAYERS_COUNT; i++) {
            String name = nameFields[i].getText().trim();
            if (name.isEmpty()) {
                name = "Zawodnik " + (i + 1);
            }
            names[i] = name;
            ages[i] = (Integer) ageSpinners[i].getValue();
            String role = (String) roleCombos[i].getSelectedItem();
            if (role == null) role = ROLE_PRZECIETNY;
            roles[i] = role;
            String position = (String) positionCombos[i].getSelectedItem();
            if (position == null) position = "ŚN";
            positions[i] = position;
        }

        manager.setTeamData(names, ages, roles);
        manager.setTeamPositions(positions);
        manager.finishCreationAndStartGame();
    }
}
