package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.utils.renameSchoolClass
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*

class RenameClassDialog(parent: Frame?, modal: Boolean) : JDialog(parent, modal) {
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        centerPanel = JPanel()
        classLabel = JLabel()
        classTextField = JTextField()
        changeToLabel = JLabel()
        changeToTextField = JTextField()
        southPanel = JPanel()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Klasse umbennen"
        isResizable = false
        contentPane.layout = GridBagLayout()
        centerPanel.layout = GridLayout(2, 2, 20, 10)
        classLabel.text = "Klasse"
        centerPanel.add(classLabel)
        centerPanel.add(classTextField)
        changeToLabel.text = "ändern zu"
        centerPanel.add(changeToLabel)
        centerPanel.add(changeToTextField)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.insets = Insets(10, 10, 0, 10)
        contentPane.add(centerPanel, gridBagConstraints)
        okButton.text = "OK"
        okButton.addActionListener { evt -> okButtonActionPerformed(evt) }
        southPanel.add(okButton)
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        southPanel.add(cancelButton)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 10, 10, 10)
        contentPane.add(southPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun okButtonActionPerformed(evt: ActionEvent) {
        val newClassName = changeToTextField.text
        val previousClassName = classTextField.text

        val numMatches = dataManager.buildingList.renameSchoolClass(previousClassName, newClassName)

        if (numMatches == 0) {
            JOptionPane.showMessageDialog(
                null,
                "Kein Schließfachmieter besucht die Klasse $previousClassName!",
                "Fehler",
                JOptionPane.ERROR_MESSAGE
            )
        } else {
            dispose()
            JOptionPane.showMessageDialog(
                null,
                "Die Klasse von $numMatches Schließfachmietern wurde erfolgreich geändert!",
                "Information",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var changeToLabel: JLabel
    private lateinit var changeToTextField: JTextField
    private lateinit var classLabel: JLabel
    private lateinit var classTextField: JTextField
    private lateinit var okButton: JButton
    private lateinit var southPanel: JPanel

    init {
        initComponents()

        // center on screen
        setLocationRelativeTo(null)

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton
    }
}