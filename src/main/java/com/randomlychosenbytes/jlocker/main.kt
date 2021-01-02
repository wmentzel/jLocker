package com.randomlychosenbytes.jlocker

import java.awt.EventQueue
import java.io.File
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    EventQueue.invokeLater {
        MainFrame().apply {
            State.mainFrame = this
            initialize()
            isVisible = true
        }

        val url = MainFrame::class.java.protectionDomain.codeSource.location
        var homeDirectory = File(url.file)

        if (!homeDirectory.isDirectory) {
            homeDirectory = homeDirectory.parentFile
        }

        State.dataManager.initPath(homeDirectory)

        println("""* program directory is: "$homeDirectory"""")
    }
}