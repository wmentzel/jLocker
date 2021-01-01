package com.randomlychosenbytes.jlocker

import java.awt.EventQueue
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    EventQueue.invokeLater {
        MainFrame().apply {
            DataManager.mainFrame = this
            initialize()
            isVisible = true
        }
    }
}