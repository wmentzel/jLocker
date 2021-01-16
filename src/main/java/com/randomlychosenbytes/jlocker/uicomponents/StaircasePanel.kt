package com.randomlychosenbytes.jlocker.uicomponents

import com.randomlychosenbytes.jlocker.dialogs.StaircaseDialog
import com.randomlychosenbytes.jlocker.model.Staircase
import com.randomlychosenbytes.jlocker.staircaseBackgroundColor
import com.randomlychosenbytes.jlocker.staircaseFontColor
import java.awt.BorderLayout
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class StaircasePanel(var staircase: Staircase) : JPanel() {
    fun Staircase(name: String) {
        initComponents()
        setCaption(name)
    }

    fun Staircase() {
        initComponents()
    }

    fun setCaption(name: String?) {
        this.name = name
        captionLabel.text =
            "<html><div align=\"center\">Treppenhaus<br><br><div style='font-size:12pt;'>" + this.name + "</div></div></html>"
    }

    override fun toString(): String {
        return "Treppenhaus"
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        captionLabel = JLabel()
        background = staircaseBackgroundColor
        layout = BorderLayout()
        captionLabel.font = Font("Tahoma", 1, 14) // NOI18N
        captionLabel.foreground = staircaseFontColor
        captionLabel.horizontalAlignment = SwingConstants.CENTER
        captionLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(evt: MouseEvent) {
                captionLabelMouseReleased(evt)
            }
        })
        add(captionLabel, BorderLayout.CENTER)
    } // </editor-fold>


    private fun captionLabelMouseReleased(evt: MouseEvent) {
        // TODO mainframe as first argument
        val dialog = StaircaseDialog(null, true, this)
        dialog.isVisible = true
    }

    private lateinit var captionLabel: JLabel
}