
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Test {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        frame.setSize(200,200);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                System.out.println("e.getKeyChar() = " + ((int) e.getKeyChar()));
                System.out.println("e.getKeyCode() = " + ((int) e.getKeyCode()));
                return true;
            }
        });

        while(true) {
            Thread.sleep(1000);
        }
    }
}

