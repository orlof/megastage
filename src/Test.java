
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Test {
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        Logger.getLogger("Hello").info("Hello World");
    }
}

