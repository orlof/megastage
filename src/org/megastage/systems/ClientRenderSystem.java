package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.VoidEntitySystem;
import org.megastage.client.Game;
import org.megastage.client.ViewComp;
import org.megastage.components.client.VirtualMonitorView;
import org.megastage.util.application.ApplicationFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ClientRenderSystem extends VoidEntitySystem {
    ApplicationFrame frame = new ApplicationFrame();
    ViewComp.ImageJPanel panel = new ViewComp.ImageJPanel("boot.png");

    public ClientRenderSystem() {
        super();

        frame.init();
        frame.addCard(panel, "BootImage");
        frame.showCard("BootImage");
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