package com.randomlychosenbytes.jlocker.main

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.randomlychosenbytes.jlocker.manager.DataManager
import com.randomlychosenbytes.jlocker.nonabstractreps.Locker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainFrameTest {

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun shouldHidePanelWhenThereAreNoLockersToShow() {
        whenever(dataManager.currentLockerList).thenReturn(listOf())
        mainFrame.showLockerInformation()
        verify(containerPanel).isVisible = false
    }

    @Test
    fun shouldShowLockerInformation() {
        whenever(dataManager.currentLockerList).thenReturn(listOf(Locker()))
        whenever(dataManager.currentLocker).thenReturn(Locker())

        mainFrame.showLockerInformation()

        verify(containerPanel).isVisible = true
    }

    @Mock
    private lateinit var lockerIDTextField: JTextField

    @Mock
    private lateinit var outOfOrderCheckbox: JCheckBox

    @Mock
    private lateinit var codeTextField: JTextField

    @Mock
    private lateinit var lockTextField: JTextField

    @Mock
    private lateinit var noteTextArea: JTextField

    @Mock
    private lateinit var containerPanel: JPanel

    @Mock
    private lateinit var dataManager: DataManager

    @Mock
    private lateinit var surnameTextField: JTextField

    @Mock
    private lateinit var nameTextField: JTextField

    @Mock
    private lateinit var classTextField: JTextField

    @Mock
    private lateinit var sizeTextField: JTextField

    @Mock
    private lateinit var hasContractCheckbox: JCheckBox

    @Mock
    private lateinit var moneyTextField: JTextField

    @Mock
    private lateinit var previousAmountTextField: JTextField

    @Mock
    private lateinit var fromDateTextField: JTextField

    @Mock
    private lateinit var untilDateTextField: JTextField

    @Mock
    private lateinit var remainingTimeInMonthsTextField: JTextField

    @InjectMocks
    private lateinit var mainFrame: MainFrame
}