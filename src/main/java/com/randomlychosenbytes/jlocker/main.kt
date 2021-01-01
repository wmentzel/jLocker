package com.randomlychosenbytes.jlocker

import java.awt.EventQueue
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    EventQueue.invokeLater {
        val mainFrame = MainFrame().apply {
            initialize()
            isVisible = true
        }
        DataManager.mainFrame = mainFrame
    }
}