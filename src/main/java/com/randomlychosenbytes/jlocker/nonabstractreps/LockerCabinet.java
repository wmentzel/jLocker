package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.manager.DataManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class LockerCabinet extends JPanel {

    @Expose
    private List<Locker> lockers;

    public LockerCabinet() {
        initComponents();

        lockers = new LinkedList<>();

        updateCabinet(0);
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/

    public void setLockers(List<Locker> lockers) {
        this.lockers = lockers;
    }

    public void addLocker(Locker locker, boolean first) {
        if (first) {
            lockers.add(0, locker);

            int rows = 0;

            List<ManagementUnit> mus = DataManager.getInstance().getCurManagmentUnitList();

            for (ManagementUnit mu : mus) {
                int size = mu.getLockerCabinet().getLockerList().size();

                if (size > rows) {
                    rows = size;
                }
            }

            for (ManagementUnit mu : mus) {
                mu.getLockerCabinet().updateCabinet(rows);
            }
        } else {
            lockers.add(locker);
            cabinetPanel.add(locker);
        }

        remLockerLabel.setEnabled(true);
    }

    private void updateCabinet(int rows) {
        cabinetPanel.removeAll();
        cabinetPanel.setLayout(new GridLayout(0, 1, 0, 10));

        if (rows > lockers.size()) {
            for (int i = 0; i < rows - lockers.size(); i++) {
                cabinetPanel.add(new JLabel());
            }
        }

        for (Locker locker : lockers) {
            cabinetPanel.add(locker);
        }

        cabinetPanel.updateUI();
    }

    public void setUpMouseListeners() {
        if (addLockerLabel.getMouseListeners().length == 0) {
            addLockerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    addLockerLabelMouseReleased(evt);
                }
            });
        }

        if (remLockerLabel.getMouseListeners().length == 0) {
            remLockerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    remLockerLabelMouseReleased(evt);
                }
            });
        }

        for (Locker locker : lockers) {
            locker.setUpMouseListener();
        }
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/

    public int getLockerRowByID(String id) {
        for (int l = 0; l < lockers.size(); l++) {
            if (lockers.get(l).getId().equals(id))
                return l;
        }

        return 0;
    }

    public List<Locker> getLockerList() {
        return lockers;
    }

    public List<Locker> getLockers() {
        return lockers;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        addLockerLabel = new javax.swing.JLabel();
        remLockerLabel = new javax.swing.JLabel();
        cabinetPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        buttonPanel.setBackground(new java.awt.Color(177, 192, 138));

        addLockerLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        addLockerLabel.setForeground(new java.awt.Color(131, 150, 81));
        addLockerLabel.setText("+");
        addLockerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addLockerLabelMouseReleased(evt);
            }
        });
        buttonPanel.add(addLockerLabel);

        remLockerLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        remLockerLabel.setForeground(new java.awt.Color(131, 150, 81));
        remLockerLabel.setText("-");
        remLockerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                remLockerLabelMouseReleased(evt);
            }
        });
        buttonPanel.add(remLockerLabel);

        add(buttonPanel, java.awt.BorderLayout.NORTH);

        cabinetPanel.setBackground(new java.awt.Color(177, 192, 138));

        javax.swing.GroupLayout cabinetPanelLayout = new javax.swing.GroupLayout(cabinetPanel);
        cabinetPanel.setLayout(cabinetPanelLayout);
        cabinetPanelLayout.setHorizontalGroup(
                cabinetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 170, Short.MAX_VALUE)
        );
        cabinetPanelLayout.setVerticalGroup(
                cabinetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 233, Short.MAX_VALUE)
        );

        add(cabinetPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addLockerLabelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_addLockerLabelMouseReleased
    {//GEN-HEADEREND:event_addLockerLabelMouseReleased
        addLocker(new Locker("", "", "", 0, "", "", "", false, 0, 0, "", false, ""), true);
    }//GEN-LAST:event_addLockerLabelMouseReleased

    private void remLockerLabelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_remLockerLabelMouseReleased
    {//GEN-HEADEREND:event_remLockerLabelMouseReleased
        if (!lockers.isEmpty()) {
            int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie dieses Schließfach wirklich löschen?", "Löschen", JOptionPane.YES_NO_CANCEL_OPTION);

            if (answer == JOptionPane.YES_OPTION) {
                lockers.remove(0); // remove first

                int rows = 0;

                List<ManagementUnit> mus = DataManager.getInstance().getCurManagmentUnitList();

                for (ManagementUnit mu : mus) {
                    int size = mu.getLockerCabinet().getLockerList().size();
                    if (size > rows)
                        rows = size;
                }

                for (ManagementUnit mu : mus) {
                    mu.getLockerCabinet().updateCabinet(rows);
                }
            }
        }
    }//GEN-LAST:event_remLockerLabelMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addLockerLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel cabinetPanel;
    private javax.swing.JLabel remLockerLabel;
    // End of variables declaration//GEN-END:variables
}
