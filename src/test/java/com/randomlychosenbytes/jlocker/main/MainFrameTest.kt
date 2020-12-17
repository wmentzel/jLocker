package com.randomlychosenbytes.jlocker.main

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.randomlychosenbytes.jlocker.manager.DataManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.swing.JPanel

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainFrameTest {

    @Mock
    private lateinit var containerPanel: JPanel

    @Mock
    private lateinit var dataManager: DataManager


    @InjectMocks
    private lateinit var mainFrame: MainFrame

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun test() {
        whenever(dataManager.curLockerList).thenReturn(listOf())
        mainFrame.showLockerInformation()
        verify(containerPanel).setVisible(ArgumentMatchers.anyBoolean())
    }
}