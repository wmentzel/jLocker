package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.MainFrame
import com.randomlychosenbytes.jlocker.State
import com.randomlychosenbytes.jlocker.model.Locker
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class EditCodesDialog(parent: Frame, modal: Boolean, currentLocker: Locker) : JDialog(parent, modal) {
    private var codeTextFields: Array<JTextField>
    private var iCurCodeIndex: Int
    private val dataManager = State.dataManager
    private val currentLocker: Locker

    private fun setCurCode(evt: MouseEvent) {
        if (!allowEditCheckBox.isSelected) {
            codeTextFields[iCurCodeIndex].background = Color(240, 240, 240)
            for (i in 0..4) {
                if (codeTextFields[i] === evt.source) {
                    iCurCodeIndex = i
                }
            }
            codeTextFields[iCurCodeIndex].background = Color(0, 102, 0)
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        centerPanel = JPanel()
        codeOneTextField = JTextField()
        codeTwoTextField = JTextField()
        codeThreeTextField = JTextField()
        codeFourTextField = JTextField()
        codeFiveTextField = JTextField()
        allowEditCheckBox = JCheckBox()
        bottomPanel = JPanel()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Codes bearbeiten"
        isResizable = false
        contentPane.layout = GridBagLayout()
        centerPanel.border = BorderFactory.createTitledBorder("Verfügbare Codes")
        centerPanel.layout = GridLayout(6, 1, 0, 10)
        codeOneTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                codeOneTextFieldMouseClicked(evt)
            }
        })
        centerPanel.add(codeOneTextField)
        codeTwoTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                codeTwoTextFieldMouseClicked(evt)
            }
        })
        centerPanel.add(codeTwoTextField)
        codeThreeTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                codeThreeTextFieldMouseClicked(evt)
            }
        })
        centerPanel.add(codeThreeTextField)
        codeFourTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                codeFourTextFieldMouseClicked(evt)
            }
        })
        centerPanel.add(codeFourTextField)
        codeFiveTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                codeFiveTextFieldMouseClicked(evt)
            }
        })
        centerPanel.add(codeFiveTextField)
        allowEditCheckBox.text = "Editieren erlauben"
        allowEditCheckBox.addItemListener { evt -> allowEditCheckBoxItemStateChanged(evt) }
        centerPanel.add(allowEditCheckBox)
        val gridBagConstraints: GridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        contentPane.add(centerPanel, gridBagConstraints)
        okButton.text = "OK"
        okButton.addActionListener { evt -> okButtonActionPerformed(evt) }
        bottomPanel.add(okButton)
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        bottomPanel.add(cancelButton)
        contentPane.add(bottomPanel, GridBagConstraints())
        pack()
    } // </editor-fold>

    private fun okButtonActionPerformed(evt: ActionEvent) {
        currentLocker.currentCodeIndex = iCurCodeIndex
        val codes = (0..4).map { i ->
            var isValid = false
            val code = codeTextFields[i].text.replace("-", "").replace(" ", "")

            if (code.length == 6) {
                try {
                    code.toInt()
                    isValid = true
                } catch (e: NumberFormatException) {
                }
            }
            if (!isValid) {
                JOptionPane.showMessageDialog(null, "Der $i. Code ist ungültig!", "Fehler", JOptionPane.ERROR_MESSAGE)
                return
            }
            code
        }
        currentLocker.setCodes(codes.toTypedArray(), dataManager.superUserMasterKey)
        (parent as MainFrame).showLockerInformation()
        dispose()
    }

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private fun allowEditCheckBoxItemStateChanged(evt: ItemEvent) {
        val editable = allowEditCheckBox.isSelected
        for (i in 0..4) {
            codeTextFields[i].isEditable = editable
            if (editable) codeTextFields[i].background = Color(255, 255, 255) else codeTextFields[i].background =
                Color(240, 240, 240)
        }
        if (!editable) {
            iCurCodeIndex = 0
            codeTextFields[iCurCodeIndex].background = Color(0, 102, 0)
        }
    }

    private fun codeOneTextFieldMouseClicked(evt: MouseEvent) {
        setCurCode(evt)
    }

    private fun codeTwoTextFieldMouseClicked(evt: MouseEvent) {
        setCurCode(evt)
    }

    private fun codeThreeTextFieldMouseClicked(evt: MouseEvent) {
        setCurCode(evt)
    }

    private fun codeFourTextFieldMouseClicked(evt: MouseEvent) {
        setCurCode(evt)
    }

    private fun codeFiveTextFieldMouseClicked(evt: MouseEvent) {
        setCurCode(evt)
    }

    private lateinit var allowEditCheckBox: JCheckBox
    private lateinit var bottomPanel: JPanel
    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var codeFiveTextField: JTextField
    private lateinit var codeFourTextField: JTextField
    private lateinit var codeOneTextField: JTextField
    private lateinit var codeThreeTextField: JTextField
    private lateinit var codeTwoTextField: JTextField
    private lateinit var okButton: JButton

    /**
     * Creates new form EditCodesDialog
     */
    init {
        initComponents()

        codeTextFields =
            arrayOf(codeOneTextField, codeTwoTextField, codeThreeTextField, codeFourTextField, codeFiveTextField)

        this.currentLocker = currentLocker

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        // focus in the middle
        setLocationRelativeTo(null)
        iCurCodeIndex = currentLocker.currentCodeIndex

        val codes = currentLocker.getCodes(dataManager.superUserMasterKey)

        for (i in 0..4) {
            codeTextFields[i].text = codes[i]
            if (i == iCurCodeIndex) {
                codeTextFields[i].background = Color(0, 102, 0)
            }
        }
    }
}