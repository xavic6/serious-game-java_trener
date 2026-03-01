package trenergame;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Limituje długość tekstu w JTextField (np. nazwy drużyn do 40 znaków)
 * Używa PlainDocument do blokady na poziomie insertString
 */
public class JTextFieldLimit extends PlainDocument {
    private int limit; // Maksymalna liczba znaków

    /**
     * Konstruktor - ustawia limit długości pola tekstowego
     */
    public JTextFieldLimit(int limit) {
        super();
        this.limit = limit; // np. 40 dla nazw drużyn
    }

    /**
     * Nadpisuje insertString - blokuje jeśli przekroczono limit
     */
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) return; // Null safety

        // Sprawdza: aktualna długość + nowa <= limit?
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr); // Wstawia tylko jeśli OK
        }
        // Inaczej ignoruje (nie rzuca błędu)
    }
}
