package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.model.Room;

import javax.swing.*;
import java.awt.*;

public class RoomDialog extends JDialog {
    private final Room room;

    public RoomDialog(Frame parent, boolean modal, Room room) {
        super(parent, modal);
        initComponents();

        // focus in the middle
        setLocationRelativeTo(null);

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        this.room = room;

        roomNameTextField.setText(room.getRoomName());
        classNameTextField.setText(room.getSchoolClassName());
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
        roomNameLabel = new javax.swing.JLabel();
        roomNameTextField = new javax.swing.JTextField();
        classNameLabel = new javax.swing.JLabel();
        classNameTextField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Raum");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.GridBagLayout());

        roomNameLabel.setText("Raumname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        centerPanel.add(roomNameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        centerPanel.add(roomNameTextField, gridBagConstraints);

        classNameLabel.setText("Klassenname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        centerPanel.add(classNameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        centerPanel.add(classNameTextField, gridBagConstraints);

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

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        String name = roomNameTextField.getText();

        if (name.isBlank()) {
            JOptionPane.showMessageDialog(null, "Sie müssen einen Raumnamen angeben!", "Fehler", JOptionPane.OK_OPTION);
        }

        room.setCaption(name, classNameTextField.getText());

        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel classNameLabel;
    private javax.swing.JTextField classNameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel roomNameLabel;
    private javax.swing.JTextField roomNameTextField;
    // End of variables declaration//GEN-END:variables
}
