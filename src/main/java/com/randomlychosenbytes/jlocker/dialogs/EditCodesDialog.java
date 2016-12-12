/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.randomlychosenbytes.jlocker.dialogs;

import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.randomlychosenbytes.jlocker.main.MainFrame;
import com.randomlychosenbytes.jlocker.manager.DataManager;

/**
 *
 * @author Willi
 */
public class EditCodesDialog extends javax.swing.JDialog
{    
    private final JTextField codeTextFields[] = new JTextField[5];
    private int iCurCodeIndex;
    DataManager dataManager;
    
    /**
     * Creates new form EditCodesDialog
     * @param parent
     * @param dataManager
     * @param modal
     */
    public EditCodesDialog(java.awt.Frame parent, DataManager dataManager, boolean modal)
    {
        super(parent, modal);
        initComponents();
        
        this.dataManager = dataManager;
        
        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);
        
        // focus in the middle
        setLocationRelativeTo(null);
        
        iCurCodeIndex = dataManager.getCurLocker().getCurrentCodeIndex();

        codeTextFields[0] = codeOneTextField;
        codeTextFields[1] = codeTwoTextField;
        codeTextFields[2] = codeThreeTextField;
        codeTextFields[3] = codeFourTextField;
        codeTextFields[4] = codeFiveTextField;

        String codes[] = dataManager.getCurLocker().getCodes(dataManager.getCurUser().getSuperUMasterKey());

        for(int i = 0; i < 5; i++)
        {
             codeTextFields[i].setText(codes[i]);

             if(i == iCurCodeIndex)
             {
                 codeTextFields[i].setBackground(new Color(0, 102, 0));
             }
        }
    }

    private void setCurCode(java.awt.event.MouseEvent evt)
    {
        if(!allowEditCheckBox.isSelected())
        {
            codeTextFields[iCurCodeIndex].setBackground(new Color(240, 240, 240));

            for(int i = 0; i < 5; i++)
            {
                if(codeTextFields[i] == evt.getSource())
                {
                    iCurCodeIndex = i;
                }
            }

            codeTextFields[iCurCodeIndex].setBackground(new Color(0, 102, 0));
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        codeOneTextField = new javax.swing.JTextField();
        codeTwoTextField = new javax.swing.JTextField();
        codeThreeTextField = new javax.swing.JTextField();
        codeFourTextField = new javax.swing.JTextField();
        codeFiveTextField = new javax.swing.JTextField();
        allowEditCheckBox = new javax.swing.JCheckBox();
        bottomPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Codes bearbeiten");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        centerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Verfügbare Codes"));
        centerPanel.setLayout(new java.awt.GridLayout(6, 1, 0, 10));

        codeOneTextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                codeOneTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeOneTextField);

        codeTwoTextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                codeTwoTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeTwoTextField);

        codeThreeTextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                codeThreeTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeThreeTextField);

        codeFourTextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                codeFourTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeFourTextField);

        codeFiveTextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                codeFiveTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeFiveTextField);

        allowEditCheckBox.setText("Editieren erlauben");
        allowEditCheckBox.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                allowEditCheckBoxItemStateChanged(evt);
            }
        });
        centerPanel.add(allowEditCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        getContentPane().add(centerPanel, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                okButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(okButton);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(cancelButton);

        getContentPane().add(bottomPanel, new java.awt.GridBagConstraints());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
                dataManager.getCurLocker().setCurrentCodeIndex(iCurCodeIndex);
        String codes[] = new String[5];

        for(int i = 0; i < 5; i++)
        {
             boolean isValid = false;

            String code = codeTextFields[i].getText();
            
            code = code.replace("-", "");
            code = code.replace(" ", "");

            if(code.length() == 6)
            {
                try
                {
                    new Integer(code);
                    isValid = true;
                }
                catch(NumberFormatException e)
                {
                }
            }

            if(isValid == false)
            {
                JOptionPane.showMessageDialog(null, "Der " + new Integer(i).toString() + ". Code ist ungültig!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            codes[i] = code;
        }

        dataManager.getCurLocker().setCodes(codes, dataManager.getCurUser().getSuperUMasterKey());
        
        ((MainFrame)this.getParent()).showLockerInformation();

        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void allowEditCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_allowEditCheckBoxItemStateChanged
    {//GEN-HEADEREND:event_allowEditCheckBoxItemStateChanged
        boolean editable = allowEditCheckBox.isSelected();
        
        for(int i = 0; i < 5; i++)
        {
            codeTextFields[i].setEditable(editable);

            if(editable)
                codeTextFields[i].setBackground(new Color(255, 255, 255));
            else
                codeTextFields[i].setBackground(new Color(240, 240, 240));
        }

        if(!editable)
        {
            iCurCodeIndex = 0;
            codeTextFields[iCurCodeIndex].setBackground(new Color(0, 102, 0));
        }
        
    }//GEN-LAST:event_allowEditCheckBoxItemStateChanged

    private void codeOneTextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_codeOneTextFieldMouseClicked
    {//GEN-HEADEREND:event_codeOneTextFieldMouseClicked
        setCurCode(evt);
    }//GEN-LAST:event_codeOneTextFieldMouseClicked

    private void codeTwoTextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_codeTwoTextFieldMouseClicked
    {//GEN-HEADEREND:event_codeTwoTextFieldMouseClicked
        setCurCode(evt);
    }//GEN-LAST:event_codeTwoTextFieldMouseClicked

    private void codeThreeTextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_codeThreeTextFieldMouseClicked
    {//GEN-HEADEREND:event_codeThreeTextFieldMouseClicked
        setCurCode(evt);
    }//GEN-LAST:event_codeThreeTextFieldMouseClicked

    private void codeFourTextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_codeFourTextFieldMouseClicked
    {//GEN-HEADEREND:event_codeFourTextFieldMouseClicked
        setCurCode(evt);
    }//GEN-LAST:event_codeFourTextFieldMouseClicked

    private void codeFiveTextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_codeFiveTextFieldMouseClicked
    {//GEN-HEADEREND:event_codeFiveTextFieldMouseClicked
        setCurCode(evt);
    }//GEN-LAST:event_codeFiveTextFieldMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allowEditCheckBox;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JTextField codeFiveTextField;
    private javax.swing.JTextField codeFourTextField;
    private javax.swing.JTextField codeOneTextField;
    private javax.swing.JTextField codeThreeTextField;
    private javax.swing.JTextField codeTwoTextField;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}