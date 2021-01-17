package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.State
import com.randomlychosenbytes.jlocker.model.Task
import com.randomlychosenbytes.jlocker.utils.moveOwner
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import javax.swing.*

class MoveLockerDialog(parent: Frame?, modal: Boolean) : JDialog(parent, modal) {
    private val dataManager = State.dataManager

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        centerPanel = JPanel()
        sourceIDLabel = JLabel()
        sourceIDTextField = JTextField()
        destinationIDLabel = JLabel()
        destinationIDTextField = JTextField()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Schließfachumzug"
        isResizable = false
        contentPane.layout = GridBagLayout()
        centerPanel.layout = GridBagLayout()
        sourceIDLabel.text = "Quelle"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        centerPanel.add(sourceIDLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 0)
        centerPanel.add(sourceIDTextField, gridBagConstraints)
        destinationIDLabel.text = "Ziel"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        centerPanel.add(destinationIDLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 0)
        centerPanel.add(destinationIDTextField, gridBagConstraints)
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

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private fun okButtonActionPerformed(evt: ActionEvent) {
        val sourceLocker = dataManager.getLockerById(sourceIDTextField.text)
        if (sourceLocker == null) {
            JOptionPane.showMessageDialog(
                this,
                "Das Quellschließfach existiert nicht!",
                "Fehler",
                JOptionPane.OK_OPTION
            )
            return
        }
        val destLocker = dataManager.getLockerById(destinationIDTextField.text)
        if (destLocker == null) {
            JOptionPane.showMessageDialog(this, "Das Zielschließfach existiert nicht!", "Fehler", JOptionPane.OK_OPTION)
            return
        }
        moveOwner(sourceLocker, destLocker)
        if (!sourceLocker.isFree) {
            // TODO check null pointer dereferencing getSurname()
            val task1 =
                sourceLocker.pupil.lastName + ", " + sourceLocker.pupil.firstName + " (" + sourceLocker.pupil.schoolClassName + ") über Umzug informieren (" + destLocker.id + " -> " + sourceLocker.id + ")"
            dataManager.tasks.add(Task(task1))
        }
        if (!destLocker.isFree) {
            val task2 =
                destLocker.pupil.lastName + ", " + destLocker.pupil.firstName + " (" + destLocker.pupil.schoolClassName + ") über Umzug informieren (" + sourceLocker.id + " -> " + destLocker.id + ")"
            dataManager.tasks.add(Task(task2))
        }
        sourceIDTextField.text = ""
        destinationIDTextField.text = ""
        dispose()
    }

    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var destinationIDLabel: JLabel
    private lateinit var destinationIDTextField: JTextField
    private lateinit var okButton: JButton
    private lateinit var sourceIDLabel: JLabel
    private lateinit var sourceIDTextField: JTextField

    init {
        initComponents()

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        // focus in the middle
        setLocationRelativeTo(null)
    }
}