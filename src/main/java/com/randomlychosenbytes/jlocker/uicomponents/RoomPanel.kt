package com.randomlychosenbytes.jlocker.uicomponents

import com.randomlychosenbytes.jlocker.dialogs.RoomDialog
import com.randomlychosenbytes.jlocker.model.Room
import com.randomlychosenbytes.jlocker.roomBackgroundColor
import com.randomlychosenbytes.jlocker.roomFontColor
import java.awt.BorderLayout
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class RoomPanel(val room: Room) : JPanel() {

    fun Room(name: String, classname: String) {
        initComponents()
        setCaption(name, classname)
    }

    fun Room() {
        initComponents()
        setCaption("", "")
    }

    fun setCaption(name: String, schoolClassName: String) {
        room.name = name
        room.schoolClassName = schoolClassName
        var caption = "<html><div align=\"center\">$name"

        // if there was a class name specified
        if (schoolClassName.isNotEmpty()) {
            caption += "<br><br><div style='font-size:12pt;'>Klasse<br>$schoolClassName</div>"
        }
        caption += "</div></html>"
        captionLabel.text = caption
    }

    override fun toString(): String {
        return "Raum"
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        captionLabel = JLabel()
        background = roomBackgroundColor
        layout = BorderLayout()
        captionLabel.font = Font("Tahoma", 1, 14) // NOI18N
        captionLabel.foreground = roomFontColor
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
        val dialog = RoomDialog(null, true, this)
        dialog.isVisible = true
    }

    private lateinit var captionLabel: JLabel
}