package com.randomlychosenbytes.jlocker.main

import com.nhaarman.mockitokotlin2.*
import com.randomlychosenbytes.jlocker.DataManager
import com.randomlychosenbytes.jlocker.State
import com.randomlychosenbytes.jlocker.dialogs.CreateUsersDialog
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import javax.swing.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CreateUsersDialogTest {

    @InjectMocks
    private lateinit var dialog: CreateUsersDialog

    val dataManager = mock<DataManager>()

    init {
        State.dataManager = dataManager
    }

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @AfterEach
    fun reset() {
        reset(dataManager)
    }

    @Test
    fun `should show error message if super user password is blank`() {

        whenever(superUserPasswordTextField.text).thenReturn("")

        Mockito.mockStatic(JOptionPane::class.java).use { jOptionPaneMock ->
            dialog.nextButtonActionPerformed() // skip intro
            dialog.nextButtonActionPerformed() // confirm super user settings

            jOptionPaneMock.verify {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Bitte geben Sie ein Passwort mit\nmindestens 8 Zeichen ein!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    @Test
    fun `should show error message if repeated password does not match`() {
        whenever(superUserPasswordTextField.text).thenReturn("11111111")
        whenever(superUserRepeatPasswordTextField.text).thenReturn("11111111_")

        Mockito.mockStatic(JOptionPane::class.java).use { jOptionPaneMock ->
            dialog.nextButtonActionPerformed() // skip intro
            dialog.nextButtonActionPerformed() // confirm super user settings

            jOptionPaneMock.verify {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Die Passwörter stimmen nicht überein!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }


    @Test
    fun `should initialize users on the first run`() {
        whenever(superUserPasswordTextField.text).thenReturn("11111111")
        whenever(superUserRepeatPasswordTextField.text).thenReturn("11111111")

        whenever(userPasswordTextField.text).thenReturn("22222222")
        whenever(userRepeatPasswordTextField.text).thenReturn("22222222")

        dialog.nextButtonActionPerformed() // skip intro
        dialog.nextButtonActionPerformed() // confirm super user settings
        dialog.nextButtonActionPerformed() // confirm user settings

        verify(dataManager).setNewUsers(any(), any(), any(), any())
        verify(dataManager).saveAndCreateBackup()
    }

    @Mock
    private lateinit var cancelButton: JButton

    @Mock
    private lateinit var dialogTitleLabel: JLabel

    @Mock
    private lateinit var nextButton: JButton

    @Mock
    private lateinit var previousButton: JButton

    @Mock
    private lateinit var southPanel: JPanel

    @Mock
    private lateinit var suPasswordLabel: JLabel

    @Mock
    private lateinit var superUserPasswordTextField: JTextField

    @Mock
    private lateinit var suRepeatPasswordLabel: JLabel

    @Mock
    private lateinit var superUserRepeatPasswordTextField: JTextField

    @Mock
    private lateinit var superUserLabel: JLabel

    @Mock
    private lateinit var superUserPanel: JPanel

    @Mock
    private lateinit var userLabel: JLabel

    @Mock
    private lateinit var userPanel: JPanel

    @Mock
    private lateinit var userPasswordLabel: JLabel

    @Mock
    private lateinit var userPasswordTextField: JTextField

    @Mock
    private lateinit var userRepeatPasswordLabel: JLabel

    @Mock
    private lateinit var userRepeatPasswordTextField: JTextField

    @Mock
    private lateinit var welcomeMessageLabel: JLabel

    @Mock
    private lateinit var welcomePanel: JPanel
}