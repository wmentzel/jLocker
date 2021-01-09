package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.State;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class SetLockerMinimumSizesDialog extends javax.swing.JDialog {
    private final List<JTextField> textFields = new LinkedList<>();
    private final List<Integer> minSizes = State.Companion.getDataManager().getSettings().getLockerMinSizes();

    public SetLockerMinimumSizesDialog(JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // focus in the middle
        setLocationRelativeTo(null);

        buildLayout();
    }

    private void buildLayout() {
        GridLayout gl = new GridLayout(minSizes.size() + 1, 2);
        gl.setHgap(5);
        gl.setVgap(5);
        centerPanel.setLayout(gl);

        centerPanel.add(new JLabel("Schließfach"));
        centerPanel.add(new JLabel("Mindestgröße (cm)"));

        for (int i = 0; i < minSizes.size(); i++) {
            centerPanel.add(new JLabel(Integer.toString(minSizes.size() - i)));

            JTextField textField = new JTextField(minSizes.get(minSizes.size() - i - 1).toString());
            textFields.add(textField);
            centerPanel.add(textField);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mindestgrößen");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout centerPanelLayout = new javax.swing.GroupLayout(centerPanel);
        centerPanel.setLayout(centerPanelLayout);
        centerPanelLayout.setHorizontalGroup(
                centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 198, Short.MAX_VALUE)
        );
        centerPanelLayout.setVerticalGroup(
                centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 178, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(centerPanel, gridBagConstraints);

        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(okButton);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(bottomPanel, gridBagConstraints);

        pack();
    }// </editor-fold>

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        for (int i = 0; i < minSizes.size(); i++) {
            try {
                Integer n = Integer.parseInt(textFields.get(i).getText());
                minSizes.set(minSizes.size() - i - 1, n);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Bitte nur ganze Zahlen eingeben!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        ((SettingsDialog) this.getParent()).updateLockerMinSizesTextField();

        this.dispose();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton okButton;

}
