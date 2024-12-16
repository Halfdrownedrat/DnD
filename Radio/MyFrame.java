import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

interface KeyInputListener {
    void onKeyPressed(char keyChar);
}

public class MyFrame extends JFrame implements KeyListener {
    private KeyInputListener keyInputListener;

    MyFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(null);
        this.addKeyListener(this);
        this.setVisible(true);
    }

    public void setKeyInputListener(KeyInputListener listener) {
        this.keyInputListener = listener;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // This method is invoked when a key is pressed down.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // This method is invoked when a key is released.
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Called when a key is typed.
        char keyChar = e.getKeyChar();
        if (keyInputListener != null) {
            keyInputListener.onKeyPressed(keyChar);  // Notify listener with the pressed key character
        }
    }

}


// Based on https://www.youtube.com/watch?v=BJ7fr9XwS2o
// Tutorial is doing way more