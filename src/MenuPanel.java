package trenergame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel menu główne: animowany tytuł "TRENER." + przyciski
 * Tło background.jpg + pulsujący efekt + load/save
 */
public class MenuPanel extends JPanel implements ActionListener {
    private Image backgroundImage;     // Tło stadionu (background.jpg)
    public JButton nowaGraButton;      // "Nowa Gra" → GameCreationManager
    public JButton wczytajGreButton;   // "Wczytaj Grę" → SaveLoadManager
    private float scale = 1.0f;        // Skala tytułu (puls 1.0-1.1)
    private boolean scaleIncreasing = true;
    private Timer timer;               // 40ms repaint dla animacji

    /**
     * Inicjalizuje: tło + przyciski + timer + load listener
     */
    public MenuPanel() {
        setLayout(null);
        
        // Ładuje tło z resources
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("background.jpg"));
        backgroundImage = bgIcon.getImage();

        // Przyciski (36pt Arial)
        nowaGraButton = new JButton("Nowa Gra");
        wczytajGreButton = new JButton("Wczytaj Grę");
        nowaGraButton.setFont(new Font("Arial", Font.PLAIN, 36));
        wczytajGreButton.setFont(new Font("Arial", Font.PLAIN, 36));
        add(nowaGraButton);
        add(wczytajGreButton);

        // Load: dialog → TrenerGame.loadedGameState → showGameWithLoadedState
        wczytajGreButton.addActionListener(e -> {
            GameState state = SaveLoadManager.showLoadDialog();
            if (state != null) {
                TrenerGame.loadedGameState = state;
                TrenerGame.showGameWithLoadedState();
            }
        });

        // Timer animacji (40ms ~25fps)
        timer = new Timer(40, this);
        timer.start();
    }

    /**
     * Rysuje: tło → cień "TRENER." → tytuł → pozycjonuje przyciski
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Tło rozciągnięte
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        // Animowany tytuł "TRENER." (pulsujący 100pt * scale)
        String text = "TRENER.";
        Font baseFont = new Font("Arial", Font.BOLD, 100);
        int fontSize = (int) (baseFont.getSize() * scale);
        Font font = baseFont.deriveFont((float) fontSize);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics(font);
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = 220; // Wysokość tytułu
        
        // Cień (szary + offset)
        g2d.setColor(Color.GRAY);
        g2d.drawString(text, x + 3, y + 3);
        // Główny (czarny)
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x, y);
        
        // Dynamiczne pozycje przycisków (prawy dolny róg)
        int buttonWidth = 400;
        int buttonHeight = 80;
        int padding = 50;
        nowaGraButton.setBounds(getWidth() - buttonWidth - padding, 
                               getHeight() - 2 * buttonHeight - 2 * padding, 
                               buttonWidth, buttonHeight);
        wczytajGreButton.setBounds(getWidth() - buttonWidth - padding, 
                                  getHeight() - buttonHeight - padding, 
                                  buttonWidth, buttonHeight);
    }

    /**
     * Timer tick: puls scale (1.0 ↔ 1.1) + repaint
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (scaleIncreasing) {
            scale += 0.005f;
            if (scale >= 1.1f) scaleIncreasing = false;
        } else {
            scale -= 0.005f;
            if (scale <= 1.0f) scaleIncreasing = true;
        }
        repaint(); // Wywołuje paintComponent
    }
}
