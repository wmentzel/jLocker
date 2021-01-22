package com.randomlychosenbytes.jlocker.uicomponents

import com.randomlychosenbytes.jlocker.accentColor
import com.randomlychosenbytes.jlocker.dialogs.ChooseManagementUnitTypeDialog
import com.randomlychosenbytes.jlocker.model.LockerCabinet
import com.randomlychosenbytes.jlocker.model.Module
import com.randomlychosenbytes.jlocker.model.Room
import com.randomlychosenbytes.jlocker.model.Staircase
import com.randomlychosenbytes.jlocker.secondaryColor
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel

class ModulePanel(
    module: Module,
    val addUnitLeft: (ModulePanel) -> Unit,
    val addUnitRight: (ModulePanel) -> Unit,
    val removeUnit: (ModulePanel) -> Unit
) : JPanel() {

    var module: Module = module
        set(value) {
            updatePanel(value)
        }

    private fun updatePanel(module: Module) {
        centerPanel.apply {
            removeAll()
            add(
                when (module) {
                    is Room -> RoomPanel(module)
                    is Staircase -> StaircasePanel(module)
                    is LockerCabinet -> LockerCabinetPanel(module)
                    else -> TODO()
                }
            )
            updateUI()
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        centerPanel = JPanel()
        southPanel = JPanel()
        addMUnitLeftLabel = JLabel()
        transformLabel = JLabel()
        removeThisMUnitLabel = JLabel()
        addMUnitRightLabel = JLabel()
        background = secondaryColor
        minimumSize = Dimension(125, 72)
        preferredSize = Dimension(125, 72)
        layout = GridBagLayout()
        centerPanel.background = secondaryColor
        centerPanel.layout = BorderLayout()
        var gridBagConstraints: GridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.weighty = 1.0
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        add(centerPanel, gridBagConstraints)
        southPanel.background = secondaryColor
        addMUnitLeftLabel.font = Font("Tahoma", 1, 18) // NOI18N
        addMUnitLeftLabel.foreground = accentColor
        addMUnitLeftLabel.text = "+"
        addMUnitLeftLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(evt: MouseEvent) {
                addUnitLeft(evt.source as ModulePanel)
            }
        })
        southPanel.add(addMUnitLeftLabel)
        transformLabel.icon = ImageIcon(javaClass.getResource("/gear.png")) // NOI18N
        transformLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(evt: MouseEvent) {
                transformLabelMouseReleased(evt)
            }
        })
        southPanel.add(transformLabel)
        removeThisMUnitLabel.font = Font("Tahoma", 1, 18) // NOI18N
        removeThisMUnitLabel.foreground = accentColor
        removeThisMUnitLabel.text = "-"
        removeThisMUnitLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(evt: MouseEvent) {
                removeUnit(evt.source as ModulePanel)
            }
        })
        southPanel.add(removeThisMUnitLabel)
        addMUnitRightLabel.font = Font("Tahoma", 1, 18) // NOI18N
        addMUnitRightLabel.foreground = accentColor
        addMUnitRightLabel.text = "+"
        addMUnitRightLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(evt: MouseEvent) {
                addUnitRight(evt.source as ModulePanel)
            }
        })
        southPanel.add(addMUnitRightLabel)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(10, 5, 10, 5)
        add(southPanel, gridBagConstraints)
    } // </editor-fold>


    private fun transformLabelMouseReleased(evt: MouseEvent) {
        // TODO mainframe as first argument
        val dialog = ChooseManagementUnitTypeDialog(null, true, this)
        dialog.isVisible = true
    }

    private lateinit var addMUnitLeftLabel: JLabel
    private lateinit var addMUnitRightLabel: JLabel
    private lateinit var centerPanel: JPanel
    private lateinit var removeThisMUnitLabel: JLabel
    private lateinit var southPanel: JPanel
    private lateinit var transformLabel: JLabel

    init {
        initComponents()
        updatePanel(module)
    }
}