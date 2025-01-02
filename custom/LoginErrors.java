package custom;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JLabel to display login error messages in red text.
 * The label is initially hidden and can be made visible as needed.
 */
public class LoginErrors extends JLabel {

    /**
     * Constructs a LoginErrors label with the specified error message.
     *
     * @param errorText the text of the error message to display
     */
    public LoginErrors(String errorText) {
        super(errorText);
        setForeground(Color.RED);
        setVisible(false);
    }
}
