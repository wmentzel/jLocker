package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.DataManager;
import com.randomlychosenbytes.jlocker.State;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SettingsDialog extends JDialog {
    private final DataManager dataManager = State.Companion.getDataManager();

    public SettingsDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        // focus in the middle
        setLocationRelativeTo(null);

        int numBackups = dataManager.getSettings().getNumOfBackups();
        numBackupsSlider.setValue(numBackups);
        numBackupsTextField.setText(Integer.toString(numBackups));

        updateLockerMinSizesTextField();
    }

    public final void updateLockerMinSizesTextField() {
        String text = "";

        List<Integer> minSizes = dataManager.getSettings().getLockerMinSizes();

        for (int i = 0; i < minSizes.size(); i++) {
            if (i != 0) {
                text += ", ";
            }

            text += minSizes.get(minSizes.size() - i - 1).toString();
        }

        lockerMinSizesTextField.setText(text);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        backupPanel = new javax.swing.JPanel();
        numBackupsLabel = new javax.swing.JLabel();
        numBackupsTextField = new javax.swing.JTextField();
        numBackupsSlider = new javax.swing.JSlider();
        lockerMinSizesLabel = new javax.swing.JLabel();
        lockerMinSizesTextField = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Einstellungen");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        mainPanel.setLayout(new java.awt.GridLayout(2, 2, 10, 10));

        backupPanel.setLayout(new java.awt.GridBagLayout());

        numBackupsLabel.setText("Anzahl der Backups");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        backupPanel.add(numBackupsLabel, gridBagConstraints);

        numBackupsTextField.setEditable(false);
        numBackupsTextField.setColumns(3);
        backupPanel.add(numBackupsTextField, new java.awt.GridBagConstraints());

        mainPanel.add(backupPanel);

        numBackupsSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                numBackupsSliderStateChanged(evt);
            }
        });
        mainPanel.add(numBackupsSlider);

        lockerMinSizesLabel.setText("Mindestgrößen für Schließfächer");
        mainPanel.add(lockerMinSizesLabel);

        lockerMinSizesTextField.setEditable(false);
        lockerMinSizesTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lockerMinSizesTextFieldMouseClicked(evt);
            }
        });
        mainPanel.add(lockerMinSizesTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(mainPanel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        getContentPane().add(buttonPanel, gridBagConstraints);

        pack();
    }// </editor-fold>

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dataManager.getSettings().setNumOfBackups(numBackupsSlider.getValue());

        this.dispose();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void numBackupsSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        int num = numBackupsSlider.getValue();
        numBackupsTextField.setText(Integer.toString(num));
    }

    private void lockerMinSizesTextFieldMouseClicked(java.awt.event.MouseEvent evt) {
        new SetLockerMinimumSizesDialog(this, true).setVisible(true);
    }


    private javax.swing.JPanel backupPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel lockerMinSizesLabel;
    private javax.swing.JTextField lockerMinSizesTextField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel numBackupsLabel;
    private javax.swing.JSlider numBackupsSlider;
    private javax.swing.JTextField numBackupsTextField;
    private javax.swing.JButton okButton;

}
