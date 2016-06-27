/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.randomlychosenbytes.jlocker.abstractreps;

import com.randomlychosenbytes.jlocker.dialogs.ChooseManagementUnitTypeDialog;
import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.Locker;
import com.randomlychosenbytes.jlocker.nonabstractreps.LockerCabinet;
import com.randomlychosenbytes.jlocker.nonabstractreps.Room;
import com.randomlychosenbytes.jlocker.nonabstractreps.Staircase;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Willi
 */
public class ManagementUnit extends javax.swing.JPanel
{
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -8054374141198601700L;
    
    private LockerCabinet cabinet;
    private Room room;
    private Staircase staircase;
    
    /**
     * Can either be 0 (ROOM), 1 (LOCKERCOLUMN) or 2 (STAIRCASE)
     */
    public int mType;
    
    public static final int ROOM = 0;
    public static final int LOCKERCOLUMN = 1;
    public static final int STAIRCASE = 2;

    public ManagementUnit(int kind)
    {
        initComponents();

        setAs(kind);
    }
    
    /* *************************************************************************
     * Getter
     **************************************************************************/
    public int getType() 
    { 
        return mType; 
    }

    public LockerCabinet getLockerCabinet() 
    { 
        return cabinet; 
    }
    public List<Locker> getLockerList() 
    { 
        return cabinet.getLockerList(); 
    }
    
    public Room getRoom()
    { 
        return room; 
    }
    
    public Staircase getStaircase()
    { 
        return staircase; 
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/
    
    public final void setAs(int kind)
    {
        centerPanel.removeAll(); // remove previous child
        mType = kind;
        
        cabinet = new LockerCabinet();
        room = new Room("", "");
        staircase = new Staircase();
        
        switch(kind)
        {
            case ROOM: 
            {
                centerPanel.add(room);         
                break;
            }
            case LOCKERCOLUMN:  
            {
                centerPanel.add(cabinet);      
                break;
            }
            case STAIRCASE:
            {    
                centerPanel.add(staircase);
                break;
            }
        }

        centerPanel.updateUI();
    }
    
    /**
     * After loading the Management Units from file all event listeners have
     * to be added again.
     */
    public void setUpMouseListeners()
    {
        if(addMUnitLeftLabel.getMouseListeners().length == 0)
        {
            addMUnitLeftLabel.addMouseListener(new java.awt.event.MouseAdapter() 
            {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt)
                {
                    addMUnitLeftLabelMouseReleased(evt);
                }
            });
        }

        if(removeThisMUnitLabel.getMouseListeners().length == 0)
        {
            removeThisMUnitLabel.addMouseListener(new java.awt.event.MouseAdapter() 
            {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt)
                {
                    removeThisMUnitLabelMouseReleased(evt);
                }
            });
        }

        if(transformLabel.getMouseListeners().length == 0)
        {
            transformLabel.addMouseListener(new java.awt.event.MouseAdapter() 
            {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) 
                {
                    transformLabelMouseReleased(evt);
                }
            });
        }

        if(addMUnitRightLabel.getMouseListeners().length == 0)
        {
            addMUnitRightLabel.addMouseListener(new java.awt.event.MouseAdapter() 
            {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) 
                {
                    addMUnitRightLabelMouseReleased(evt);
                }
            });
        }

        if(cabinet != null)
        {
            cabinet.setUpMouseListeners();
        }
        
        if(room != null)
        {
            room.setUpMouseListener();
        }
        
        if(staircase != null)
        {
            staircase.setUpMouseListener();
        }
    }

    
    public static String getNameFromID(final int type)
    {
        switch(type)
        {
            case LOCKERCOLUMN: 
            {
                return "Schließfachschrank";
            }
            case ROOM: 
            {
                return "Raum";
            }
            case STAIRCASE: 
            {
                return "Treppenhauszugang";
            }
            default: 
            {
                return "";
            }
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
        southPanel = new javax.swing.JPanel();
        addMUnitLeftLabel = new javax.swing.JLabel();
        transformLabel = new javax.swing.JLabel();
        removeThisMUnitLabel = new javax.swing.JLabel();
        addMUnitRightLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(177, 192, 138));
        setMinimumSize(new java.awt.Dimension(125, 72));
        setPreferredSize(new java.awt.Dimension(125, 72));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setBackground(new java.awt.Color(177, 192, 138));
        centerPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(centerPanel, gridBagConstraints);

        southPanel.setBackground(new java.awt.Color(177, 192, 138));

        addMUnitLeftLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        addMUnitLeftLabel.setForeground(new java.awt.Color(131, 150, 81));
        addMUnitLeftLabel.setText("+");
        addMUnitLeftLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addMUnitLeftLabelMouseReleased(evt);
            }
        });
        southPanel.add(addMUnitLeftLabel);

        transformLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gear.png"))); // NOI18N
        transformLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                transformLabelMouseReleased(evt);
            }
        });
        southPanel.add(transformLabel);

        removeThisMUnitLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        removeThisMUnitLabel.setForeground(new java.awt.Color(131, 150, 81));
        removeThisMUnitLabel.setText("-");
        removeThisMUnitLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                removeThisMUnitLabelMouseReleased(evt);
            }
        });
        southPanel.add(removeThisMUnitLabel);

        addMUnitRightLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        addMUnitRightLabel.setForeground(new java.awt.Color(131, 150, 81));
        addMUnitRightLabel.setText("+");
        addMUnitRightLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addMUnitRightLabelMouseReleased(evt);
            }
        });
        southPanel.add(addMUnitRightLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        add(southPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addMUnitLeftLabelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_addMUnitLeftLabelMouseReleased
    {//GEN-HEADEREND:event_addMUnitLeftLabelMouseReleased
        List<ManagementUnit> mus = DataManager.getInstance().getCurManagmentUnitList();

        int index = mus.indexOf(this);
        int iNewIndex;

        if(index == mus.size() - 1)
        {
            mus.add(new ManagementUnit(ManagementUnit.LOCKERCOLUMN));
            iNewIndex = mus.size() - 1;
        }
        else
        {
            mus.add(index + 1, new ManagementUnit(ManagementUnit.LOCKERCOLUMN));
            iNewIndex = index + 1;
        }

        DataManager.getInstance().setCurrentMUnitIndex(iNewIndex);
        DataManager.getInstance().setCurrentLockerIndex(0);

        DataManager.getInstance().getMainFrame().drawLockerOverview();
    }//GEN-LAST:event_addMUnitLeftLabelMouseReleased

    private void transformLabelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_transformLabelMouseReleased
    {//GEN-HEADEREND:event_transformLabelMouseReleased
        // TODO mainframe as first argument
        ChooseManagementUnitTypeDialog dialog = new ChooseManagementUnitTypeDialog(null, true, this);
        dialog.setVisible(true);
    }//GEN-LAST:event_transformLabelMouseReleased

    private void removeThisMUnitLabelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_removeThisMUnitLabelMouseReleased
    {//GEN-HEADEREND:event_removeThisMUnitLabelMouseReleased
        if(DataManager.getInstance().getCurManagmentUnitList().size() > 1)
        {
            String type = getNameFromID(mType);
            
            int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen " + type + " wirklich löschen?", "Löschen", JOptionPane.YES_NO_CANCEL_OPTION);

            if(answer == JOptionPane.YES_OPTION)
            {
                List<ManagementUnit> mus = DataManager.getInstance().getCurManagmentUnitList();
                mus.remove(this);
                DataManager.getInstance().getMainFrame().drawLockerOverview();
            }
        }
    }//GEN-LAST:event_removeThisMUnitLabelMouseReleased

    private void addMUnitRightLabelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_addMUnitRightLabelMouseReleased
    {//GEN-HEADEREND:event_addMUnitRightLabelMouseReleased
        List<ManagementUnit> mus = DataManager.getInstance().getCurManagmentUnitList();

        int index = mus.indexOf(this);

        if(index == 0)
        {
            mus.add(0,new ManagementUnit(ManagementUnit.LOCKERCOLUMN));
        }
        else
        {
            mus.add(index, new ManagementUnit(ManagementUnit.LOCKERCOLUMN));
        }

        DataManager.getInstance().setCurrentMUnitIndex(index);
        DataManager.getInstance().setCurrentLockerIndex(0);

        DataManager.getInstance().getMainFrame().drawLockerOverview();
    }//GEN-LAST:event_addMUnitRightLabelMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addMUnitLeftLabel;
    private javax.swing.JLabel addMUnitRightLabel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel removeThisMUnitLabel;
    private javax.swing.JPanel southPanel;
    private javax.swing.JLabel transformLabel;
    // End of variables declaration//GEN-END:variables
}
