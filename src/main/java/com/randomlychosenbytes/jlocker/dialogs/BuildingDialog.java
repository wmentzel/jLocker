package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.main.MainFrame;
import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;
import com.randomlychosenbytes.jlocker.nonabstractreps.Floor;
import com.randomlychosenbytes.jlocker.nonabstractreps.Walk;

public class BuildingDialog extends javax.swing.JDialog {
    public static final int EDIT = 0;
    public static final int ADD = 1;
    private final DataManager dataManager;
    int mode;

    public BuildingDialog(java.awt.Frame parent, boolean modal, DataManager dataManager, int mode) {
        super(parent, modal);
        initComponents();

        this.dataManager = dataManager;

        // center dialog on screen
        setLocationRelativeTo(null);

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        this.mode = mode;

        if (this.mode == EDIT) {
            setTitle("Gebäudename bearbeiten");
            entityNameTextField.setText(dataManager.getCurBuilding().getName());
        } else {
            setTitle("Gebäude hinzufügen");
        }
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
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        if (mode == EDIT) {
            dataManager.getCurBuilding().setName(entityNameTextField.getText());
        } else {
            dataManager.getBuildingList().add(new Building(entityNameTextField.getText(), ""));
            dataManager.setCurrentBuildingIndex(dataManager.getBuildingList().size() - 1);

            dataManager.getCurFloorList().add(new Floor("-"));
            dataManager.setCurrentFloorIndex(0);

            dataManager.getCurWalkList().add(new Walk("-"));
            dataManager.setCurrentWalkIndex(0);

            dataManager.getCurManagmentUnitList().add(new ManagementUnit(ManagementUnit.LOCKER_CABINET));
            dataManager.setCurrentMUnitIndex(dataManager.getCurManagmentUnitList().size() - 1);

            dataManager.setCurrentLockerIndex(0);
        }

        ((MainFrame) this.getParent()).setComboBoxes2CurIndizes();

        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel entityNameLabel;
    private javax.swing.JTextField entityNameTextField;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
