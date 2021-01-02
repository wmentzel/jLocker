package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.DataManager;
import com.randomlychosenbytes.jlocker.State;
import com.randomlychosenbytes.jlocker.model.Locker;
import com.randomlychosenbytes.jlocker.model.Task;

import javax.swing.*;
import java.awt.*;

import static com.randomlychosenbytes.jlocker.UtilsKt.moveOwner;

public class MoveLockerDialog extends JDialog {
    private final DataManager dataManager = State.Companion.getDataManager();

    public MoveLockerDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        // focus in the middle
        setLocationRelativeTo(null);
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
        sourceIDLabel = new javax.swing.JLabel();
        sourceIDTextField = new javax.swing.JTextField();
        destinationIDLabel = new javax.swing.JLabel();
        destinationIDTextField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Schließfachumzug");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.GridBagLayout());

        sourceIDLabel.setText("Quelle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        centerPanel.add(sourceIDLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        centerPanel.add(sourceIDTextField, gridBagConstraints);

        destinationIDLabel.setText("Ziel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        centerPanel.add(destinationIDLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        centerPanel.add(destinationIDTextField, gridBagConstraints);

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
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        Locker sourceLocker = dataManager.getLockerById(sourceIDTextField.getText());

        if (sourceLocker == null) {
            JOptionPane.showMessageDialog(this, "Das Quellschließfach existiert nicht!", "Fehler", JOptionPane.OK_OPTION);
            return;
        }

        Locker destLocker = dataManager.getLockerById(destinationIDTextField.getText());

        if (destLocker == null) {
            JOptionPane.showMessageDialog(this, "Das Zielschließfach existiert nicht!", "Fehler", JOptionPane.OK_OPTION);
            return;
        }

        moveOwner(sourceLocker, destLocker);

        if (!sourceLocker.isFree()) {
            // TODO check null pointer dereferencing getSurname()
            String task1 = sourceLocker.getPupil().getLastName() + ", " + sourceLocker.getPupil().getFirstName() + " (" + sourceLocker.getPupil().getSchoolClassName() + ") über Umzug informieren (" + destLocker.getId() + " -> " + sourceLocker.getId() + ")";
            dataManager.getTasks().add(new Task(task1));
        }

        if (!destLocker.isFree()) {
            String task2 = destLocker.getPupil().getLastName() + ", " + destLocker.getPupil().getFirstName() + " (" + destLocker.getPupil().getSchoolClassName() + ") über Umzug informieren (" + sourceLocker.getId() + " -> " + destLocker.getId() + ")";
            dataManager.getTasks().add(new Task(task2));
        }
        sourceIDTextField.setText("");
        destinationIDTextField.setText("");

        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel destinationIDLabel;
    private javax.swing.JTextField destinationIDTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel sourceIDLabel;
    private javax.swing.JTextField sourceIDTextField;
    // End of variables declaration//GEN-END:variables
}
