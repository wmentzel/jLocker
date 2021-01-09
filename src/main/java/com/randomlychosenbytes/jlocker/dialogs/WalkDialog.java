package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.DataManager;
import com.randomlychosenbytes.jlocker.MainFrame;
import com.randomlychosenbytes.jlocker.State;
import com.randomlychosenbytes.jlocker.model.LockerCabinet;
import com.randomlychosenbytes.jlocker.model.ModuleWrapper;
import com.randomlychosenbytes.jlocker.model.Walk;

import javax.swing.*;
import java.awt.*;

public class WalkDialog extends JDialog {
    public static final int EDIT = 0;
    public static final int ADD = 1;

    final int bMode;
    private final DataManager dataManager = State.Companion.getDataManager();

    public WalkDialog(Frame parent, boolean modal, int mode) {
        super(parent, modal);
        initComponents();

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        // focus in the middle
        setLocationRelativeTo(null);

        bMode = mode;

        if (bMode == EDIT) {
            setTitle("Gangname bearbeiten");
            entityNameTextField.setText(dataManager.getCurrentWalk().getName());
        } else {
            setTitle("Gang hinzufügen");
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        entityNameLabel = new javax.swing.JLabel();
        entityNameTextField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.GridBagLayout());

        entityNameLabel.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        centerPanel.add(entityNameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        centerPanel.add(entityNameTextField, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        centerPanel.add(okButton, gridBagConstraints);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        centerPanel.add(cancelButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(centerPanel, gridBagConstraints);

        pack();
    }// </editor-fold>

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (bMode == EDIT) {
            dataManager.getCurrentWalk().setName(entityNameTextField.getText());
        } else {
            String name = entityNameTextField.getText();

            if (name.isBlank()) {
                JOptionPane.showMessageDialog(this, "Bitte geben Sie einen Namen ein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dataManager.getCurrentWalkList().add(new Walk(name));
            dataManager.setCurrentWalkIndex(dataManager.getCurrentWalkList().size() - 1);

            dataManager.getCurrentWalk().getModuleWrappers().add(new ModuleWrapper(new LockerCabinet()));
            dataManager.setCurrentManagementUnitIndex(dataManager.getCurrentWalk().getModuleWrappers().size() - 1);

            dataManager.setCurrentLockerIndex(0);
        }

        ((MainFrame) this.getParent()).updateComboBoxes();

        dispose();
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel entityNameLabel;
    private javax.swing.JTextField entityNameTextField;
    private javax.swing.JButton okButton;

}
