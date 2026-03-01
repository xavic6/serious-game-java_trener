package trenergame;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SaveLoadManager {

    private static final String SAVES_DIRECTORY = "saves";

    static {
        try {
            Files.createDirectories(Paths.get(SAVES_DIRECTORY));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zapisuje stan gry do pliku
     */
    public static boolean saveGame(GameState gameState, String fileName) {
        try {
            String filePath = SAVES_DIRECTORY + File.separator + fileName + ".sav";
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(filePath))) {
                oos.writeObject(gameState);
            }
            JOptionPane.showMessageDialog(null, "✅ Gra zapisana: " + fileName);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Błąd zapisu: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Wczytuje stan gry z pliku
     */
    public static GameState loadGame(String fileName) {
        try {
            String filePath = SAVES_DIRECTORY + File.separator + fileName + ".sav";
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(filePath))) {
                return (GameState) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Błąd wczytania: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Pobiera listę wszystkich zapisów
     */
    public static List<String> getSaveFileList() {
        List<String> saves = new ArrayList<>();
        try {
            File savesDir = new File(SAVES_DIRECTORY);
            if (savesDir.exists() && savesDir.isDirectory()) {
                File[] files = savesDir.listFiles((dir, name) -> name.endsWith(".sav"));
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName().replace(".sav", "");
                        saves.add(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saves;
    }

    /**
     * Dialog do zapisania gry z listą istniejących zapisów
     */
    public static boolean showSaveDialog(GameState gameState) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Zapisz grę");
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setLayout(null);

        JLabel label = new JLabel("Nazwa zapisu:");
        label.setBounds(20, 20, 360, 20);
        dialog.add(label);

        JTextField textField = new JTextField();
        textField.setBounds(20, 45, 360, 30);
        dialog.add(textField);

        JLabel existingLabel = new JLabel("Istniejące zapisy:");
        existingLabel.setBounds(20, 85, 360, 20);
        dialog.add(existingLabel);

        // Lista istniejących zapisów
        List<String> savesList = getSaveFileList();
        JList<String> listSaves = new JList<>(savesList.toArray(new String[0]));
        listSaves.setBounds(20, 110, 360, 120);
        JScrollPane scrollPane = new JScrollPane(listSaves);
        scrollPane.setBounds(20, 110, 360, 120);
        dialog.add(scrollPane);

        // Po kliknięciu na zapis - wstaw nazwę do pola tekstowego
        listSaves.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listSaves.getSelectedValue() != null) {
                textField.setText(listSaves.getSelectedValue());
            }
        });

        JButton btnSave = new JButton("Zapisz");
        btnSave.setBounds(150, 245, 100, 30);
        btnSave.addActionListener(e -> {
            String fileName = textField.getText().trim();
            if (fileName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Wpisz nazwę zapisu!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            saveGame(gameState, fileName);
            dialog.dispose();
        });
        dialog.add(btnSave);

        dialog.setVisible(true);
        return true;
    }

    /**
     * Dialog do wczytania gry z listą zapisów
     */
    public static GameState showLoadDialog() {
        List<String> savesList = getSaveFileList();

        if (savesList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brak zapisanych gier!", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Wczytaj grę");
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setLayout(null);

        JLabel label = new JLabel("Wybierz grę do wczytania:");
        label.setBounds(20, 20, 360, 20);
        dialog.add(label);

        // Lista zapisów
        JList<String> listSaves = new JList<>(savesList.toArray(new String[0]));
        listSaves.setBounds(20, 50, 360, 220);
        JScrollPane scrollPane = new JScrollPane(listSaves);
        scrollPane.setBounds(20, 50, 360, 220);
        dialog.add(scrollPane);

        JButton btnLoad = new JButton("Wczytaj");
        btnLoad.setBounds(100, 285, 100, 35);
        btnLoad.addActionListener(e -> {
            String selectedGame = listSaves.getSelectedValue();
            if (selectedGame == null) {
                JOptionPane.showMessageDialog(dialog, "Wybierz grę!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            GameState state = loadGame(selectedGame);
            if (state != null) {
                dialog.dispose();
            }
        });
        dialog.add(btnLoad);

        JButton btnCancel = new JButton("Anuluj");
        btnCancel.setBounds(210, 285, 100, 35);
        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.add(btnCancel);

        dialog.setVisible(true);

        if (listSaves.getSelectedValue() != null) {
            return loadGame(listSaves.getSelectedValue());
        }
        return null;
    }

    /**
     * Usuwa zapis
     */
    public static boolean deleteSave(String fileName) {
        try {
            String filePath = SAVES_DIRECTORY + File.separator + fileName + ".sav";
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}