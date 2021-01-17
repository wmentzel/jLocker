package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileFilter

class LogInDialog(parent: Frame, modal: Boolean) : JDialog(parent, modal) {
    var resPath: File? = null

    // auto generated
    private fun initComponents() {
        splashImageLabel = JLabel()
        loginPanel = JPanel()
        userNameLabel = JLabel()
        chooseUserComboBox = JComboBox()
        passwordLabel = JLabel()
        passwordTextField = JPasswordField()
        bottomPanel = JPanel()
        okButton = JButton()
        loadBackupButton = JButton()
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        title = "${dataManager.appTitle} ${dataManager.appVersion} - Anmeldung"
        isResizable = false
        contentPane.layout = GridBagLayout()
        splashImageLabel.icon = ImageIcon(javaClass.getResource("/jLocker_2014.png")) // NOI18N
        var gridBagConstraints: GridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 20, 0)
        contentPane.add(splashImageLabel, gridBagConstraints)
        loginPanel.layout = GridBagLayout()
        userNameLabel.text = "Benutzer"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        loginPanel.add(userNameLabel, gridBagConstraints)
        chooseUserComboBox.model = DefaultComboBoxModel(arrayOf("SuperUser", "Eingeschränkter Benutzer"))
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.insets = Insets(0, 0, 10, 0)
        loginPanel.add(chooseUserComboBox, gridBagConstraints)
        passwordLabel.text = "Passwort"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        loginPanel.add(passwordLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        loginPanel.add(passwordTextField, gridBagConstraints)
        bottomPanel.layout = GridBagLayout()
        okButton.text = "OK"
        okButton.addActionListener { evt -> okButtonActionPerformed(evt) }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.insets = Insets(0, 0, 0, 5)
        bottomPanel.add(okButton, gridBagConstraints)
        loadBackupButton.text = "Backup laden"
        loadBackupButton.addActionListener { evt -> loadBackupButtonActionPerformed(evt) }
        bottomPanel.add(loadBackupButton, GridBagConstraints())
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 1
        gridBagConstraints.anchor = GridBagConstraints.EAST
        gridBagConstraints.insets = Insets(20, 0, 10, 0)
        loginPanel.add(bottomPanel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.insets = Insets(0, 0, 20, 0)
        contentPane.add(loginPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun okButtonActionPerformed(evt: ActionEvent) {
        val isSuperUser = chooseUserComboBox.selectedIndex == 0

        //
        // Loading...
        //

        resPath?.let {
            dataManager.loadFromCustomFile(it, loadAsSuperUser = isSuperUser) // backup loading
        } ?: kotlin.run {
            dataManager.loadFromCustomFile(loadAsSuperUser = isSuperUser)
        }

        //
        // Initialization
        //
        val password = String(passwordTextField.password)
        if (dataManager.currentUser.isPasswordCorrect(password)) {
            dataManager.initMasterKeys(password)
        } else {
            JOptionPane.showMessageDialog(this, "Das Passwort ist falsch!", "Fehler", JOptionPane.OK_OPTION)
            return
        }
        dataManager.initBuildingObject()
        dispose()
    }

    private fun loadBackupButtonActionPerformed(evt: ActionEvent) {
        val fc = JFileChooser(dataManager.backupDirectory)
        val filter: FileFilter = object : FileFilter() {
            override fun accept(pathname: File): Boolean {
                return pathname.name.endsWith(".dat")
            }

            override fun getDescription(): String {
                return "jLocker-Dateien"
            }
        }
        fc.fileFilter = filter
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        val returnVal = fc.showOpenDialog(this)
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            resPath = fc.selectedFile
        }
    }

    private lateinit var bottomPanel: JPanel
    private lateinit var chooseUserComboBox: JComboBox<String>
    private lateinit var loadBackupButton: JButton
    private lateinit var loginPanel: JPanel
    private lateinit var okButton: JButton
    private lateinit var passwordLabel: JLabel
    private lateinit var passwordTextField: JPasswordField
    private lateinit var splashImageLabel: JLabel
    private lateinit var userNameLabel: JLabel

    init {
        initComponents()

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        // focus in the middle
        setLocationRelativeTo(null)

        // set title from resources
        title = dataManager.appTitle + " " + dataManager.appVersion + " - Anmeldung"

        // Default closing operation
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(winEvt: WindowEvent) {
                System.exit(0)
            }
        })

        // Check if backup directory exists. If not, create it.
        loadBackupButton.isEnabled = dataManager.backupDirectory.exists()
    }
}