package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.dialogs.StaircaseDialog;

public class Staircase extends javax.swing.JPanel {

    @Expose
    private String sName;

    public Staircase() {
        initComponents();
    }

    public void setEntityName(String n) {
        sName = n;
        captionLabel.setText("<html><div align=\"center\">Treppenhaus<br><br><div style='font-size:12pt;'>" + sName + "</div></div></html>");
    }

    public void setSName(String n) {
        sName = n;
    }

    public void setUpMouseListener() {
        if (this.getMouseListeners().length == 0) {
            captionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    captionLabelMouseReleased(evt);
                }
            });
        }
    }

    public String getSName() {
        return sName;
    }

    public String getEntityName() {
        return sName;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        captionLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(153, 153, 153));
        setLayout(new java.awt.BorderLayout());

        captionLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        captionLabel.setForeground(new java.awt.Color(255, 255, 255));
        captionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        captionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                captionLabelMouseReleased(evt);
            }
        });
        add(captionLabel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void captionLabelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_captionLabelMouseReleased
    {//GEN-HEADEREND:event_captionLabelMouseReleased
        // TODO mainframe as first argument
        StaircaseDialog dialog = new StaircaseDialog(null, true, this);
        dialog.setVisible(true);
    }//GEN-LAST:event_captionLabelMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel captionLabel;
    // End of variables declaration//GEN-END:variables
}
