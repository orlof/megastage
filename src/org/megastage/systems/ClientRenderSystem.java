package org.megastage.systems;

import com.artemis.systems.VoidEntitySystem;
import org.megastage.client.ViewComp;
import org.megastage.util.application.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;

public class ClientRenderSystem extends VoidEntitySystem {
    ApplicationFrame frame = new ApplicationFrame();
    ViewComp.ImageJPanel panel = new ViewComp.ImageJPanel("boot.png");

    public ClientRenderSystem() {
        super();

        frame.init();
        frame.addCard(panel, "BootImage");
        frame.showCard("BootImage");

        JPanel dcpuPanel = new JPanel(new BorderLayout());
        JPanel drawPanel = new JPanel(null);
        JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, drawPanel, dcpuPanel);

        frame.addCard(splitPanel, "game");
    }

    @Override
    protected void processSystem() {
        frame.repaint();
    }

    public void addWindowListener(WindowListener listener) {
        frame.addWindowListener(listener);
    }

    public void showLoginScreen() {
        ViewComp.LoginDialog loginDlg = new ViewComp.LoginDialog(frame.getFrame(), null);
        loginDlg.setVisible(true);
    }

    public void close() {
        frame.close();
    }
}