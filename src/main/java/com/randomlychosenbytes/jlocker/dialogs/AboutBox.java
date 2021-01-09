package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.State;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A simple dialog that displays information about this program.
 */
public class AboutBox extends javax.swing.JDialog {
    // TODO put in properties/resource file
    final String[] names =
            {
                    "Babette Mentzel",
                    "Torsten Brandes",
                    "Heiko Niemeyer",
                    "Indra Beeske",
                    "Hang Ming Pham",
                    "Anne Hauer",
                    "Die Schließfach-AG der RLO"
            };

    public AboutBox(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(closeButton);

        // focus in the middle
        this.setLocationRelativeTo(null);

        // start with the first name
        appThanks2Label.setText(names[0]);

        Timer timer = new Timer(1500, changeText);
        timer.setRepeats(true);
        timer.start();
    }

    ActionListener changeText = new ActionListener() {
        private int textIndex = 1;

        @Override
        public void actionPerformed(ActionEvent evt) {
            appThanks2Label.setText(names[textIndex]);

            if (++textIndex > names.length - 1)
                textIndex = 0;
        }
    };


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        imageLabel = new javax.swing.JLabel();
        dataPanel = new javax.swing.JPanel();
        westPanel = new javax.swing.JPanel();
        versionLabel = new javax.swing.JLabel();
        vendorLabel = new javax.swing.JLabel();
        technologyLabel = new javax.swing.JLabel();
        emailAddressLabel = new javax.swing.JLabel();
        thanks2Label = new javax.swing.JLabel();
        centerPanel = new javax.swing.JPanel();
        appVersionLabel = new javax.swing.JLabel();
        appVendorLabel = new javax.swing.JLabel();
        appTechnologyLabel = new javax.swing.JLabel();
        appEmailAddressLabel = new javax.swing.JLabel();
        appThanks2Label = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Über jLocker");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jLocker_2014.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        getContentPane().add(imageLabel, gridBagConstraints);

        dataPanel.setLayout(new java.awt.GridBagLayout());

        westPanel.setLayout(new java.awt.GridLayout(6, 0, 0, 5));

        versionLabel.setText("Version:");
        westPanel.add(versionLabel);

        vendorLabel.setText("Geschrieben von:");
        westPanel.add(vendorLabel);

        technologyLabel.setText("Technologie:");
        technologyLabel.setToolTipText("");
        westPanel.add(technologyLabel);

        emailAddressLabel.setText("E-Mail-Adresse:");
        westPanel.add(emailAddressLabel);

        thanks2Label.setText("Dank an:");
        westPanel.add(thanks2Label);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        dataPanel.add(westPanel, gridBagConstraints);

        centerPanel.setLayout(new java.awt.GridLayout(6, 0, 0, 5));

        appVersionLabel.setText(State.Companion.getDataManager().getAppVersion());
        centerPanel.add(appVersionLabel);

        appVendorLabel.setText("Willi Mentzel");
        centerPanel.add(appVendorLabel);

        String version = System.getProperty("java.version");
        appTechnologyLabel.setText("Java " + version + "");
        centerPanel.add(appTechnologyLabel);

        appEmailAddressLabel.setText("willi.mentzel@gmail.com");
        centerPanel.add(appEmailAddressLabel);
        centerPanel.add(appThanks2Label);

        dataPanel.add(centerPanel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(dataPanel, gridBagConstraints);

        closeButton.setText("Schließen");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        getContentPane().add(closeButton, gridBagConstraints);

        pack();
    }// </editor-fold>

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private javax.swing.JLabel appEmailAddressLabel;
    private javax.swing.JLabel appTechnologyLabel;
    private javax.swing.JLabel appThanks2Label;
    private javax.swing.JLabel appVendorLabel;
    private javax.swing.JLabel appVersionLabel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JLabel emailAddressLabel;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JLabel technologyLabel;
    private javax.swing.JLabel thanks2Label;
    private javax.swing.JLabel vendorLabel;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JPanel westPanel;

}
