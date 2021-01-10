package com.randomlychosenbytes.jlocker.main

import assertk.assertThat
import assertk.assertions.hasSize
import com.nhaarman.mockitokotlin2.mock
import com.randomlychosenbytes.jlocker.DataManager
import com.randomlychosenbytes.jlocker.State.Companion.mainFrame
import com.randomlychosenbytes.jlocker.decryptKeyWithString
import com.randomlychosenbytes.jlocker.model.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class DataManagerTest {

    @BeforeEach
    fun setup() {
    }

    private val superUserPassword = "111111111"
    private val restrictedUserPassword = "22222222"
    private val jlockerTestDirectory = File("/tmp/jlockertests")
    private val jlockerTestFile = File(jlockerTestDirectory, "jlocker.json")

    private fun DataManager.createResourceFile() {
        val superUser = SuperUser(superUserPassword)
        val userMasterKey = decryptKeyWithString(superUser.encryptedUserMasterKeyBase64, superUserPassword)
        val superUserMasterKey = decryptKeyWithString(superUser.encryptedSuperUMasterKeyBase64, superUserPassword)
        val restrictedUser = RestrictedUser(restrictedUserPassword, userMasterKey)

        setNewUsers(superUser, restrictedUser, userMasterKey, superUserMasterKey)

        buildingList.add(Building("-"))
        currentFloorList.add(Floor("-"))
        currentWalkList.add(Walk("-"))
        currentManagmentUnitList.add(ModuleWrapper(LockerCabinet()))

        jlockerTestDirectory.mkdirs()
        initPath(jlockerTestDirectory)

        mainFrame = mock()

        saveAndCreateBackup()
    }

    @Test
    fun `should reencrypt codes when new users are set`() {
        val dataManager = DataManager()
        dataManager.createResourceFile()

        val locker = Locker(id = "1").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Don"
                lastName = "Draper"
                heightInCm = 175
                schoolClassName = "12"
                setCodes(
                    arrayOf(
                        "11-11-11",
                        "22-22-22",
                        "33-33-33",
                        "44-44-44",
                        "55-55-55"
                    ), dataManager.superUserMasterKey
                )
            })
        }

        dataManager.buildingList[0].floors[0].walks[0].moduleWrappers[0].lockerCabinet.lockers.add(locker)

        assertDoesNotThrow {
            locker.getCodes(dataManager.superUserMasterKey)
        }

        val newSuperUserPassword = "33333333"
        val superUser = SuperUser("33333333")
        val userMasterKey = decryptKeyWithString(superUser.encryptedUserMasterKeyBase64, newSuperUserPassword)
        val superUserMasterKey = decryptKeyWithString(superUser.encryptedSuperUMasterKeyBase64, newSuperUserPassword)
        val restrictedUser = RestrictedUser(restrictedUserPassword, userMasterKey)

        dataManager.setNewUsers(superUser, restrictedUser, userMasterKey, superUserMasterKey)

        assertDoesNotThrow {
            locker.getCodes(dataManager.superUserMasterKey)
        }
    }

    @Test
    fun `should create data file from scratch and load it as super user`() {
        val dataManager = DataManager()
        dataManager.createResourceFile()
        dataManager.loadFromCustomFile(jlockerTestFile, loadAsSuperUser = true)
        assertThat(dataManager.buildingList).hasSize(1)
    }

    @Test
    fun `should create data file from scratch and load it as restricted user`() {
        val dataManager = DataManager()
        dataManager.createResourceFile()
        dataManager.loadFromCustomFile(jlockerTestFile, loadAsSuperUser = false)
        assertThat(dataManager.buildingList).hasSize(1)
    }

    @Test
    fun `should create data file from scratch and save it`() {
        DataManager().createResourceFile()
        assertTrue(File(jlockerTestDirectory, "jlocker.json").exists())
    }

    @Test
    fun `should load existing data as a super user`() {
        DataManager().createResourceFile()

        val dataManager = DataManager() // initialize from scratch

        mainFrame = mock()
        dataManager.loadFromCustomFile(jlockerTestFile, loadAsSuperUser = true)
        dataManager.initMasterKeys(superUserPassword)
        dataManager.initBuildingObject()

        assertThat(dataManager.buildingList).hasSize(1)
    }

    @Test
    fun `should load existing data as a restricted user`() {
        DataManager().createResourceFile()

        val dataManager = DataManager() // initialize from scratch

        mainFrame = mock()
        dataManager.loadFromCustomFile(jlockerTestFile, loadAsSuperUser = false)
        dataManager.initMasterKeys(restrictedUserPassword)
        dataManager.initBuildingObject()

        assertThat(dataManager.buildingList).hasSize(1)
    }
}