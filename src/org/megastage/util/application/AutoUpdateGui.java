/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util.application;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author Teppo
 */
public class AutoUpdateGui {
    public static boolean isUpdating = false;

    public static boolean execute() {
        if (AppConfig.settings.get("Application.AutoUpdate", false)) {
    
            final CountDownLatch completed = new CountDownLatch(1);

            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final JFrame updateFrame = new JFrame();
                    updateFrame.setTitle(AppConfig.messages.getString("update.title"));

                    updateFrame.setIconImage(AppConfig.image);
                    JLabel waiting = new JLabel(AppConfig.messages.getString("update.checking"));
                    updateFrame.getContentPane().setLayout(new FlowLayout());
                    updateFrame.getContentPane().add(waiting);
                    updateFrame.setSize(new Dimension(250, 70));
                    updateFrame.setLocationRelativeTo(null);
                    updateFrame.setVisible(true);

                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            AutoUpdate updater = checkForUpdates();
                            
                            isUpdating = updater.isUpdating();
                            completed.countDown();

                            updateFrame.dispose();
                        }
                    });
                }
            });
            
            try {
                completed.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(AutoUpdateGui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return isUpdating;
    }
    
    private static AutoUpdate checkForUpdates() {
        AutoUpdate updater = new AutoUpdate(AppConfig.version, new AutoUpdate.Callback() {

            @Override
            public void checksumFailed() {
                JOptionPane.showMessageDialog(
                        null,
                        AppConfig.messages.getString("update.checksumFailed.message"),
                        AppConfig.messages.getString("update.checksumFailed.title"),
                        JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void updateCheckFailed() {
                JOptionPane.showMessageDialog(
                        null,
                        AppConfig.messages.getString("update.updateCheckFailed.message"),
                        AppConfig.messages.getString("update.updateCheckFailed.title"),
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        
        if (updater.isUpdateAvailable()) {
            JOptionPane pane = new JOptionPane(AppConfig.messages.getString("update.updateAvailable.message"));
            String yes = AppConfig.messages.getString("update.updateAvailable.yes");
            String no = AppConfig.messages.getString("update.updateAvailable.no");
            pane.setOptions(new String[]{yes, no});
            JDialog dialog = pane.createDialog(new JFrame(), AppConfig.messages.getString("update.updateAvailable.title", updater.getLatestVersion()));
            dialog.setVisible(true);

            Object selection = pane.getValue();

            if (selection.equals(yes)) {
                JFrame updateFrame = new JFrame();
                updateFrame.setTitle(AppConfig.messages.getString("update.updating.title"));
                updateFrame.setResizable(false);
                updateFrame.setIconImage(AppConfig.image);

                final JProgressBar updateProgress = new JProgressBar(0, 100);
                updateProgress.setValue(0);
                updateProgress.setStringPainted(true);
                updateFrame.add(updateProgress);
                updateFrame.setPreferredSize(new Dimension(200, 100));
                updateFrame.pack();
                updateFrame.setLocationRelativeTo(null);
                updateFrame.setVisible(true);
                
                updater.addPropertyChangeListener(new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            updateProgress.setValue((Integer) evt.getNewValue());
                        }
                    }
                });
                
                updater.setUpdating(true);
                updater.execute();
            }
        }
        return updater;
    }
}
