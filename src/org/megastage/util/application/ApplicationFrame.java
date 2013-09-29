/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util.application;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;
/**
 *
 * @author Teppo
 */
public class ApplicationFrame {
    private JFrame frame;
    private JPanel panel;
    private CardLayout layout;

    public JFrame getFrame() {
        return frame;
    }
    
    public void repaint() {
        // TODO remove this check and fix null pointer by correcting the sequence
        panel.repaint();
    }

    public void addCard(final Component card, final String name) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.add(card, name);
                panel.validate();
            }
        });
    }
    
    public void showCard(final String name) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                layout.show(panel, name);
            }
        });
    }
    
    public void addWindowListener(final WindowListener listener) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.addWindowListener(listener);
            }
        });
    }

    public void close() {
        // save the window settings on exit
        int currentExtendedState = frame.getExtendedState();

        // get the preferred size of the non-maximized view
        if (currentExtendedState != JFrame.NORMAL) {
            frame.setExtendedState(JFrame.NORMAL);
        }
        Dimension currentSize = frame.getSize();

        AppConfig.settings.set("Application.WindowExtensionState", currentExtendedState);
        AppConfig.settings.set("Application.WindowSize", currentSize);
        AppConfig.settings.save();
    }

    public void init() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                frame = new JFrame();

                frame.setTitle(AppConfig.messages.getString("title", AppConfig.version));
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                frame.setPreferredSize(calculateOptimalAppSize(4 * 128, 4 * 112));

                frame.setIconImage(AppConfig.image);

                layout = new CardLayout();
                panel = new JPanel(layout);
                frame.getContentPane().add(panel);

                frame.pack();
                frame.setLocationRelativeTo(null);

                frame.setExtendedState(getOptimalExtendedState(frame));

                frame.setVisible(true);
                frame.createBufferStrategy(2);
            }
        });
    }

    /**
     * checks if this JFrame is too big to be viewed on the main screen and
     * returns the recommended extension state if the userSettings contains an
     * preference, this is used as initial value. However, this may get
     * overwritten if the screen resolution changed.
     *
     * @param frame the frame to check
     * @return the state, to tell if this window should be maximized in any
     * direction
     * @see JFrame#setExtendedState(int)
     */
    private static int getOptimalExtendedState(JFrame frame) {
        int extendedState = AppConfig.settings.get("Application.WindowExtensionState", Frame.NORMAL);

        int width = frame.getPreferredSize().width;
        int height = frame.getPreferredSize().height;

        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenDimensions.getHeight() <= height) {
            // height = screenDimensions.height;
            extendedState = extendedState | JFrame.MAXIMIZED_VERT;
        }

        if (screenDimensions.getWidth() <= width) {
            // width = screenDimensions.width;
            extendedState = extendedState | JFrame.MAXIMIZED_HORIZ;
        }

        return extendedState;
    }

    /**
     * fits the app size to the resolution of the users screen if user settings
     * exist, those get used as default value. However, if the screen size is
     * too low, this value is overwritten.
     *
     * @param width the default width
     * @param height the default height
     * @return the preferred values or lower sizes if the screen resolution is
     * too low
     */
    private static Dimension calculateOptimalAppSize(int width, int height) {
        Dimension userWindowSize = AppConfig.settings.getAsDimension("Application.WindowSize", width, height);
        if (userWindowSize != null) {
            width = userWindowSize.width;
            height = userWindowSize.height;
        }

        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenDimensions.getHeight() < height) {
            height = screenDimensions.height;
        }

        if (screenDimensions.getWidth() < width) {
            width = screenDimensions.width;
        }

        return new Dimension(width, height);
    }

}
