package com.randomlychosenbytes.jlocker.model;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.dialogs.RoomDialog;

import static com.randomlychosenbytes.jlocker.ColorsKt.getRoomBackgroundColor;
import static com.randomlychosenbytes.jlocker.ColorsKt.getRoomFontColor;

public class Room extends Module {

    @Expose
    private String schoolClassName;

    @Expose
    private String name;

    public Room(String name, String classname) {
        initComponents();
        setCaption(name, classname);
    }

    public Room() {
        initComponents();
        setCaption("", "");
    }

    public final void setCaption(String name, String schoolClassName) {
        this.name = name;
        this.schoolClassName = schoolClassName;

        String caption = "<html><div align=\"center\">" + this.name;

        // if there was a class name specified
        if (!this.schoolClassName.isEmpty()) {
            caption += "<br><br><div style='font-size:12pt;'>Klasse<br>" + this.schoolClassName + "</div>";
        }

        caption += "</div></html>";
        captionLabel.setText(caption);
    }

    public String getRoomName() {
        return name;
    }

    public String getSchoolClassName() {
        return schoolClassName;
    }

    @Override
    public String toString() {
        return "Raum";
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

        setBackground(getRoomBackgroundColor());
        setLayout(new java.awt.BorderLayout());

        captionLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        captionLabel.setForeground(getRoomFontColor());
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
        RoomDialog dialog = new RoomDialog(null, true, this);
        dialog.setVisible(true);
    }//GEN-LAST:event_captionLabelMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel captionLabel;
    // End of variables declaration//GEN-END:variables
}
