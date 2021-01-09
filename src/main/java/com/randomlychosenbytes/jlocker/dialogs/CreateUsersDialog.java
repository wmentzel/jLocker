package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.DataManager;
import com.randomlychosenbytes.jlocker.State;
import com.randomlychosenbytes.jlocker.model.Module;
import com.randomlychosenbytes.jlocker.model.*;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.List;

import static com.randomlychosenbytes.jlocker.CryptoKt.decryptKeyWithString;

public class CreateUsersDialog extends JDialog {
    private int displayedCardIndex;
    private final CardLayout cardLayout;
    private final boolean isFirstRun;
    private final DataManager dataManager = State.Companion.getDataManager();

    private SuperUser superUser;
    private RestrictedUser restrictedUser;

    private SecretKey superUserMasterKey;
    private SecretKey userMasterKey;

    public CreateUsersDialog(final java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // focus in the middle
        setLocationRelativeTo(null);

        isFirstRun = dataManager.getBuildingList().isEmpty();

        addWindowListener(
                new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent winEvt) {
                        if (isFirstRun) {
                            System.exit(0);
                        } else {
                            ((JDialog) winEvt.getSource()).dispose();
                        }
                    }
                }
        );

        cardLayout = new CardLayout();

        centerPanel.setLayout(cardLayout);
        centerPanel.add(welcomePanel, "card1");
        centerPanel.add(superUserPanel, "card2");
        centerPanel.add(userPanel, "card3");

        displayedCardIndex = 0;

        if (!isFirstRun) {
            cardLayout.next(centerPanel);
            displayedCardIndex = 1;
        }

        previousButton.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        welcomePanel = new javax.swing.JPanel();
        dialogTitleLabel = new javax.swing.JLabel();
        welcomeMessageLabel = new javax.swing.JLabel();
        superUserPanel = new javax.swing.JPanel();
        superUserLabel = new javax.swing.JLabel();
        suPasswordLabel = new javax.swing.JLabel();
        superUserPasswordTextField = new javax.swing.JTextField();
        suRepeatPasswordLabel = new javax.swing.JLabel();
        superUserRepeatPasswordTextField = new javax.swing.JTextField();
        userPanel = new javax.swing.JPanel();
        userLabel = new javax.swing.JLabel();
        userPasswordLabel = new javax.swing.JLabel();
        userPasswordTextField = new javax.swing.JTextField();
        userRepeatPasswordLabel = new javax.swing.JLabel();
        userRepeatPasswordTextField = new javax.swing.JTextField();
        southPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Benutzer");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.CardLayout());

        welcomePanel.setLayout(new java.awt.GridBagLayout());

        dialogTitleLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        dialogTitleLabel.setText("Benutzeranlegungs-Assisstent");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        welcomePanel.add(dialogTitleLabel, gridBagConstraints);

        welcomeMessageLabel.setText("<html>Willkommen zum Benutzeranlegungs-Assistenten!<p>Dieser Assistent führt Sie durch alle nötigen Schritte, um zwei Benuter zu anzulegen.</p></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        welcomePanel.add(welcomeMessageLabel, gridBagConstraints);

        centerPanel.add(welcomePanel, "card2");

        superUserPanel.setLayout(new java.awt.GridBagLayout());

        superUserLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        superUserLabel.setText("SuperUser");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        superUserPanel.add(superUserLabel, gridBagConstraints);

        suPasswordLabel.setText("Passwort");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        superUserPanel.add(suPasswordLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        superUserPanel.add(superUserPasswordTextField, gridBagConstraints);

        suRepeatPasswordLabel.setText("Passwort wiederholen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        superUserPanel.add(suRepeatPasswordLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        superUserPanel.add(superUserRepeatPasswordTextField, gridBagConstraints);

        centerPanel.add(superUserPanel, "card3");

        userPanel.setLayout(new java.awt.GridBagLayout());

        userLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        userLabel.setText("Eingeschränkter Benutzer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        userPanel.add(userLabel, gridBagConstraints);

        userPasswordLabel.setText("Passwort");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        userPanel.add(userPasswordLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        userPanel.add(userPasswordTextField, gridBagConstraints);

        userRepeatPasswordLabel.setText("Passwort wiederholen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        userPanel.add(userRepeatPasswordLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        userPanel.add(userRepeatPasswordTextField, gridBagConstraints);

        centerPanel.add(userPanel, "card4");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(centerPanel, gridBagConstraints);

        southPanel.setLayout(new java.awt.GridBagLayout());

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        southPanel.add(cancelButton, gridBagConstraints);

        previousButton.setText("< Zurück");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        southPanel.add(previousButton, gridBagConstraints);

        nextButton.setText("Weiter >");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        southPanel.add(nextButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(southPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (isFirstRun) {
            System.exit(0);
        } else {
            this.dispose();
        }
    }

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Prevent the user from returning to the welcome screen, because
        // that would make no sense.
        if ((displayedCardIndex - 1) > 0) {
            displayedCardIndex--;
            cardLayout.previous(centerPanel);
        }

        if (displayedCardIndex != 2) {
            nextButton.setText("Weiter >");
        }
    }

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {

        if (displayedCardIndex < 2) {
            cardLayout.next(centerPanel); // display next card
        }

        displayedCardIndex++;

        switch (displayedCardIndex - 1) {
            case 1: {
                String superUserPassword = superUserPasswordTextField.getText();

                if (superUserPassword.isEmpty() || superUserPassword.length() < 8) {
                    JOptionPane.showMessageDialog(this, "Bitte geben Sie ein Passwort mit\nmindestens 8 Zeichen ein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (superUserPassword.equals(superUserRepeatPasswordTextField.getText())) {
                    superUser = new SuperUser(superUserPassword);

                    userMasterKey = decryptKeyWithString(superUser.getEncryptedUserMasterKeyBase64(), superUserPassword);
                    superUserMasterKey = decryptKeyWithString(superUser.getEncryptedSuperUMasterKeyBase64(), superUserPassword);
                } else {
                    JOptionPane.showMessageDialog(this, "Die Passwörter stimmen nicht überein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                previousButton.setEnabled(true);
                nextButton.setText("Fertigstellen");

                break;
            }
            case 2: {
                String password = userPasswordTextField.getText();

                if (password.isBlank() || password.length() < 8) {
                    JOptionPane.showMessageDialog(this, "Bitte geben Sie ein Passwort mit\nmindestens 8 Zeichen ein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.equals(userRepeatPasswordTextField.getText())) {
                    restrictedUser = new RestrictedUser(password, userMasterKey);
                } else {
                    JOptionPane.showMessageDialog(this, "Die Passwörter stimmen nicht überein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isFirstRun) {
                    //
                    // Create initial data
                    //
                    dataManager.getBuildingList().add(new Building("-"));
                    dataManager.getCurrentFloorList().add(new Floor("-"));
                    dataManager.getCurrentWalkList().add(new Walk("-"));
                    dataManager.getCurrentManagmentUnitList().add(new ModuleWrapper(new LockerCabinet()));
                } else {
                    List<Building> buildings = dataManager.getBuildingList();

                    for (Building building : buildings) {
                        List<Floor> floors = building.getFloors();

                        for (Floor floor : floors) {
                            List<Walk> walks = floor.getWalks();

                            for (Walk walk : walks) {
                                List<ModuleWrapper> mus = walk.getModuleWrappers();

                                for (ModuleWrapper mu : mus) {

                                    Module module = mu.getModule();

                                    if (!(module instanceof LockerCabinet)) {
                                        continue;
                                    }

                                    List<Locker> lockers = ((LockerCabinet) module).getLockers();

                                    for (Locker locker : lockers) {
                                        String[] codes = locker.getCodes(dataManager.getSuperUserMasterKey());

                                        for (int i = 0; i < 5; i++) {
                                            codes[i] = codes[i].replace("-", "");
                                        }

                                        locker.setCodes(codes, superUserMasterKey);
                                    }
                                }
                            }
                        }
                    }
                }

                dataManager.setNewUsers(superUser, restrictedUser, userMasterKey, superUserMasterKey);

                dataManager.saveAndCreateBackup();

                this.dispose();
            }
        } // end of switch

    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel dialogTitleLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JLabel suPasswordLabel;
    private javax.swing.JTextField superUserPasswordTextField;
    private javax.swing.JLabel suRepeatPasswordLabel;
    private javax.swing.JTextField superUserRepeatPasswordTextField;
    private javax.swing.JLabel superUserLabel;
    private javax.swing.JPanel superUserPanel;
    private javax.swing.JLabel userLabel;
    private javax.swing.JPanel userPanel;
    private javax.swing.JLabel userPasswordLabel;
    private javax.swing.JTextField userPasswordTextField;
    private javax.swing.JLabel userRepeatPasswordLabel;
    private javax.swing.JTextField userRepeatPasswordTextField;
    private javax.swing.JLabel welcomeMessageLabel;
    private javax.swing.JPanel welcomePanel;
    // End of variables declaration//GEN-END:variables
}
