package com.randomlychosenbytes.jlocker

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.State.Companion.mainFrame
import java.awt.EventQueue
import java.io.File
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    EventQueue.invokeLater {

        var homeDirectory = File(MainFrame::class.java.protectionDomain.codeSource.location.file)

        if (!homeDirectory.isDirectory) {
            homeDirectory = homeDirectory.parentFile
        }

        dataManager.initPath(homeDirectory)

        println("""* program directory is: "$homeDirectory"""")

        mainFrame = MainFrame()

        mainFrame.apply {
            initialize()
            isVisible = true
        }
    }
}