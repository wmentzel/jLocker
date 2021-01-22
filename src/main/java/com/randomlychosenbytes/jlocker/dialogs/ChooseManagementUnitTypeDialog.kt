package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.model.LockerCabinet
import com.randomlychosenbytes.jlocker.model.Module
import com.randomlychosenbytes.jlocker.model.Room
import com.randomlychosenbytes.jlocker.model.Staircase
import com.randomlychosenbytes.jlocker.uicomponents.ModulePanel
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*

class ChooseManagementUnitTypeDialog(parent: Frame?, modal: Boolean, modulePanel: ModulePanel) :
    JDialog(parent, modal) {
    var modulePanel: ModulePanel

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        buttonGroup = ButtonGroup()
        northPanel = JPanel()
        questionLabel = JLabel()
        radioButtonPanel = JPanel()
        lockerButton = JRadioButton()
        roomButton = JRadioButton()
        staircaseButton = JRadioButton()
        buttonPanel = JPanel()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Typ"
        isResizable = false
        northPanel.layout = GridBagLayout()
        questionLabel.text = "Was wollen Sie einrichten?"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.insets = Insets(20, 20, 20, 20)
        northPanel.add(questionLabel, gridBagConstraints)
        radioButtonPanel.layout = GridLayout(3, 1)
        buttonGroup.add(lockerButton)
        lockerButton.text = "Schließfachschrank"
        radioButtonPanel.add(lockerButton)
        buttonGroup.add(roomButton)
        roomButton.text = "Tür"
        radioButtonPanel.add(roomButton)
        buttonGroup.add(staircaseButton)
        staircaseButton.text = "Treppenhauszugang"
        radioButtonPanel.add(staircaseButton)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 20, 0)
        northPanel.add(radioButtonPanel, gridBagConstraints)
        contentPane.add(northPanel, BorderLayout.PAGE_START)
        okButton.text = "OK"
        okButton.addActionListener { evt -> okButtonActionPerformed(evt) }
        buttonPanel.add(okButton)
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        buttonPanel.add(cancelButton)
        contentPane.add(buttonPanel, BorderLayout.CENTER)
        pack()
    } // </editor-fold>

    private fun okButtonActionPerformed(evt: ActionEvent) {
        val module: Module = when {
            lockerButton.isSelected -> {
                LockerCabinet()
            }
            roomButton.isSelected -> {
                Room("", "")
            }
            else -> {
                Staircase("")
            }
        }
        val text = ("Wollen Sie diesen "
                + modulePanel.module
                + " in einen "
                + module
                + " transformieren?")
        val answer = JOptionPane.showConfirmDialog(null, text, "Schließfach leeren", JOptionPane.YES_NO_OPTION)
        if (answer == JOptionPane.YES_OPTION) {
            modulePanel.module = module
        }

        // close after the selection
        dispose()
    }

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private lateinit var buttonGroup: ButtonGroup
    private lateinit var buttonPanel: JPanel
    private lateinit var cancelButton: JButton
    private lateinit var lockerButton: JRadioButton
    private lateinit var northPanel: JPanel
    private lateinit var okButton: JButton
    private lateinit var questionLabel: JLabel
    private lateinit var radioButtonPanel: JPanel
    private lateinit var roomButton: JRadioButton
    private lateinit var staircaseButton: JRadioButton

    /**
     * Creates new form ChooseMUnitTypeDialog
     */
    init {
        initComponents()

        // focus in the middle
        setLocationRelativeTo(null)

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton
        this.modulePanel = modulePanel

        // Deactivate the radio button that represents the current type
        if (this.modulePanel.module is LockerCabinet) {
            lockerButton.isEnabled = false
        }
        if (this.modulePanel.module is Room) {
            roomButton.isEnabled = false
        }
        if (this.modulePanel.module is Staircase) {
            staircaseButton.isEnabled = false
        }
    }
}