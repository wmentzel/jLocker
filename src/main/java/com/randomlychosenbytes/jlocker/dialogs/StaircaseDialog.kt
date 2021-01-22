package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.uicomponents.StaircasePanel
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import javax.swing.*

class StaircaseDialog(parent: Frame?, modal: Boolean, staircasePanel: StaircasePanel) : JDialog(parent, modal) {
    private val staircase: StaircasePanel

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        centerPanel = JPanel()
        staircasenrLabel = JLabel()
        staircaseNameTextBox = JTextField()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Treppenaufgang"
        isResizable = false
        contentPane.layout = GridBagLayout()
        centerPanel.layout = GridBagLayout()
        staircasenrLabel.text = "Name"
        var gridBagConstraints: GridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        centerPanel.add(staircasenrLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 0)
        centerPanel.add(staircaseNameTextBox, gridBagConstraints)
        okButton.text = "OK"
        okButton.addActionListener { evt -> okButtonActionPerformed(evt) }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 10)
        centerPanel.add(okButton, gridBagConstraints)
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        centerPanel.add(cancelButton, GridBagConstraints())
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        contentPane.add(centerPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun okButtonActionPerformed(evt: ActionEvent) {
        staircase.setCaption(staircaseNameTextBox.text)
        dispose()
    }

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var okButton: JButton
    private lateinit var staircaseNameTextBox: JTextField
    private lateinit var staircasenrLabel: JLabel

    init {
        initComponents()

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        // focus in the middle
        setLocationRelativeTo(null)
        staircase = staircasePanel
        staircaseNameTextBox.text = staircasePanel.name
    }
}