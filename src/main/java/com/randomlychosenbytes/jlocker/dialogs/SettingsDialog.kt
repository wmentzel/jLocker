package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.State
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.event.ChangeEvent

class SettingsDialog(parent: Frame?, modal: Boolean) : JDialog(parent, modal) {
    private val dataManager = State.dataManager
    fun updateLockerMinSizesTextField() {
        var text = ""
        val minSizes: List<Int> = dataManager.settings.lockerMinSizes
        for (i in minSizes.indices) {
            if (i != 0) {
                text += ", "
            }
            text += minSizes[minSizes.size - i - 1].toString()
        }
        lockerMinSizesTextField.text = text
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        mainPanel = JPanel()
        backupPanel = JPanel()
        numBackupsLabel = JLabel()
        numBackupsTextField = JTextField()
        numBackupsSlider = JSlider()
        lockerMinSizesLabel = JLabel()
        lockerMinSizesTextField = JTextField()
        buttonPanel = JPanel()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Einstellungen"
        isResizable = false
        contentPane.layout = GridBagLayout()
        mainPanel.layout = GridLayout(2, 2, 10, 10)
        backupPanel.layout = GridBagLayout()
        numBackupsLabel.text = "Anzahl der Backups"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        backupPanel.add(numBackupsLabel, gridBagConstraints)
        numBackupsTextField.isEditable = false
        numBackupsTextField.columns = 3
        backupPanel.add(numBackupsTextField, GridBagConstraints())
        mainPanel.add(backupPanel)
        numBackupsSlider.addChangeListener { evt -> numBackupsSliderStateChanged(evt) }
        mainPanel.add(numBackupsSlider)
        lockerMinSizesLabel.text = "Mindestgrößen für Schließfächer"
        mainPanel.add(lockerMinSizesLabel)
        lockerMinSizesTextField.isEditable = false
        lockerMinSizesTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                lockerMinSizesTextFieldMouseClicked(evt)
            }
        })
        mainPanel.add(lockerMinSizesTextField)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(10, 10, 0, 10)
        contentPane.add(mainPanel, gridBagConstraints)
        buttonPanel.layout = FlowLayout(FlowLayout.RIGHT)
        okButton.text = "OK"
        okButton.addActionListener { evt -> okButtonActionPerformed(evt) }
        buttonPanel.add(okButton)
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        buttonPanel.add(cancelButton)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(10, 0, 10, 0)
        contentPane.add(buttonPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun okButtonActionPerformed(evt: ActionEvent) {
        dataManager.settings.numOfBackups = numBackupsSlider.value
        dispose()
    }

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private fun numBackupsSliderStateChanged(evt: ChangeEvent) {
        val num = numBackupsSlider.value
        numBackupsTextField.text = Integer.toString(num)
    }

    private fun lockerMinSizesTextFieldMouseClicked(evt: MouseEvent) {
        SetLockerMinimumSizesDialog(this, true).isVisible = true
    }

    private lateinit var backupPanel: JPanel
    private lateinit var buttonPanel: JPanel
    private lateinit var cancelButton: JButton
    private lateinit var lockerMinSizesLabel: JLabel
    private lateinit var lockerMinSizesTextField: JTextField
    private lateinit var mainPanel: JPanel
    private lateinit var numBackupsLabel: JLabel
    private lateinit var numBackupsSlider: JSlider
    private lateinit var numBackupsTextField: JTextField
    private lateinit var okButton: JButton

    init {
        initComponents()

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        // focus in the middle
        setLocationRelativeTo(null)
        val numBackups = dataManager.settings.numOfBackups
        numBackupsSlider.value = numBackups
        numBackupsTextField.text = Integer.toString(numBackups)
        updateLockerMinSizesTextField()
    }
}