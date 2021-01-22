package com.randomlychosenbytes.jlocker.uicomponents

import com.randomlychosenbytes.jlocker.accentColor
import com.randomlychosenbytes.jlocker.model.Locker
import com.randomlychosenbytes.jlocker.model.LockerCabinet
import com.randomlychosenbytes.jlocker.secondaryColor
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.GroupLayout
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel

class LockerCabinetPanel(var lockerCabinet: LockerCabinet) : JPanel() {

    companion object {
        @JvmStatic
        fun updateDummyRows(cabinets: List<LockerCabinetPanel>) {
            val maxRows = cabinets.map { it.lockerCabinet.lockers.size }.maxOrNull() ?: 0
            cabinets.forEach { it.updateDummyRows(maxRows) }
        }
    }

    private fun updateDummyRows(numRowsOverall: Int) {
        cabinetPanel.removeAll()
        if (numRowsOverall > lockerCabinet.lockers.size) {
            for (i in 0 until numRowsOverall - lockerCabinet.lockers.size) {
                cabinetPanel.add(JLabel())
            }
        }
        for (locker in lockerCabinet.lockers) {
            cabinetPanel.add(LockerPanel(locker))
        }
        cabinetPanel.updateUI()
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        buttonPanel = JPanel()
        addLockerLabel = JLabel()
        remLockerLabel = JLabel()
        cabinetPanel = JPanel()
        layout = BorderLayout()
        buttonPanel.background = secondaryColor
        addLockerLabel.font = Font("Tahoma", 1, 18) // NOI18N
        addLockerLabel.foreground = accentColor
        addLockerLabel.text = "+"
        addLockerLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(evt: MouseEvent) {
                addLockerLabelMouseReleased(evt)
            }
        })
        buttonPanel.add(addLockerLabel)
        remLockerLabel.font = Font("Tahoma", 1, 18) // NOI18N
        remLockerLabel.foreground = accentColor
        remLockerLabel.text = "-"
        remLockerLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(evt: MouseEvent) {
                remLockerLabelMouseReleased(evt)
            }
        })
        buttonPanel.add(remLockerLabel)
        add(buttonPanel, BorderLayout.NORTH)
        cabinetPanel.background = secondaryColor
        val cabinetPanelLayout = GroupLayout(cabinetPanel)
        cabinetPanel.layout = cabinetPanelLayout
        cabinetPanelLayout.setHorizontalGroup(
            cabinetPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 170, Short.MAX_VALUE.toInt())
        )
        cabinetPanelLayout.setVerticalGroup(
            cabinetPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 233, Short.MAX_VALUE.toInt())
        )
        add(cabinetPanel, BorderLayout.CENTER)
    } // </editor-fold>

    private fun addLockerLabelMouseReleased(evt: MouseEvent) {
        lockerCabinet.lockers.add(0, Locker())
        //updateDummyRows(State.dataManager.currentModuleWrapperList.map { it.module }.filterIsInstance<LockerCabinet>())
        remLockerLabel.isEnabled = true
    }

    private fun remLockerLabelMouseReleased(evt: MouseEvent) {
        if (lockerCabinet.lockers.isNotEmpty()) {
            val answer = JOptionPane.showConfirmDialog(
                null,
                "Wollen Sie dieses Schließfach wirklich löschen?",
                "Löschen",
                JOptionPane.YES_NO_OPTION
            )
            if (answer == JOptionPane.YES_OPTION) {
                lockerCabinet.lockers.removeAt(0) // remove first
/*                updateDummyRows(State.dataManager.currentModuleWrapperList.map { it.module }
                    .filterIsInstance<LockerCabinet>())*/
            }
        }
    }

    private lateinit var addLockerLabel: JLabel
    private lateinit var buttonPanel: JPanel
    private lateinit var cabinetPanel: JPanel
    private lateinit var remLockerLabel: JLabel

    override fun toString() = "Schließfachschrank"

    init {
        initComponents()
        lockerCabinet.lockers = lockerCabinet.lockers.map { Locker(it) }.toMutableList()
        cabinetPanel.layout = GridLayout(0, 1, 0, 10)
        updateDummyRows(0)
    }
}