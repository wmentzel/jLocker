package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.State;
import com.randomlychosenbytes.jlocker.model.Module;
import com.randomlychosenbytes.jlocker.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RenameClassDialog extends JDialog {

    public RenameClassDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // center on screen
        setLocationRelativeTo(null);

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        classLabel = new javax.swing.JLabel();
        classTextField = new javax.swing.JTextField();
        changeToLabel = new javax.swing.JLabel();
        changeToTextField = new javax.swing.JTextField();
        southPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Klasse umbennen");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.GridLayout(2, 2, 20, 10));

        classLabel.setText("Klasse");
        centerPanel.add(classLabel);
        centerPanel.add(classTextField);

        changeToLabel.setText("ändern zu");
        centerPanel.add(changeToLabel);
        centerPanel.add(changeToTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(centerPanel, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        southPanel.add(okButton);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        southPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(southPanel, gridBagConstraints);

        pack();
    }// </editor-fold>

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {

        String newClassName = changeToTextField.getText();
        String previousClassName = classTextField.getText();
        boolean searchForAgeGroup = previousClassName.indexOf('.') == -1;
        int numMatches = 0;

        List<Building> buildings = State.Companion.getDataManager().getBuildingList();

        for (Building building : buildings) {
            List<Floor> floors = building.getFloors();

            for (Floor floor : floors) {
                List<Walk> walks = floor.getWalks();

                for (Walk walk : walks) {
                    List<ModuleWrapper> cols = walk.getModuleWrappers();

                    for (ModuleWrapper col : cols) {

                        Module module = col.getModule();

                        if (!(module instanceof LockerCabinet)) {
                            continue;
                        }

                        List<Locker> lockers = ((LockerCabinet) module).getLockers();
                        String sSubClass;

                        for (Locker locker : lockers) {

                            if (locker.isFree()) {
                                continue;
                            }

                            String sFoundClass = locker.getPupil().getSchoolClassName();
                            sSubClass = "";

                            //7, E, Kurs
                            if (searchForAgeGroup) // with dot
                            {
                                int iDotIndex = sFoundClass.indexOf('.');

                                if (iDotIndex != -1) {
                                    sSubClass = sFoundClass.substring(iDotIndex);
                                    sFoundClass = sFoundClass.substring(0, iDotIndex);
                                }

                            }
                            // else 7.2, E.2 ...

                            if (sFoundClass.equals(previousClassName)) {
                                numMatches++;
                                locker.getPupil().setSchoolClassName(newClassName + sSubClass);
                            }
                        }
                    }
                }
            }
        }

        if (numMatches == 0) {
            JOptionPane.showMessageDialog(null, "Kann Schließfachmieter besucht die Klasse " + previousClassName + "!", "Fehler", JOptionPane.ERROR_MESSAGE);
        } else {
            this.dispose();
            JOptionPane.showMessageDialog(null, "Die Klasse von " + numMatches + " Schließfachmietern wurde erfolgreich geändert!", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel changeToLabel;
    private javax.swing.JTextField changeToTextField;
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField classTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel southPanel;

}
