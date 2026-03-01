package trenergame;

import javax.swing.*;
import java.util.Random;
import java.io.Serializable;

/**
 * Piłkarz z 14 statystykami (0-100) + rola/pozycja + rozmowy 1:1
 * Serializable do save/load, generuje przykładowych do testów
 */
public class Pilkarz implements Serializable {
    private static final long serialVersionUID = 1L;

    // Podstawowe dane
    private String imieINazwisko;      // "Jan Kowalski"
    private int morale;                // 0-100 (rozmowy)
    private int relacjeZTrenerem;      // 0-100 (rozmowy)
    private String pozycja;            // "BR", "PO", "NP"
    private int wiek;                  // 17-40

    // Statystyki (0-100)
    private int umiejetnosci;          // Ogólna siła (z GameCreationManager)
    private String pozycjaWSzatni;     // "Gwiazda", "Junior"
    private int forma;                 // Bieżąca forma
    private int kondycja;              // Wytrzymałość
    private int dyscyplina;            // Mniej kartek
    private int doswiadczenie;         // Wiekowe
    private int popularnosc;           // Marketing
    private int umiejetnosciDowodcze;  // Liderstwo
    private boolean kontuzje;          // Unavailable?

    /**
     * Pełny konstruktor z generatora (GameCreationManager)
     */
    public Pilkarz(String imieINazwisko, int morale, int relacjeZTrenerem, String pozycja, int wiek,
                   int umiejetnosci, String pozycjaWSzatni, int forma, int kondycja, int dyscyplina,
                   int doswiadczenie, int popularnosc, int umiejetnosciDowodcze, boolean kontuzje) {
        this.imieINazwisko = imieINazwisko;
        this.morale = morale;
        this.relacjeZTrenerem = relacjeZTrenerem;
        this.pozycja = pozycja;
        this.wiek = wiek;
        this.umiejetnosci = umiejetnosci;
        this.pozycjaWSzatni = pozycjaWSzatni;
        this.forma = forma;
        this.kondycja = kondycja;
        this.dyscyplina = dyscyplina;
        this.doswiadczenie = doswiadczenie;
        this.popularnosc = popularnosc;
        this.umiejetnosciDowodcze = umiejetnosciDowodcze;
        this.kontuzje = kontuzje;
    }

    // ========== GETTERY/SETTERY ==========
    public String getImieINazwisko() { return imieINazwisko; }
    public int getMorale() { return morale; }
    public void setMorale(int morale) { this.morale = Math.max(0, Math.min(100, morale)); }
    public int getRelacjeZTrenerem() { return relacjeZTrenerem; }
    public void setRelacjeZTrenerem(int relacjeZTrenerem) { 
        this.relacjeZTrenerem = Math.max(0, Math.min(100, relacjeZTrenerem)); 
    }
    public String getPozycja() { return pozycja; }
    public int getWiek() { return wiek; }
    public int getUmiejetnosci() { return umiejetnosci; }
    public String getPozycjaWSzatni() { return pozycjaWSzatni; }
    public int getForma() { return forma; }
    public int getKondycja() { return kondycja; }
    public int getDyscyplina() { return dyscyplina; }
    public int getDoswiadczenie() { return doswiadczenie; }
    public int getPopularnosc() { return popularnosc; }
    public int getUmiejetnosciDowodcze() { return umiejetnosciDowodcze; }
    public boolean isKontuzje() { return kontuzje; }

    /**
     * Generuje n przykładowych piłkarzy (do testów/debug)
     */
    public static Pilkarz[] generujPrzykladowych(int n) {
        String[] imiona = {"Jan", "Adam", "Michał", "Piotr", "Paweł", "Kamil", "Mateusz", "Tomasz", "Dawid", "Marek"};
        String[] nazwiska = {"Nowak", "Kowalski", "Wiśniewski", "Wójcik", "Lewandowski", "Kamiński", "Dąbrowski", "Szymański", "Woźniak", "Zieliński"};
        String[] pozycje = {"BR", "PO", "ŚO", "LO", "ŚP", "LW", "NP"};
        String[] role = {"Junior", "Kapitan", "Gwiazda", "Lider", "Przeciętny"};

        Random r = new Random();
        Pilkarz[] tab = new Pilkarz[n];
        for (int i = 0; i < n; i++) {
            tab[i] = new Pilkarz(
                imiona[r.nextInt(imiona.length)] + " " + nazwiska[r.nextInt(nazwiska.length)], // Imię Nazwisko
                50 + r.nextInt(51),                                        // Morale 50-100
                40 + r.nextInt(61),                                        // Relacje 40-100
                pozycje[r.nextInt(pozycje.length)],                        // Pozycja
                17 + r.nextInt(24),                                        // Wiek 17-40
                20 + r.nextInt(81),                                        // Um. 20-100
                role[r.nextInt(role.length)],                              // Rola
                50 + r.nextInt(51),                                        // Forma 50-100
                80 + r.nextInt(46),                                        // Kond. 80-125? → clamp w setter
                20 + r.nextInt(81),                                        // Dyscyplina 20-100
                r.nextInt(101),                                            // Doświadczenie 0-100
                10 + r.nextInt(91),                                        // Popularność 10-100
                r.nextInt(101),                                            // Dow. 0-100
                r.nextDouble() < 0.15                                      // 15% kontuzja
            );
        }
        return tab;
    }

    /**
     * 6 opcji rozmowy 1:1 (z GamePanelCustom)
     */
    public String[] getDialogOptions() {
        return new String[] {
            "Pochwal jego ostatnią formę",
            "Wyraź delikatną krytykę",
            "Zapytaj o samopoczucie",
            "Zmotywuj do walki",
            "Omów trudne decyzje",
            "Docen jego profesjonalizm"
        };
    }

    /**
     * Efekty rozmowy (zmiana morale/relacji, clamp 0-100)
     */
    public void applyDialogEffect(int option) {
        switch(option) {
            case 0: // Pochwała
                setMorale(getMorale() + 8);
                setRelacjeZTrenerem(getRelacjeZTrenerem() + 6);
                break;
            case 1: // Krytyka
                setMorale(getMorale() - 6);
                setRelacjeZTrenerem(getRelacjeZTrenerem() - 7);
                break;
            case 2: // Samopoczucie
                setMorale(getMorale() + 2);
                setRelacjeZTrenerem(getRelacjeZTrenerem() + 3);
                break;
            case 3: // Motywacja
                setMorale(getMorale() + 10);
                break;
            case 4: // Decyzje
                setMorale(getMorale() - 5);
                setRelacjeZTrenerem(getRelacjeZTrenerem() - 4);
                break;
            case 5: // Profesjonalizm
                setRelacjeZTrenerem(getRelacjeZTrenerem() + 10);
                break;
            default:
                break; // Brak efektu
        }
    }
}
