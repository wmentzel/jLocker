package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.uicomponents.RoomPanel
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import javax.swing.*

class RoomDialog(parent: Frame?, modal: Boolean, roomPanel: RoomPanel) : JDialog(parent, modal) {
    private val roomPanel: RoomPanel

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        centerPanel = JPanel()
        roomNameLabel = JLabel()
        roomNameTextField = JTextField()
        classNameLabel = JLabel()
        classNameTextField = JTextField()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Raum"
        isResizable = false
        contentPane.layout = GridBagLayout()
        centerPanel.layout = GridBagLayout()
        roomNameLabel.text = "Raumname"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        centerPanel.add(roomNameLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 0)
        centerPanel.add(roomNameTextField, gridBagConstraints)
        classNameLabel.text = "Klassenname"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        centerPanel.add(classNameLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 0)
        centerPanel.add(classNameTextField, gridBagConstraints)
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
        val name = roomNameTextField.text
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(null, "Sie müssen einen Raumnamen angeben!", "Fehler", JOptionPane.OK_OPTION)
        }
        roomPanel.setCaption(name, classNameTextField.text)
        dispose()
    }

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var classNameLabel: JLabel
    private lateinit var classNameTextField: JTextField
    private lateinit var okButton: JButton
    private lateinit var roomNameLabel: JLabel
    private lateinit var roomNameTextField: JTextField

    init {
        initComponents()

        // focus in the middle
        setLocationRelativeTo(null)

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton
        this.roomPanel = roomPanel
        roomNameTextField.text = roomPanel.room.name
        classNameTextField.text = roomPanel.room.schoolClassName
    }
}