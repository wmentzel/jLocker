package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.SuperUser;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.WindowEvent;
import java.io.File;

import static com.randomlychosenbytes.jlocker.manager.UtilsKt.decryptKeyWithString;

public class LogInDialog extends javax.swing.JDialog {
    File resPath = null;
    private final DataManager dataManager;

    public LogInDialog(java.awt.Frame parent, DataManager dataManager, boolean modal) {
        super(parent, modal);
        initComponents();

        this.dataManager = dataManager;

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        // focus in the middle
        setLocationRelativeTo(null);

        // set title from resources
        setTitle(dataManager.getAppTitle() + " " + dataManager.getAppVersion() + " - Anmeldung");

        // Default closing operation
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent winEvt) {
                System.exit(0);
            }
        });

        // Check if backup directory exists. If not, create it.
        loadBackupButton.setEnabled(DataManager.getInstance().getBackupDirectory().exists());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        splashImageLabel = new javax.swing.JLabel();
        loginPanel = new javax.swing.JPanel();
        userNameLabel = new javax.swing.JLabel();
        chooseUserComboBox = new javax.swing.JComboBox();
        passwordLabel = new javax.swing.JLabel();
        passwordTextField = new javax.swing.JPasswordField();
        bottomPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        loadBackupButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("jLocker 1.5 - Anmeldung");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        splashImageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jLocker_2014.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        getContentPane().add(splashImageLabel, gridBagConstraints);

        loginPanel.setLayout(new java.awt.GridBagLayout());

        userNameLabel.setText("Benutzer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        loginPanel.add(userNameLabel, gridBagConstraints);

        chooseUserComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"SuperUser", "Eingeschränkter Benutzer"}));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        loginPanel.add(chooseUserComboBox, gridBagConstraints);

        passwordLabel.setText("Passwort");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        loginPanel.add(passwordLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        loginPanel.add(passwordTextField, gridBagConstraints);

        bottomPanel.setLayout(new java.awt.GridBagLayout());

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        bottomPanel.add(okButton, gridBagConstraints);

        loadBackupButton.setText("Backup laden");
        loadBackupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBackupButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(loadBackupButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 10, 0);
        loginPanel.add(bottomPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        getContentPane().add(loginPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed

        boolean isSuperUser = chooseUserComboBox.getSelectedIndex() == 0;

        //
        // Loading...
        //
        if (resPath == null) {
            dataManager.loadDefaultFile(isSuperUser);
        } else {
            dataManager.loadFromCustomFile(resPath, isSuperUser); // backup loading
        }

        //
        // Initialization
        //
        String password = String.valueOf(passwordTextField.getPassword());

        if (dataManager.getCurrentUser().isPasswordCorrect(password)) {

            dataManager.setUserMasterKey(decryptKeyWithString(dataManager.getCurrentUser().getEncryptedUserMasterKeyBase64(), password));

            if (isSuperUser) {
                dataManager.setSuperUserMasterKey(decryptKeyWithString(((SuperUser) dataManager.getCurrentUser()).getEncryptedSuperUMasterKeyBase64(), password));
            }

        } else {
            JOptionPane.showMessageDialog(this, "Das Passwort ist falsch!", "Fehler", JOptionPane.OK_OPTION);
            return;
        }

        dataManager.initBuildingObject();

        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void loadBackupButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadBackupButtonActionPerformed
    {//GEN-HEADEREND:event_loadBackupButtonActionPerformed
        final JFileChooser fc = new JFileChooser(DataManager.getInstance().getBackupDirectory());

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".dat");
            }

            @Override
            public String getDescription() {
                return "jLocker-Dateien";
            }
        };

        fc.setFileFilter(filter);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            resPath = fc.getSelectedFile();
        }
    }//GEN-LAST:event_loadBackupButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JComboBox chooseUserComboBox;
    private javax.swing.JButton loadBackupButton;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordTextField;
    private javax.swing.JLabel splashImageLabel;
    private javax.swing.JLabel userNameLabel;
    // End of variables declaration//GEN-END:variables
}
