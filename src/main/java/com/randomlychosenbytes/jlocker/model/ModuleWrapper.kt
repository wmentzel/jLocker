package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.State.Companion.mainFrame
import com.randomlychosenbytes.jlocker.accentColor
import com.randomlychosenbytes.jlocker.dialogs.ChooseManagementUnitTypeDialog
import com.randomlychosenbytes.jlocker.secondaryColor
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel

class ModuleWrapper(module: Module) : JPanel() {

    @Expose
    var module: Module = module
        set(value) {
            field = value
            updatePanel()
        }

    private fun updatePanel() {
        centerPanel.apply {
            removeAll()
            add(module)
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
                addMUnitLeftLabelMouseReleased(evt)
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
                removeThisMUnitLabelMouseReleased(evt)
            }
        })
        southPanel.add(removeThisMUnitLabel)
        addMUnitRightLabel.font = Font("Tahoma", 1, 18) // NOI18N
        addMUnitRightLabel.foreground = accentColor
        addMUnitRightLabel.text = "+"
        addMUnitRightLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(evt: MouseEvent) {
                addMUnitRightLabelMouseReleased(evt)
            }
        })
        southPanel.add(addMUnitRightLabel)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(10, 5, 10, 5)
        add(southPanel, gridBagConstraints)
    } // </editor-fold>

    private fun addMUnitLeftLabelMouseReleased(evt: MouseEvent) {
        val mus = dataManager.currentManagmentUnitList
        val index = mus.indexOf(this)
        val iNewIndex: Int = if (index == mus.size - 1) {
            mus.add(ModuleWrapper(LockerCabinet()))
            mus.size - 1
        } else {
            mus.add(index + 1, ModuleWrapper(LockerCabinet()))
            index + 1
        }
        dataManager.currentManagementUnitIndex = iNewIndex
        dataManager.currentLockerIndex = 0
        mainFrame.drawLockerOverview()
    }

    private fun transformLabelMouseReleased(evt: MouseEvent) {
        // TODO mainframe as first argument
        val dialog = ChooseManagementUnitTypeDialog(null, true, this)
        dialog.isVisible = true
    }

    private fun removeThisMUnitLabelMouseReleased(evt: MouseEvent) {
        if (dataManager.currentManagmentUnitList.size <= 1) {
            return
        }

        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie diesen ${this.module} wirklich löschen?",
            "Löschen",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            val mus = dataManager.currentManagmentUnitList
            mus.remove(this)
            mainFrame.drawLockerOverview()
        }
    }

    private fun addMUnitRightLabelMouseReleased(evt: MouseEvent) {
        val mus = dataManager.currentManagmentUnitList
        val index = mus.indexOf(this)
        mus.add(index, ModuleWrapper(LockerCabinet()))
        dataManager.currentManagementUnitIndex = index
        dataManager.currentLockerIndex = 0
        mainFrame.drawLockerOverview()
    }

    private lateinit var addMUnitLeftLabel: JLabel
    private lateinit var addMUnitRightLabel: JLabel
    private lateinit var centerPanel: JPanel
    private lateinit var removeThisMUnitLabel: JLabel
    private lateinit var southPanel: JPanel
    private lateinit var transformLabel: JLabel

    init {
        initComponents()
        updatePanel()
    }
}