package com.randomlychosenbytes.jlocker.main

import com.google.gson.GsonBuilder
import com.randomlychosenbytes.jlocker.DataManager
import com.randomlychosenbytes.jlocker.decryptKeyWithString
import com.randomlychosenbytes.jlocker.generateAndEncryptKey
import com.randomlychosenbytes.jlocker.model.Building
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.crypto.SecretKey

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataManagerTest {

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun shouldInitBuildings() {

        // create test file

        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        val buildings = listOf(Building("Main Building"))
        val encryptedUserMasterKey = generateAndEncryptKey("test")
        val userMasterKey = decryptKeyWithString(encryptedUserMasterKey, "test")

        dataManager.initBuildingObject()

        assertTrue(dataManager.buildingList.size == 1)
    }

    @InjectMocks
    private lateinit var dataManager: DataManager

    @Mock
    private lateinit var encryptedBuildingsBase64: String

    @Mock
    private lateinit var userMasterKey: SecretKey
}