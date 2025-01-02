package custom;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A custom document manager that enforces a character limit on text input.
 */
public class TextManager extends PlainDocument {

    /** The maximum number of characters allowed in the text field. */
    private int charLimit;

    /**
     * Constructs a TextManager with the specified character limit.
     *
     * @param charLimit the maximum number of characters allowed
     */
    public TextManager(int charLimit) {
        this.charLimit = charLimit;
    }

    /**
     * Inserts a string into the document, ensuring the total length does not exceed the character limit.
     *
     * @param offs the offset at which to insert the string
     * @param str the string to insert
     * @param a the attributes to associate with the inserted content
     * @throws BadLocationException if the insert position is invalid
     */
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) return;
        if ((super.getLength() + str.length()) <= charLimit) {
            super.insertString(offs, str, a);
        }
    }
}
