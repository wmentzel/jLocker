package com.randomlychosenbytes.jlocker.model;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.dialogs.StaircaseDialog;

import static com.randomlychosenbytes.jlocker.ColorsKt.getStaircaseBackgroundColor;
import static com.randomlychosenbytes.jlocker.ColorsKt.getStaircaseFontColor;

public class Staircase extends Module {

    @Expose
    private String name;

    public Staircase(String name) {
        initComponents();
        setCaption(name);
    }

    public Staircase() {
        initComponents();
    }

    public String getStaircaseName() {
        return name;
    }

    public void setCaption(String name) {
        this.name = name;
        captionLabel.setText("<html><div align=\"center\">Treppenhaus<br><br><div style='font-size:12pt;'>" + this.name + "</div></div></html>");
    }

    @Override
    public String toString() {
        return "Treppenhaus";
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        captionLabel = new javax.swing.JLabel();

        setBackground(getStaircaseBackgroundColor());
        setLayout(new java.awt.BorderLayout());

        captionLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        captionLabel.setForeground(getStaircaseFontColor());
        captionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        captionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                captionLabelMouseReleased(evt);
            }
        });
        add(captionLabel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>

    private void captionLabelMouseReleased(java.awt.event.MouseEvent evt) {
        // TODO mainframe as first argument
        StaircaseDialog dialog = new StaircaseDialog(null, true, this);
        dialog.setVisible(true);
    }

    private javax.swing.JLabel captionLabel;

}
