package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import java.awt.*
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.*

class SetLockerMinimumSizesDialog(parent: JDialog?, modal: Boolean) : JDialog(parent, modal) {
    private val textFields: MutableList<JTextField> = LinkedList()
    private val minSizes = dataManager.settings.lockerMinSizes
    private fun buildLayout() {
        val gl = GridLayout(minSizes.size + 1, 2)
        gl.hgap = 5
        gl.vgap = 5
        centerPanel.layout = gl
        centerPanel.add(JLabel("Schließfach"))
        centerPanel.add(JLabel("Mindestgröße (cm)"))
        for (i in minSizes.indices) {
            centerPanel.add(JLabel(Integer.toString(minSizes.size - i)))
            val textField = JTextField(minSizes[minSizes.size - i - 1].toString())
            textFields.add(textField)
            centerPanel.add(textField)
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        centerPanel = JPanel()
        bottomPanel = JPanel()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Mindestgrößen"
        isResizable = false
        contentPane.layout = GridBagLayout()
        val centerPanelLayout = GroupLayout(centerPanel)
        centerPanel.layout = centerPanelLayout
        centerPanelLayout.setHorizontalGroup(
            centerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 198, Short.MAX_VALUE.toInt())
        )
        centerPanelLayout.setVerticalGroup(
            centerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 178, Short.MAX_VALUE.toInt())
        )
        var gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.weighty = 1.0
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        contentPane.add(centerPanel, gridBagConstraints)
        bottomPanel.layout = FlowLayout(FlowLayout.RIGHT)
        okButton.text = "OK"
        okButton.addActionListener { evt -> okButtonActionPerformed(evt) }
        bottomPanel.add(okButton)
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        bottomPanel.add(cancelButton)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        contentPane.add(bottomPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun okButtonActionPerformed(evt: ActionEvent) {
        for (i in minSizes.indices) {
            try {
                val n = textFields[i].text.toInt()
                minSizes[minSizes.size - i - 1] = n
            } catch (e: NumberFormatException) {
                JOptionPane.showMessageDialog(
                    this,
                    "Bitte nur ganze Zahlen eingeben!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }
        (parent as SettingsDialog).updateLockerMinSizesTextField()
        dispose()
    }

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private lateinit var bottomPanel: JPanel
    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var okButton: JButton

    init {
        initComponents()

        // focus in the middle
        setLocationRelativeTo(null)
        buildLayout()
    }
}