package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.model.Staircase;

import javax.swing.*;
import java.awt.*;

public class StaircaseDialog extends JDialog {
    private final Staircase staircase;

    public StaircaseDialog(Frame parent, boolean modal, Staircase staircase) {
        super(parent, modal);
        initComponents();

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        // focus in the middle
        setLocationRelativeTo(null);

        this.staircase = staircase;

        staircaseNameTextBox.setText(staircase.getStaircaseName());
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        staircasenrLabel = new javax.swing.JLabel();
        staircaseNameTextBox = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Treppenaufgang");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.GridBagLayout());

        staircasenrLabel.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        centerPanel.add(staircasenrLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        centerPanel.add(staircaseNameTextBox, gridBagConstraints);

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

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        staircase.setCaption(staircaseNameTextBox.getText());
        dispose();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField staircaseNameTextBox;
    private javax.swing.JLabel staircasenrLabel;

}
