package org.megastage.client;

import org.megastage.util.application.AppConfig;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Teppo
 * Date: 27.1.2012
 * Time: 12:03
 * To change this template use File | Settings | File Templates.
 */
public class ViewComp {
    public static class ImageJPanel extends JPanel {
        public Image image;

        public ImageJPanel(Image image) {
            super(null);
            this.image = image;
        }

        public ImageJPanel(String filename) {
            super(null);

            try {
                image = ImageIO.read(new File(filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }

    public static class LoginDialog extends JDialog implements ActionListener {
        private JTextField tfServer;
        private JTextField tfPort;
        private JLabel lbServer;
        private JLabel lbPort;
        private JButton btnLogin;
        private JButton btnCancel;
        
        private Game controller;
    
        public LoginDialog(Frame parent, Game controller) {
            super(parent, "Connect", true);
            this.controller = controller;
            //
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints cs = new GridBagConstraints();

            cs.fill = GridBagConstraints.HORIZONTAL;

            lbServer = new JLabel("Server: ");
            cs.gridx = 0;
            cs.gridy = 0;
            cs.gridwidth = 1;
            panel.add(lbServer, cs);

            tfServer = new JTextField(AppConfig.settings.get("Application.ServerAddress", "localhost"), 20);
            cs.gridx = 1;
            cs.gridy = 0;
            cs.gridwidth = 2;
            panel.add(tfServer, cs);

            lbPort = new JLabel("Port: ");
            cs.gridx = 0;
            cs.gridy = 1;
            cs.gridwidth = 1;
            panel.add(lbPort, cs);

            tfPort = new JTextField(AppConfig.settings.get("Application.ServerPort", "12321"));
            cs.gridx = 1;
            cs.gridy = 1;
            cs.gridwidth = 2;
            panel.add(tfPort, cs);
            panel.setBorder(new LineBorder(Color.GRAY));

            btnLogin = new JButton("Connect");
            btnLogin.setActionCommand("ConnectToServer");
            btnLogin.addActionListener(this);
            
            btnCancel = new JButton("Cancel");
            btnCancel.setActionCommand("CancelConnectToServer");
            btnCancel.addActionListener(this);
            
            JPanel bp = new JPanel();
            bp.add(btnLogin);
            bp.add(btnCancel);

            getContentPane().add(panel, BorderLayout.CENTER);
            getContentPane().add(bp, BorderLayout.PAGE_END);

            pack();
            setResizable(false);
            setLocationRelativeTo(parent);
        }

        public String getServer() {
            return tfServer.getText().trim();
        }

        public int getPort() {
            return Integer.parseInt(tfPort.getText());
        }

        public void actionPerformed(ActionEvent e) {
            try {
                if(e.getActionCommand().equals("ConnectToServer")) {
                    System.out.println("login");
                }
                if(e.getActionCommand().equals("CancelConnectToServer")) {
                    System.out.println("cancel");
                    return;
                }
                //controller.connect(getServer(), getPort());
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        ex.toString(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

    }
    
}
