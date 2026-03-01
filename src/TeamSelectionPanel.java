package trenergame;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TeamSelectionPanel extends JPanel {
    private Pilkarz[] allPlayers;
    private JList<String> playerList;
    private JLabel[] positionLabels;
    private Pilkarz[] selectedTeam;
    private JButton confirmButton;

    private static final String[] POSITIONS = {
            "BR", "PO", "ŚO", "ŚO", "LO", "LP", "ŚP", "ŚP", "PP", "ŚN", "ŚN",
            "Ławka 1", "Ławka 2", "Ławka 3", "Ławka 4", "Ławka 5", "Ławka 6",
            "Ławka 7", "Ławka 8", "Ławka 9", "Ławka 10", "Ławka 11", "Ławka 12"
    };

    private Runnable onConfirm;
    private DefaultListModel<String> listModel;
    private Map<String, Pilkarz> playerMap;

    public TeamSelectionPanel(Pilkarz[] allPlayers, Runnable onConfirm) {
        this.allPlayers = allPlayers;
        this.onConfirm = onConfirm;
        this.selectedTeam = new Pilkarz[23];
        this.playerMap = new HashMap<>();

        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Panel główny
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Lewa strona - lista piłkarzy
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Dostępni piłkarze (23)"));

        listModel = new DefaultListModel<>();
        populatePlayerList();

        playerList = new JList<String>(listModel);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.setFont(new Font("Courier New", Font.PLAIN, 12));
        playerList.setVisibleRowCount(30);
        playerList.setCellRenderer(new PlayerListRenderer());

        JScrollPane scrollLeft = new JScrollPane(playerList);
        leftPanel.add(scrollLeft, BorderLayout.CENTER);

        JLabel infoLeft = new JLabel("Kliknij na piłkarza, a następnie na pozycję");
        infoLeft.setFont(new Font("Arial", Font.PLAIN, 11));
        leftPanel.add(infoLeft, BorderLayout.SOUTH);

        // Prawa strona - pozycje
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Wybierz skład (11 + ławka)"));

        JPanel positionsPanel = new JPanel();
        positionsPanel.setLayout(new GridLayout(6, 4, 10, 10));
        positionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        positionLabels = new JLabel[23];

        for (int i = 0; i < 23; i++) {
            final int index = i;
            JPanel posPanel = new JPanel(new BorderLayout());
            posPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            posPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel posLabel = new JLabel(POSITIONS[i]);
            posLabel.setFont(new Font("Arial", Font.BOLD, 12));
            posLabel.setHorizontalAlignment(SwingConstants.CENTER);

            positionLabels[i] = new JLabel("(brak)");
            positionLabels[i].setFont(new Font("Arial", Font.PLAIN, 11));
            positionLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            positionLabels[i].setForeground(Color.GRAY);

            posPanel.add(posLabel, BorderLayout.NORTH);
            posPanel.add(positionLabels[i], BorderLayout.CENTER);

            // Klikanie na pozycję
            posPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // Prawy klik - usuń piłkarza
                    if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                        if (selectedTeam[index] != null) {
                            selectedTeam[index] = null;
                            positionLabels[index].setText("(brak)");
                            positionLabels[index].setForeground(Color.GRAY);
                            playerList.repaint();
                        }
                        return;
                    }

                    // Lewy klik - dodaj piłkarza
                    int selectedPlayerIdx = playerList.getSelectedIndex();
                    if (selectedPlayerIdx < 0) {
                        JOptionPane.showMessageDialog(TeamSelectionPanel.this,
                                "Najpierw wybierz piłkarza z listy!");
                        return;
                    }

                    String selectedKey = listModel.getElementAt(selectedPlayerIdx);
                    
                    // Pomiń nagłówki sekcji
                    if (selectedKey.startsWith("===")) {
                        JOptionPane.showMessageDialog(TeamSelectionPanel.this,
                                "Wybierz konkretnego piłkarza!");
                        return;
                    }

                    Pilkarz selectedPlayer = playerMap.get(selectedKey);

                    if (selectedPlayer == null) {
                        JOptionPane.showMessageDialog(TeamSelectionPanel.this,
                                "Błąd: nie można znaleźć piłkarza");
                        return;
                    }

                    // Sprawdzenie kontuzji
                    if (selectedPlayer.isKontuzje()) {
                        JOptionPane.showMessageDialog(TeamSelectionPanel.this,
                                "❌ Kontuzjowany zawodnik nie może grać w meczu!");
                        return;
                    }

                    // Sprawdź czy gracz jest już gdzie indziej w składzie
                    for (int j = 0; j < 23; j++) {
                        if (selectedTeam[j] == selectedPlayer) {
                            JOptionPane.showMessageDialog(TeamSelectionPanel.this,
                                    "⚠️ " + selectedPlayer.getImieINazwisko() + " jest już na pozycji " + POSITIONS[j] + "!\n" +
                                    "Każdy piłkarz może być wybrany tylko raz.");
                            return;
                        }
                    }

                    // Jeśli pozycja już zajęta, zwolnij ją
                    if (selectedTeam[index] != null) {
                        selectedTeam[index] = null;
                    }

                    // Przydziel piłkarza na pozycję
                    selectedTeam[index] = selectedPlayer;
                    positionLabels[index].setText(selectedPlayer.getImieINazwisko());
                    positionLabels[index].setForeground(Color.BLACK);

                    // Odśwież listę aby pokazać zaznaczenie
                    playerList.repaint();
                    playerList.setSelectedIndex(-1);
                }
            });

            // Tooltip z instrukcją
            posPanel.setToolTipText("Lewy klik: dodaj piłkarza | Prawy klik: usuń piłkarza");

            positionsPanel.add(posPanel);
        }

        JScrollPane scrollRight = new JScrollPane(positionsPanel);
        rightPanel.add(scrollRight, BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Przycisk potwierdzenia
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnAutoSelect = new JButton("🤖 Automat");
        btnAutoSelect.setFont(new Font("Arial", Font.BOLD, 12));
        btnAutoSelect.addActionListener(e -> autoSelectTeam());
        bottomPanel.add(btnAutoSelect);

        JButton btnClearAll = new JButton("Wyczyść wszystko");
        btnClearAll.addActionListener(e -> clearSelection());
        bottomPanel.add(btnClearAll);

        confirmButton = new JButton("✓ Potwierdź skład");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.addActionListener(e -> confirm());
        bottomPanel.add(confirmButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void autoSelectTeam() {
        clearSelection();

        // Przygotuj listę dostępnych piłkarzy (bez kontuzji)
        List<Pilkarz> available = new ArrayList<>();
        for (Pilkarz p : allPlayers) {
            if (!p.isKontuzje()) {
                available.add(p);
            }
        }

        // Pozycje podstawowe
        String[] basicPositions = {"BR", "PO", "ŚO", "LO", "LP", "PP", "ŚP", "ŚN"};

        // Mapuj ile miejsc na każdą pozycję
        Map<String, Integer> positionSlots = new HashMap<>();
        positionSlots.put("BR", 1);
        positionSlots.put("PO", 1);
        positionSlots.put("ŚO", 2);
        positionSlots.put("LO", 1);
        positionSlots.put("LP", 1);
        positionSlots.put("PP", 1);
        positionSlots.put("ŚP", 2);
        positionSlots.put("ŚN", 2);

        // Wypełnij podstawową jedenastkę
        for (String pos : basicPositions) {
            int needed = positionSlots.get(pos);
            int filled = 0;

            for (int j = 0; j < 11; j++) {
                if (POSITIONS[j].equals(pos) && selectedTeam[j] == null && filled < needed) {
                    Pilkarz best = findBestPlayerForPosition(pos, available);
                    if (best != null) {
                        selectedTeam[j] = best;
                        positionLabels[j].setText(best.getImieINazwisko());
                        positionLabels[j].setForeground(Color.BLACK);
                        available.remove(best);
                        filled++;
                    }
                }
            }
        }

        // Pozostałych piłkarzy ustaw na ławkę (posortowane po umiejętnościach)
        available.sort((p1, p2) -> Integer.compare(p2.getUmiejetnosci(), p1.getUmiejetnosci()));

        int benchIdx = 11;
        for (Pilkarz p : available) {
            if (benchIdx < 23) {
                selectedTeam[benchIdx] = p;
                positionLabels[benchIdx].setText(p.getImieINazwisko());
                positionLabels[benchIdx].setForeground(Color.BLACK);
                benchIdx++;
            }
        }

        playerList.repaint();
    }

    private Pilkarz findBestPlayerForPosition(String position, List<Pilkarz> available) {
        Pilkarz best = null;
        for (Pilkarz p : available) {
            if (p.getPozycja().equals(position)) {
                if (best == null || p.getUmiejetnosci() > best.getUmiejetnosci()) {
                    best = p;
                }
            }
        }
        return best;
    }

    private void populatePlayerList() {
        // Zbiory piłkarzy posortowane po pozycjach
        Map<String, List<Pilkarz>> playersByPosition = new LinkedHashMap<>();
        List<Pilkarz> injuredPlayers = new ArrayList<>();

        // Inicjalizuj mapy pozycji
        playersByPosition.put("BR", new ArrayList<>());
        playersByPosition.put("PO", new ArrayList<>());
        playersByPosition.put("ŚO", new ArrayList<>());
        playersByPosition.put("LO", new ArrayList<>());
        playersByPosition.put("LP", new ArrayList<>());
        playersByPosition.put("PP", new ArrayList<>());
        playersByPosition.put("ŚP", new ArrayList<>());
        playersByPosition.put("ŚN", new ArrayList<>());

        // Sortuj piłkarzy
        for (Pilkarz p : allPlayers) {
            if (p.isKontuzje()) {
                injuredPlayers.add(p);
            } else {
                String pos = p.getPozycja();
                if (playersByPosition.containsKey(pos)) {
                    playersByPosition.get(pos).add(p);
                }
            }
        }

        // Dodaj piłkarzy do listy
        for (Map.Entry<String, List<Pilkarz>> entry : playersByPosition.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                // Nagłówek pozycji
                listModel.addElement("=== " + entry.getKey() + " ===");

                // Piłkarze na danej pozycji
                for (Pilkarz p : entry.getValue()) {
                    String displayText = formatPlayerDisplay(p);
                    listModel.addElement(displayText);
                    playerMap.put(displayText, p);
                }
            }
        }

        // Dodaj kontuzjowanych na końcu
        if (!injuredPlayers.isEmpty()) {
            listModel.addElement("=== KONTUZJE ===");
            for (Pilkarz p : injuredPlayers) {
                String displayText = formatPlayerDisplay(p) + " [KONTUZJA]";
                listModel.addElement(displayText);
                playerMap.put(displayText, p);
            }
        }
    }

    private String formatPlayerDisplay(Pilkarz p) {
        return String.format("%-25s | %s | Uma: %3d | Kond: %3d",
                p.getImieINazwisko(),
                p.getPozycja(),
                p.getUmiejetnosci(),
                p.getKondycja()
        );
    }

    private int findPlayerIndex(Pilkarz player) {
        for (int i = 0; i < allPlayers.length; i++) {
            if (allPlayers[i] == player) {
                return i;
            }
        }
        return -1;
    }

    private void clearSelection() {
        selectedTeam = new Pilkarz[23];
        for (JLabel label : positionLabels) {
            label.setText("(brak)");
            label.setForeground(Color.GRAY);
        }
        playerList.setSelectedIndex(-1);
        playerList.repaint();
    }

    private void confirm() {
        // Sprawdź ile piłkarzy w podstawowej 11
        int countBasic = 0;
        for (int i = 0; i < 11; i++) {
            if (selectedTeam[i] != null) {
                countBasic++;
            }
        }

        // Sprawdź ile piłkarzy łącznie
        int countTotal = 0;
        for (Pilkarz p : selectedTeam) {
            if (p != null) countTotal++;
        }

        // Wymóg: minimum 11 w podstawowej jedenastce
        if (countBasic < 11) {
            int result = JOptionPane.showConfirmDialog(this,
                    "⚠️ UWAGA!\n\n" +
                    "Wystawiłeś tylko " + countBasic + " piłkarzy w podstawowej jedenastce.\n" +
                    "Grożą ci WALKOWER!\n\n" +
                    "Razem masz " + countTotal + " piłkarzy.\n\n" +
                    "Czy na pewno chcesz potwierdzić skład?",
                    "Potwierdzenie składu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {
                return; // Anuluj
            }
        }

        // Jeśli wszystko ok, potwierdzenie
        onConfirm.run();
    }

    public Pilkarz[] getSelectedTeam() {
        return selectedTeam;
    }

    // Custom renderer do zaznaczania już wybranych piłkarzy
    private class PlayerListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            String text = (String) value;
            
            // Nagłówki - szare tło
            if (text.startsWith("===")) {
                c.setBackground(new Color(200, 200, 200));
                c.setForeground(Color.BLACK);
                setBackground(new Color(200, 200, 200));
                setForeground(Color.BLACK);
            } else {
                // Sprawdź czy piłkarz jest już wybrany
                Pilkarz player = playerMap.get(text.replace("✓ ", ""));
                if (player != null) {
                    // Szukaj piłkarza w selectedTeam
                    boolean isAlreadySelected = false;
                    for (Pilkarz p : selectedTeam) {
                        if (p == player) {
                            isAlreadySelected = true;
                            break;
                        }
                    }
                    
                    if (isAlreadySelected) {
                        // Zaznaczeni piłkarze - zielone tło
                        c.setBackground(new Color(144, 238, 144)); // Light green
                        c.setForeground(Color.BLACK);
                        setBackground(new Color(144, 238, 144));
                        setForeground(Color.BLACK);
                        setText("✓ " + text); // Dodaj checkmark
                    } else if (!isSelected) {
                        // Normalni piłkarze
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }
                }
            }
            
            return c;
        }
    }
}
