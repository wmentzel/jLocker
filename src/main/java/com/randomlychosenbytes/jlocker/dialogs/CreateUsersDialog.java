package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.manager.Utils;
import com.randomlychosenbytes.jlocker.nonabstractreps.*;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.List;

import static com.randomlychosenbytes.jlocker.manager.Utils.generateAndEncryptKey;

public class CreateUsersDialog extends javax.swing.JDialog {
    private int iDisplayedCard;
    private final CardLayout cl;
    private boolean isFirstRun;
    private SecretKey ukey;
    private final DataManager dataManager;

    public CreateUsersDialog(final java.awt.Frame parent, DataManager dataManager, boolean modal) {
        super(parent, modal);
        initComponents();

        this.dataManager = dataManager;

        // focus in the middle
        setLocationRelativeTo(null);

        isFirstRun = dataManager.getBuildingList().isEmpty();

        addWindowListener
                (
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

        cl = new CardLayout();

        centerPanel.setLayout(cl);
        centerPanel.add(welcomePanel, "card1");
        centerPanel.add(superUserPanel, "card2");
        centerPanel.add(userPanel, "card3");

        iDisplayedCard = 0;

        if (!isFirstRun) {
            cl.next(centerPanel);
            iDisplayedCard = 1;
        }

        previousButton.setEnabled(false);
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

        centerPanel = new javax.swing.JPanel();
        welcomePanel = new javax.swing.JPanel();
        dialogTitleLabel = new javax.swing.JLabel();
        welcomeMessageLabel = new javax.swing.JLabel();
        superUserPanel = new javax.swing.JPanel();
        superUserLabel = new javax.swing.JLabel();
        suPasswordLabel = new javax.swing.JLabel();
        suPasswordTextField = new javax.swing.JTextField();
        suRepeatPasswordLabel = new javax.swing.JLabel();
        suRepeatPasswordTextField = new javax.swing.JTextField();
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
        superUserPanel.add(suPasswordTextField, gridBagConstraints);

        suRepeatPasswordLabel.setText("Passwort wiederholen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        superUserPanel.add(suRepeatPasswordLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        superUserPanel.add(suRepeatPasswordTextField, gridBagConstraints);

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

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        if (isFirstRun) {
            System.exit(0);
        } else {
            this.dispose();
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_previousButtonActionPerformed
    {//GEN-HEADEREND:event_previousButtonActionPerformed
        // Prevent the user from returning to the welcome screen, because
        // that would make no sense.
        if ((iDisplayedCard - 1) > 0) {
            iDisplayedCard--;
            cl.previous(centerPanel);
        }

        if (iDisplayedCard != 2) {
            nextButton.setText("Weiter >");
        }
    }//GEN-LAST:event_previousButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextButtonActionPerformed
    {//GEN-HEADEREND:event_nextButtonActionPerformed

        if (iDisplayedCard < 2) {
            cl.next(centerPanel); // display next card
        }

        iDisplayedCard++;

        switch (iDisplayedCard - 1) {
            case 1: {
                String supassword = suPasswordTextField.getText();

                if (supassword.equals("") || supassword.length() < 8) {
                    JOptionPane.showMessageDialog(this, "Bitte geben Sie ein Passwort mit\nmindestens 8 Zeichen ein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (supassword.equals(suRepeatPasswordTextField.getText())) {

                    dataManager.setSuperUser(new SuperUser(
                            supassword,
                            Utils.getHash(supassword),
                            generateAndEncryptKey(supassword),
                            generateAndEncryptKey(supassword)
                    ));
                    ukey = dataManager.getSuperUser().getUserMasterKey();
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

                if (password.equals("") || password.length() < 8) {
                    JOptionPane.showMessageDialog(this, "Bitte geben Sie ein Passwort mit\nmindestens 8 Zeichen ein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.equals(userRepeatPasswordTextField.getText()))
                    dataManager.setRestrictedUser(new RestrictedUser(password, ukey));
                else {
                    JOptionPane.showMessageDialog(this, "Die Passwörter stimmen nicht überein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (dataManager.getCurrentUser() == null) {
                    dataManager.setCurrentUser(dataManager.getSuperUser());
                }

                if (isFirstRun) {
                    //
                    // Create initial data
                    //
                    dataManager.getBuildingList().add(new Building("-", ""));
                    dataManager.getCurFloorList().add(new Floor("-"));
                    dataManager.getCurWalkList().add(new Walk("-"));
                    dataManager.getCurManagmentUnitList().add(new ManagementUnit(ManagementUnit.LOCKERCOLUMN));
                } else {
                    List<Building> buildings = dataManager.getBuildingList();

                    SecretKey masterKey = dataManager.getSuperUserMasterKey();
                    for (Building building : buildings) {
                        for (int f = 0; f < building.getFloorList().size(); f++) {
                            for (int w = 0; w < building.getFloorList().get(f).getWalkList().size(); w++) {
                                for (int c = 0; c < building.getFloorList().get(f).getWalkList().get(w).getManagementUnitList().size(); c++) {
                                    for (int l = 0; l < building.getFloorList().get(f).getWalkList().get(w).getManagementUnitList().get(c).getLockerList().size(); l++) {
                                        Locker locker = building.getFloorList().get(f).getWalkList().get(w).getManagementUnitList().get(c).getLockerList().get(l);
                                        String[] codes = locker.getCodes(masterKey);

                                        for (int i = 0; i < 5; i++) {
                                            codes[i] = codes[i].replace("-", "");
                                        }

                                        locker.setCodes(codes, masterKey);
                                    }
                                }
                            }
                        }
                    }
                }

                dataManager.saveAndCreateBackup();

                this.dispose();
            }
        } // end of switch 
    }//GEN-LAST:event_nextButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel dialogTitleLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JLabel suPasswordLabel;
    private javax.swing.JTextField suPasswordTextField;
    private javax.swing.JLabel suRepeatPasswordLabel;
    private javax.swing.JTextField suRepeatPasswordTextField;
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
