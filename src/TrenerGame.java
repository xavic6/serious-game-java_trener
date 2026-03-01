package trenergame;

import javax.swing.*;

public class TrenerGame {
    
    static JFrame frame;
    static MenuPanel menuPanel;
    
    //Dla systemu save/load
    public static GameState loadedGameState = null;
    public static final JFrame mainFrame = new JFrame();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Trener.");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 1024);
            frame.setLocationRelativeTo(null);
            menuPanel = new MenuPanel();
            frame.add(menuPanel);
            frame.setVisible(true);
            menuPanel.nowaGraButton.addActionListener(e -> startNewGame());
        });
    }

    static void startNewGame() {
        GameCreationManager creationManager = new GameCreationManager(frame);
    }

    public static void showMenu() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(menuPanel);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }
    
    //Metoda do wczytywania zapisanej gry
    public static void showGameWithLoadedState() {
        if (loadedGameState == null) return;
        
    GamePanelCustom gamePanel = new GamePanelCustom(
        () -> showMenu(),
        () -> {},
        () -> {},
        loadedGameState.teamName,
        loadedGameState.daneLiga,
        loadedGameState.pilkarze
    );
        
        gamePanel.loadGameState(loadedGameState);
        
        frame.getContentPane().removeAll();
        frame.add(gamePanel);
        frame.revalidate();
        frame.repaint();
        
        loadedGameState = null;
    }
}