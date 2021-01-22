package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

/**
 * A simple dialog that displays information about this program.
 */
class AboutBox(parent: Frame?, modal: Boolean) : JDialog(parent, modal) {
    // TODO put in properties/resource file
    val names = arrayOf(
        "Babette Mentzel",
        "Torsten Brandes",
        "Heiko Niemeyer",
        "Indra Beeske",
        "Hang Ming Pham",
        "Anne Hauer",
        "Die Schließfach-AG der RLO"
    )
    var changeText: ActionListener = object : ActionListener {
        private var textIndex = 1
        override fun actionPerformed(evt: ActionEvent) {
            appThanks2Label!!.text = names[textIndex]
            if (++textIndex > names.size - 1) textIndex = 0
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        imageLabel = JLabel()
        dataPanel = JPanel()
        westPanel = JPanel()
        versionLabel = JLabel()
        vendorLabel = JLabel()
        technologyLabel = JLabel()
        emailAddressLabel = JLabel()
        thanks2Label = JLabel()
        centerPanel = JPanel()
        appVersionLabel = JLabel()
        appVendorLabel = JLabel()
        appTechnologyLabel = JLabel()
        appEmailAddressLabel = JLabel()
        appThanks2Label = JLabel()
        closeButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Über jLocker"
        isResizable = false
        contentPane.layout = GridBagLayout()
        imageLabel!!.icon = ImageIcon(javaClass.getResource("/jLocker_2014.png")) // NOI18N
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        contentPane.add(imageLabel, gridBagConstraints)
        dataPanel!!.layout = GridBagLayout()
        westPanel!!.layout = GridLayout(6, 0, 0, 5)
        versionLabel!!.text = "Version:"
        westPanel!!.add(versionLabel)
        vendorLabel!!.text = "Geschrieben von:"
        westPanel!!.add(vendorLabel)
        technologyLabel!!.text = "Technologie:"
        technologyLabel!!.toolTipText = ""
        westPanel!!.add(technologyLabel)
        emailAddressLabel!!.text = "E-Mail-Adresse:"
        westPanel!!.add(emailAddressLabel)
        thanks2Label!!.text = "Dank an:"
        westPanel!!.add(thanks2Label)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 30)
        dataPanel!!.add(westPanel, gridBagConstraints)
        centerPanel!!.layout = GridLayout(6, 0, 0, 5)
        appVersionLabel!!.text = dataManager.appVersion
        centerPanel!!.add(appVersionLabel)
        appVendorLabel!!.text = "Willi Mentzel"
        centerPanel!!.add(appVendorLabel)
        val version = System.getProperty("java.version")
        appTechnologyLabel!!.text = "Java $version"
        centerPanel!!.add(appTechnologyLabel)
        appEmailAddressLabel!!.text = "willi.mentzel@gmail.com"
        centerPanel!!.add(appEmailAddressLabel)
        centerPanel!!.add(appThanks2Label)
        dataPanel!!.add(centerPanel, GridBagConstraints())
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(10, 10, 0, 0)
        contentPane.add(dataPanel, gridBagConstraints)
        closeButton!!.text = "Schließen"
        closeButton!!.addActionListener { evt -> closeButtonActionPerformed(evt) }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(10, 0, 10, 0)
        contentPane.add(closeButton, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun closeButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    private lateinit var appEmailAddressLabel: JLabel
    private lateinit var appTechnologyLabel: JLabel
    private lateinit var appThanks2Label: JLabel
    private lateinit var appVendorLabel: JLabel
    private lateinit var appVersionLabel: JLabel
    private lateinit var centerPanel: JPanel
    private lateinit var closeButton: JButton
    private lateinit var dataPanel: JPanel
    private lateinit var emailAddressLabel: JLabel
    private lateinit var imageLabel: JLabel
    private lateinit var technologyLabel: JLabel
    private lateinit var thanks2Label: JLabel
    private lateinit var vendorLabel: JLabel
    private lateinit var versionLabel: JLabel
    private lateinit var westPanel: JPanel

    init {
        initComponents()

        // button that is clicked when you hit enter
        getRootPane().defaultButton = closeButton

        // focus in the middle
        setLocationRelativeTo(null)

        // start with the first name
        appThanks2Label!!.text = names[0]
        val timer = Timer(1500, changeText)
        timer.isRepeats = true
        timer.start()
    }
}