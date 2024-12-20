package custom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextFieldCustom extends JTextField {
    private String placeHolderText;
    private boolean hasPlaceHolder;

    public TextFieldCustom(String placeHolderText,int charLimit){
        super();
        this.placeHolderText=placeHolderText;
        hasPlaceHolder = true;
        setDocument(new LimitText(charLimit));
        setText(this.placeHolderText);
        setMargin(new Insets(0,10,0,0));
        addListeners();
    }

    public boolean isHasPlaceHolder() {
        return hasPlaceHolder;
    }
    private void addListeners(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(hasPlaceHolder){
                    hasPlaceHolder = false;
                    setText("");

                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(hasPlaceHolder){
                    hasPlaceHolder = false;
                    setText("");

                }
            }
        });
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                //Reinsert the placeholder text if the focus is lost and the box is empty
            if(getText().toString().length()<= 0){
                hasPlaceHolder = true;
                setText(placeHolderText);
            }
            }
        });
    }
}
