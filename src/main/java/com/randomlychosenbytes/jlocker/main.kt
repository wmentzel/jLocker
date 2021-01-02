package com.randomlychosenbytes.jlocker

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.State.Companion.mainFrame
import java.awt.EventQueue
import java.io.File
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    EventQueue.invokeLater {

        mainFrame = MainFrame().apply {
            initialize()
            isVisible = true
        }

        val url = MainFrame::class.java.protectionDomain.codeSource.location
        var homeDirectory = File(url.file)

        if (!homeDirectory.isDirectory) {
            homeDirectory = homeDirectory.parentFile
        }

        dataManager.initPath(homeDirectory)

        println("""* program directory is: "$homeDirectory"""")
    }
}