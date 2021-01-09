package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.MainFrame
import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.decryptKeyWithString
import com.randomlychosenbytes.jlocker.model.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.crypto.SecretKey
import javax.swing.*
import kotlin.system.exitProcess

class CreateUsersDialog(parent: Frame, modal: Boolean) : JDialog(parent, modal) {

    constructor() : this(MainFrame(), true)

    private var displayedCardIndex: Int
    private val cardLayout: CardLayout = CardLayout()

    private val isFirstRun: Boolean
        get() = dataManager.buildingList.isEmpty()

    private lateinit var superUser: SuperUser
    private lateinit var restrictedUser: RestrictedUser
    private lateinit var superUserMasterKey: SecretKey
    private lateinit var userMasterKey: SecretKey

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        centerPanel = JPanel()
        welcomePanel = JPanel()
        dialogTitleLabel = JLabel()
        welcomeMessageLabel = JLabel()
        superUserPanel = JPanel()
        superUserLabel = JLabel()
        suPasswordLabel = JLabel()
        superUserPasswordTextField = JTextField()
        suRepeatPasswordLabel = JLabel()
        superUserRepeatPasswordTextField = JTextField()
        userPanel = JPanel()
        userLabel = JLabel()
        userPasswordLabel = JLabel()
        userPasswordTextField = JTextField()
        userRepeatPasswordLabel = JLabel()
        userRepeatPasswordTextField = JTextField()
        southPanel = JPanel()
        cancelButton = JButton()
        previousButton = JButton()
        nextButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Benutzer"
        isResizable = false
        contentPane.layout = GridBagLayout()
        centerPanel.layout = CardLayout()
        welcomePanel.layout = GridBagLayout()
        dialogTitleLabel.font = Font("Tahoma", 1, 14) // NOI18N
        dialogTitleLabel.text = "Benutzeranlegungs-Assisstent"
        var gridBagConstraints: GridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        welcomePanel.add(dialogTitleLabel, gridBagConstraints)
        welcomeMessageLabel.text =
            "<html>Willkommen zum Benutzeranlegungs-Assistenten!<p>Dieser Assistent führt Sie durch alle nötigen Schritte, um zwei Benuter zu anzulegen.</p></html>"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.weighty = 1.0
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        welcomePanel.add(welcomeMessageLabel, gridBagConstraints)
        centerPanel.add(welcomePanel, "card2")
        superUserPanel.layout = GridBagLayout()
        superUserLabel.font = Font("Tahoma", 1, 11) // NOI18N
        superUserLabel.text = "SuperUser"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(0, 10, 10, 0)
        superUserPanel.add(superUserLabel, gridBagConstraints)
        suPasswordLabel.text = "Passwort"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 10, 10, 10)
        superUserPanel.add(suPasswordLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        superUserPanel.add(superUserPasswordTextField, gridBagConstraints)
        suRepeatPasswordLabel.text = "Passwort wiederholen"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 10, 10, 10)
        superUserPanel.add(suRepeatPasswordLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        superUserPanel.add(superUserRepeatPasswordTextField, gridBagConstraints)
        centerPanel.add(superUserPanel, "card3")
        userPanel.layout = GridBagLayout()
        userLabel.font = Font("Tahoma", 1, 11) // NOI18N
        userLabel.text = "Eingeschränkter Benutzer"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(0, 10, 10, 0)
        userPanel.add(userLabel, gridBagConstraints)
        userPasswordLabel.text = "Passwort"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 10, 10, 10)
        userPanel.add(userPasswordLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        userPanel.add(userPasswordTextField, gridBagConstraints)
        userRepeatPasswordLabel.text = "Passwort wiederholen"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 10, 10, 10)
        userPanel.add(userRepeatPasswordLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        userPanel.add(userRepeatPasswordTextField, gridBagConstraints)
        centerPanel.add(userPanel, "card4")
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.weighty = 1.0
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        contentPane.add(centerPanel, gridBagConstraints)
        southPanel.layout = GridBagLayout()
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { evt -> cancelButtonActionPerformed(evt) }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.insets = Insets(0, 0, 0, 20)
        southPanel.add(cancelButton, gridBagConstraints)
        previousButton.text = "< Zurück"
        previousButton.addActionListener { evt -> previousButtonActionPerformed(evt) }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 10)
        southPanel.add(previousButton, gridBagConstraints)
        nextButton.text = "Weiter >"
        nextButton.addActionListener { evt -> nextButtonActionPerformed() }
        southPanel.add(nextButton, GridBagConstraints())
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 10, 10, 10)
        contentPane.add(southPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun cancelButtonActionPerformed(evt: ActionEvent) {
        if (isFirstRun) {
            exitProcess(0)
        } else {
            dispose()
        }
    }

    private fun previousButtonActionPerformed(evt: ActionEvent) {
        // Prevent the user from returning to the welcome screen, because
        // that would make no sense.
        if (displayedCardIndex - 1 > 0) {
            displayedCardIndex--
            cardLayout.previous(centerPanel)
        }
        if (displayedCardIndex != 2) {
            nextButton.text = "Weiter >"
        }
    }

    fun nextButtonActionPerformed() {
        if (displayedCardIndex < 2) {
            cardLayout.next(centerPanel) // display next card
        }
        displayedCardIndex++
        when (displayedCardIndex - 1) {
            1 -> {
                val superUserPassword = superUserPasswordTextField.text
                if (superUserPassword.isEmpty() || superUserPassword.length < 8) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Bitte geben Sie ein Passwort mit\nmindestens 8 Zeichen ein!",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                if (superUserPassword == superUserRepeatPasswordTextField.text) {
                    superUser = SuperUser(superUserPassword)
                    userMasterKey = decryptKeyWithString(superUser.encryptedUserMasterKeyBase64, superUserPassword)
                    superUserMasterKey =
                        decryptKeyWithString(superUser.encryptedSuperUMasterKeyBase64, superUserPassword)
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Die Passwörter stimmen nicht überein!",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                previousButton.isEnabled = true
                nextButton.text = "Fertigstellen"
            }
            2 -> {
                val password = userPasswordTextField.text
                if (password.isBlank() || password.length < 8) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Bitte geben Sie ein Passwort mit\nmindestens 8 Zeichen ein!",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                restrictedUser = if (password == userRepeatPasswordTextField.text) {
                    RestrictedUser(password, userMasterKey)
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Die Passwörter stimmen nicht überein!",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }

                dataManager.setNewUsers(superUser, restrictedUser, userMasterKey, superUserMasterKey)

                if (isFirstRun) {
                    // Create initial data
                    dataManager.buildingList.add(Building("-"))
                    dataManager.currentFloorList.add(Floor("-"))
                    dataManager.currentWalkList.add(Walk("-"))
                    dataManager.currentManagmentUnitList.add(ModuleWrapper(LockerCabinet()))
                }

                dataManager.saveAndCreateBackup()
                dispose()
            }
        }
    }

    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var dialogTitleLabel: JLabel
    private lateinit var nextButton: JButton
    private lateinit var previousButton: JButton
    private lateinit var southPanel: JPanel
    private lateinit var suPasswordLabel: JLabel
    private lateinit var superUserPasswordTextField: JTextField
    private lateinit var suRepeatPasswordLabel: JLabel
    private lateinit var superUserRepeatPasswordTextField: JTextField
    private lateinit var superUserLabel: JLabel
    private lateinit var superUserPanel: JPanel
    private lateinit var userLabel: JLabel
    private lateinit var userPanel: JPanel
    private lateinit var userPasswordLabel: JLabel
    private lateinit var userPasswordTextField: JTextField
    private lateinit var userRepeatPasswordLabel: JLabel
    private lateinit var userRepeatPasswordTextField: JTextField
    private lateinit var welcomeMessageLabel: JLabel
    private lateinit var welcomePanel: JPanel

    init {
        initComponents()

        // focus in the middle
        setLocationRelativeTo(null)

        addWindowListener(
            object : WindowAdapter() {
                override fun windowClosing(winEvt: WindowEvent) {
                    if (isFirstRun) {
                        exitProcess(0)
                    } else {
                        (winEvt.source as JDialog).dispose()
                    }
                }
            }
        )
        centerPanel.apply {
            layout = cardLayout
            add(welcomePanel, "card1")
            add(superUserPanel, "card2")
            add(userPanel, "card3")
        }

        displayedCardIndex = 0

        if (!isFirstRun) {
            cardLayout.next(centerPanel)
            displayedCardIndex = 1
        }

        previousButton.isEnabled = false
    }
}