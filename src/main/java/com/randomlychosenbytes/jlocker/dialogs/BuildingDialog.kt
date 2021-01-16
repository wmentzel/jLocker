package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.MainFrame
import com.randomlychosenbytes.jlocker.State
import com.randomlychosenbytes.jlocker.model.Building
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import javax.swing.*

class BuildingDialog(
    parent: Frame,
    modal: Boolean,
    val currentBuilding: Building?,
    val createNewBuilding: ((String) -> Unit)?
) : JDialog(parent, modal) {
    private val dataManager = State.dataManager

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        centerPanel = JPanel()
        entityNameLabel = JLabel()
        entityNameTextField = JTextField()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isResizable = false
        contentPane.layout = GridBagLayout()
        centerPanel!!.layout = GridBagLayout()
        entityNameLabel!!.text = "Name"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        centerPanel!!.add(entityNameLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 0)
        centerPanel!!.add(entityNameTextField, gridBagConstraints)
        okButton!!.text = "OK"
        okButton!!.addActionListener { evt -> okButtonActionPerformed(evt) }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 10)
        centerPanel!!.add(okButton, gridBagConstraints)
        cancelButton!!.text = "Abbrechen"
        cancelButton!!.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        centerPanel!!.add(cancelButton, GridBagConstraints())
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        contentPane.add(centerPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun okButtonActionPerformed(evt: ActionEvent) {
        if (currentBuilding != null) {
            currentBuilding.name = entityNameTextField.text
        } else {
            createNewBuilding?.invoke(entityNameTextField.text)
        }
        (parent as MainFrame).updateComboBoxes()
        dispose()
    }

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var entityNameLabel: JLabel
    private lateinit var entityNameTextField: JTextField
    private lateinit var okButton: JButton

    init {
        initComponents()

        // center dialog on screen
        setLocationRelativeTo(null)

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        if (currentBuilding != null) {
            title = "Gebäudename bearbeiten"
            entityNameTextField.text = currentBuilding.name
        } else {
            title = "Gebäude hinzufügen"
        }
    }
}