package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.model.LockerCabinet;
import com.randomlychosenbytes.jlocker.model.Module;
import com.randomlychosenbytes.jlocker.model.Room;
import com.randomlychosenbytes.jlocker.model.Staircase;
import com.randomlychosenbytes.jlocker.uicomponents.ModulePanel;

import javax.swing.*;

public class ChooseManagementUnitTypeDialog extends javax.swing.JDialog {

    ModulePanel modulePanel;

    /**
     * Creates new form ChooseMUnitTypeDialog
     */
    public ChooseManagementUnitTypeDialog(java.awt.Frame parent, boolean modal, ModulePanel modulePanel) {
        super(parent, modal);
        initComponents();

        // focus in the middle
        setLocationRelativeTo(null);

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        this.modulePanel = modulePanel;

        // Deactivate the radio button that represents the current type

        if (this.modulePanel.getModule() instanceof LockerCabinet) {
            lockerButton.setEnabled(false);
        }

        if (this.modulePanel.getModule() instanceof Room) {
            roomButton.setEnabled(false);
        }

        if (this.modulePanel.getModule() instanceof Staircase) {
            staircaseButton.setEnabled(false);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        northPanel = new javax.swing.JPanel();
        questionLabel = new javax.swing.JLabel();
        radioButtonPanel = new javax.swing.JPanel();
        lockerButton = new javax.swing.JRadioButton();
        roomButton = new javax.swing.JRadioButton();
        staircaseButton = new javax.swing.JRadioButton();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Typ");
        setResizable(false);

        northPanel.setLayout(new java.awt.GridBagLayout());

        questionLabel.setText("Was wollen Sie einrichten?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        northPanel.add(questionLabel, gridBagConstraints);

        radioButtonPanel.setLayout(new java.awt.GridLayout(3, 1));

        buttonGroup.add(lockerButton);
        lockerButton.setText("Schließfachschrank");
        radioButtonPanel.add(lockerButton);

        buttonGroup.add(roomButton);
        roomButton.setText("Tür");
        radioButtonPanel.add(roomButton);

        buttonGroup.add(staircaseButton);
        staircaseButton.setText("Treppenhauszugang");
        radioButtonPanel.add(staircaseButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        northPanel.add(radioButtonPanel, gridBagConstraints);

        getContentPane().add(northPanel, java.awt.BorderLayout.PAGE_START);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {

        Module module;

        if (lockerButton.isSelected()) {
            module = new LockerCabinet();
        } else if (roomButton.isSelected()) {
            module = new Room("", "");
        } else {
            module = new Staircase("");
        }

        String text = "Wollen Sie diesen "
                + modulePanel.getModule()
                + " in einen "
                + module
                + " transformieren?";

        int answer = JOptionPane.showConfirmDialog(null, text, "Schließfach leeren", JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            modulePanel.setModule(module);
        }

        // close after the selection
        this.dispose();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }


    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton lockerButton;
    private javax.swing.JPanel northPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel questionLabel;
    private javax.swing.JPanel radioButtonPanel;
    private javax.swing.JRadioButton roomButton;
    private javax.swing.JRadioButton staircaseButton;

}
