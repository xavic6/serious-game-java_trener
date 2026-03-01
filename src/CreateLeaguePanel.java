package trenergame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Random;

public class CreateLeaguePanel extends JPanel {
    // Pola do zarządzania 9 drużynami przeciwnika
    private JTextField[] teamFields;
    private JSpinner[] strengthSpinners;
    private boolean[] teamNameEdited;   // Czy nazwa drużyny została zmieniona ręcznie
    private boolean[] teamStrengthEdited; // Czy siła drużyny została zmieniona ręcznie

    // Pola dla własnej drużyny użytkownika
    private JTextField ownTeamField;
    private JSpinner ownStrengthSpinner;
    private boolean ownTeamEdited = false;      // Czy nazwa własnej drużyny zmieniona
    private boolean ownStrengthEdited = false;  // Czy siła własnej drużyny zmieniona

    // Listy do generowania realistycznych polskich nazw drużyn
    private String[] predefinedTeams;
    private JButton randomTeamsButton;
    private JButton nextButton;
    private JButton backButton;
    private GameCreationManager manager;

    // Polskie ksywy piłkarskie (historyczne i współczesne)
    private static final String[] KSYWY = {
        "Błyskawica", "Huragan", "Wisła", "Odra", "Grom", "Orzeł", "Jastrząb", "Wicher", "Burza", "Sokół",
        "Czarni", "Victoria", "Pogoń", "Sparta", "Warta", "Stal", "Unia", "Włókniarz", "Start", "Polonia",
        "Świt", "Strzelec", "Iskra", "Tęcza", "Gwardia", "Znicz", "Naprzód", "Zryw", "Łucznik", "Lotnik",
        "Jedność", "Piast", "Olimpia", "Astra", "Flota", "Gryf", "Sarmata", "LKS", "Kolejarz", "Legion",
        "Zawisza", "Siła", "Cukrownik", "Metalowiec", "Technik", "Przyszłość", "Chrobry", "Progres",
        "Katolik", "Brzask", "Wiktoria", "Igloopol", "Młodość", "Budowlani", "Olimp", "Granica",
        "Strumień", "Gwiazda", "Promyk", "Promień", "Satelita"
    };

    // Polskie miasta i miejscowości
    private static final String[] MIEJSCOWOSCI = {
        "Warszawa", "Kraków", "Łódź", "Wrocław", "Poznań", "Gdańsk", "Szczecin", "Lublin", "Bydgoszcz",
        "Katowice", "Białystok", "Rzeszów", "Toruń", "Kielce", "Olsztyn", "Opole", "Zielona Góra",
        "Radom", "Częstochowa", "Sosnowiec", "Gliwice", "Zabrze", "Bielsko-Biała", "Rybnik", "Bytom",
        "Tarnów", "Płock", "Elbląg", "Koszalin", "Wałbrzych", "Legnica", "Grudziądz", "Słupsk",
        "Gorzów Wlkp.", "Włocławek", "Jaworzno", "Kalisz", "Piła", "Suwałki", "Jelenia Góra",
        "Ostrołęka", "Nowy Sącz", "Konin", "Leszno", "Chełm", "Zamość", "Przemyśl", "Ełk",
        "Bełchatów", "Stalowa Wola", "Kutno", "Skierniewice", "Tczew", "Inowrocław", "Tarnowskie Góry",
        "Sandomierz", "Puławy", "Zakopane", "Nysa", "Łomża", "Chojnice", "Malbork", "Starogard Gd.",
        "Ostrów Wlkp.", "Pabianice", "Zgierz", "Lubin", "Świdnica", "Dzierżoniów", "Otwock",
        "Wołomin", "Legionowo", "Mińsk Maz.", "Piaseczno", "Żyrardów"
    };

    // Konstruktor - inicjalizuje panel i generuje nazwy drużyn
    public CreateLeaguePanel(GameCreationManager manager) {
        this.manager = manager;
        setLayout(null);
        setPreferredSize(new Dimension(900, 750));

        // Wstępnie generuje 50 realistycznych nazw drużyn
        predefinedTeams = generateRealisticTeamNames(50);

        initComponents();
    }

    // Generuje nazwy w formacie "Ksywa Miasto" (np. "Wisła Kraków")
    private String[] generateRealisticTeamNames(int count) {
        Random rand = new Random();
        String[] teams = new String[count];
        for (int i = 0; i < count; i++) {
            String ksywa = KSYWY[rand.nextInt(KSYWY.length)];
            String miejscowosc = MIEJSCOWOSCI[rand.nextInt(MIEJSCOWOSCI.length)];
            teams[i] = ksywa + " " + miejscowosc;
        }
        return teams;
    }

    // Inicjalizuje wszystkie komponenty UI
    private void initComponents() {
        JLabel title = new JLabel("UTWÓRZ LIGĘ (10 drużyn)");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(20, 20, 400, 30);
        add(title);

        // Sekcja własnej drużyny
        JLabel ownTeamLabel = new JLabel("Twoja drużyna:");
        ownTeamLabel.setFont(new Font("Arial", Font.BOLD, 16));
        ownTeamLabel.setBounds(20, 70, 150, 25);
        add(ownTeamLabel);

        ownTeamField = new JTextField("", 20);
        ownTeamField.setBounds(20, 95, 400, 30);
        ownTeamField.setFont(new Font("Arial", Font.PLAIN, 16));
        ownTeamField.setDocument(new JTextFieldLimit(40));
        // Oznacza ręczną edycję nazwy
        ownTeamField.getDocument().addDocumentListener(simpleDocListener(() -> ownTeamEdited = true));
        add(ownTeamField);

        JLabel ownStrengthLabel = new JLabel("Siła drużyny (1-99):");
        ownStrengthLabel.setBounds(20, 130, 200, 25);
        ownStrengthLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(ownStrengthLabel);

        ownStrengthSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 99, 1));
        ownStrengthSpinner.setBounds(220, 130, 80, 30);
        // Oznacza ręczną edycję siły
        ownStrengthSpinner.addChangeListener(e -> ownStrengthEdited = true);
        add(ownStrengthSpinner);

        JLabel otherTeamsLabel = new JLabel("Pozostałe 9 drużyn:");
        otherTeamsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        otherTeamsLabel.setBounds(20, 180, 200, 25);
        add(otherTeamsLabel);

        // Inicjalizacja tablic dla 9 drużyn przeciwnika
        teamFields = new JTextField[9];
        strengthSpinners = new JSpinner[9];
        teamNameEdited = new boolean[9];
        teamStrengthEdited = new boolean[9];

        int startY = 215;
        int rowHeight = 40;

        // Tworzy 9 wierszy z polami nazwy i siły
        for (int i = 0; i < 9; i++) {
            int y = startY + i * rowHeight;

            JLabel teamLabel = new JLabel("Drużyna " + (i + 1) + ":");
            teamLabel.setBounds(20, y, 90, 25);
            teamLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            add(teamLabel);

            teamFields[i] = new JTextField("", 15);
            teamFields[i].setBounds(110, y, 350, 30);
            teamFields[i].setFont(new Font("Arial", Font.PLAIN, 14));
            teamFields[i].setDocument(new JTextFieldLimit(40));
            final int idx = i;
            // Oznacza ręczną edycję nazwy drużyny
            teamFields[i].getDocument().addDocumentListener(simpleDocListener(() -> teamNameEdited[idx] = true));
            add(teamFields[i]);

            JLabel strengthLabel = new JLabel("Siła:");
            strengthLabel.setBounds(480, y, 40, 25);
            strengthLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            add(strengthLabel);

            strengthSpinners[i] = new JSpinner(new SpinnerNumberModel(50, 1, 99, 1));
            strengthSpinners[i].setBounds(520, y, 70, 30);
            // Oznacza ręczną edycję siły drużyny
            strengthSpinners[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    teamStrengthEdited[idx] = true;
                }
            });
            add(strengthSpinners[i]);
        }

        // Przyciski nawigacyjne
        backButton = new JButton("← Wróć do menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBounds(20, 620, 160, 40);
        backButton.addActionListener(e -> confirmBackToMenu());
        add(backButton);

        randomTeamsButton = new JButton("Losuj 9 drużyn");
        randomTeamsButton.setFont(new Font("Arial", Font.BOLD, 16));
        randomTeamsButton.setBounds(200, 620, 200, 40);
        randomTeamsButton.addActionListener(e -> randomizeTeams());
        add(randomTeamsButton);

        nextButton = new JButton("DALEJ →");
        nextButton.setFont(new Font("Arial", Font.BOLD, 20));
        nextButton.setBounds(700, 620, 150, 40);
        nextButton.addActionListener(e -> nextStep());
        add(nextButton);
    }

    // Uproszczony DocumentListener z lambdą
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

    // Losuje nazwy i siły tylko dla nie-edytowanych pól
    private void randomizeTeams() {
        Random rand = new Random();
        for (int i = 0; i < 9; i++) {
            // Losuje nazwę tylko jeśli nie była edytowana ręcznie
            if (!teamNameEdited[i]) {
                teamFields[i].setText(predefinedTeams[rand.nextInt(predefinedTeams.length)]);
            }
            // Losuje siłę tylko jeśli nie była edytowana ręcznie
            if (!teamStrengthEdited[i]) {
                strengthSpinners[i].setValue(20 + rand.nextInt(80));
            }
        }
        // Losuje dla własnej drużyny tylko jeśli puste i nie edytowane
        if (!ownTeamEdited && ownTeamField.getText().trim().isEmpty()) {
            String ksywa = KSYWY[rand.nextInt(KSYWY.length)];
            String miejscowosc = MIEJSCOWOSCI[rand.nextInt(MIEJSCOWOSCI.length)];
            ownTeamField.setText(ksywa + " " + miejscowosc);
        }
        if (!ownStrengthEdited) {
            ownStrengthSpinner.setValue(40 + rand.nextInt(60));
        }
    }

    // Potwierdzenie powrotu z ostrzeżeniem o utracie danych
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

    // Walidacja i przejście do następnego panelu
    private void nextStep() {
        String ownTeamName = ownTeamField.getText().trim();
        if (ownTeamName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wpisz nazwę swojej drużyny!");
            return;
        }

        int ownStrength = (Integer) ownStrengthSpinner.getValue();
        String[] otherTeams = new String[9];
        int[] otherStrengths = new int[9];

        // Sprawdza wszystkie drużyny przeciwnika
        for (int i = 0; i < 9; i++) {
            otherTeams[i] = teamFields[i].getText().trim();
            if (otherTeams[i].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wpisz nazwę drużyny " + (i + 1) + " lub użyj losowania.");
                return;
            }
            otherStrengths[i] = (Integer) strengthSpinners[i].getValue();
        }

        // Przekazuje dane do managera i przechodzi dalej
        manager.setLeagueData(ownTeamName, ownStrength, otherTeams, otherStrengths);
        manager.showNextPanel();
    }
}
