package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.DataManager;
import com.randomlychosenbytes.jlocker.MainFrame;
import com.randomlychosenbytes.jlocker.State;

import javax.swing.*;
import java.awt.*;

public class EditCodesDialog extends JDialog {
    private final JTextField[] codeTextFields = new JTextField[5];
    private int iCurCodeIndex;
    private DataManager dataManager = State.Companion.getDataManager();

    /**
     * Creates new form EditCodesDialog
     */
    public EditCodesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.dataManager = dataManager;

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);

        // focus in the middle
        setLocationRelativeTo(null);

        iCurCodeIndex = dataManager.getCurrentLocker().getCurrentCodeIndex();

        codeTextFields[0] = codeOneTextField;
        codeTextFields[1] = codeTwoTextField;
        codeTextFields[2] = codeThreeTextField;
        codeTextFields[3] = codeFourTextField;
        codeTextFields[4] = codeFiveTextField;

        String[] codes = dataManager.getCurrentLocker().getCodes(dataManager.getSuperUserMasterKey());

        for (int i = 0; i < 5; i++) {
            codeTextFields[i].setText(codes[i]);

            if (i == iCurCodeIndex) {
                codeTextFields[i].setBackground(new Color(0, 102, 0));
            }
        }
    }

    private void setCurCode(java.awt.event.MouseEvent evt) {
        if (!allowEditCheckBox.isSelected()) {
            codeTextFields[iCurCodeIndex].setBackground(new Color(240, 240, 240));

            for (int i = 0; i < 5; i++) {
                if (codeTextFields[i] == evt.getSource()) {
                    iCurCodeIndex = i;
                }
            }

            codeTextFields[iCurCodeIndex].setBackground(new Color(0, 102, 0));
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
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

        codeOneTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                codeOneTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeOneTextField);

        codeTwoTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                codeTwoTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeTwoTextField);

        codeThreeTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                codeThreeTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeThreeTextField);

        codeFourTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                codeFourTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeFourTextField);

        codeFiveTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                codeFiveTextFieldMouseClicked(evt);
            }
        });
        centerPanel.add(codeFiveTextField);

        allowEditCheckBox.setText("Editieren erlauben");
        allowEditCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                allowEditCheckBoxItemStateChanged(evt);
            }
        });
        centerPanel.add(allowEditCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        getContentPane().add(centerPanel, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(okButton);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(cancelButton);

        getContentPane().add(bottomPanel, new java.awt.GridBagConstraints());

        pack();
    }// </editor-fold>

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dataManager.getCurrentLocker().setCurrentCodeIndex(iCurCodeIndex);
        String[] codes = new String[5];

        for (int i = 0; i < 5; i++) {
            boolean isValid = false;

            String code = codeTextFields[i].getText();

            code = code.replace("-", "");
            code = code.replace(" ", "");

            if (code.length() == 6) {
                try {
                    Integer.parseInt(code);
                    isValid = true;
                } catch (NumberFormatException e) {
                }
            }

            if (!isValid) {
                JOptionPane.showMessageDialog(null, "Der " + i + ". Code ist ungültig!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            codes[i] = code;
        }

        dataManager.getCurrentLocker().setCodes(codes, dataManager.getSuperUserMasterKey());

        ((MainFrame) this.getParent()).showLockerInformation();

        this.dispose();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void allowEditCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        boolean editable = allowEditCheckBox.isSelected();

        for (int i = 0; i < 5; i++) {
            codeTextFields[i].setEditable(editable);

            if (editable)
                codeTextFields[i].setBackground(new Color(255, 255, 255));
            else
                codeTextFields[i].setBackground(new Color(240, 240, 240));
        }

        if (!editable) {
            iCurCodeIndex = 0;
            codeTextFields[iCurCodeIndex].setBackground(new Color(0, 102, 0));
        }

    }

    private void codeOneTextFieldMouseClicked(java.awt.event.MouseEvent evt) {
        setCurCode(evt);
    }

    private void codeTwoTextFieldMouseClicked(java.awt.event.MouseEvent evt) {
        setCurCode(evt);
    }

    private void codeThreeTextFieldMouseClicked(java.awt.event.MouseEvent evt) {
        setCurCode(evt);
    }

    private void codeFourTextFieldMouseClicked(java.awt.event.MouseEvent evt) {
        setCurCode(evt);
    }

    private void codeFiveTextFieldMouseClicked(java.awt.event.MouseEvent evt) {
        setCurCode(evt);
    }

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

}
