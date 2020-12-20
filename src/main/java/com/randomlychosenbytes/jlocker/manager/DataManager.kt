package com.randomlychosenbytes.jlocker.manager

import com.google.gson.GsonBuilder
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit
import com.randomlychosenbytes.jlocker.main.MainFrame
import com.randomlychosenbytes.jlocker.nonabstractreps.*
import java.io.File
import java.io.FileFilter
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import javax.crypto.SecretKey

/**
 * DataManager is a singleton class. There can only be one instance of this
 * class at any time and it has to be accessed from anywhere. This may not be
 * the best design but it stays that way for the time being.
 */
object DataManager {
    /**
     * TODO remove
     * instance of the MainFrame object is need to call the setStatusMessage method
     */
    lateinit var mainFrame: MainFrame

    val ressourceFile: File
    val backupDirectory: File

    var hasDataChanged = false
    var buildingList: List<Building> = mutableListOf()
        private set

    private lateinit var restrictedUser: RestrictedUser
    private lateinit var superUser: SuperUser
    lateinit var currentUser: User
        private set

    lateinit var tasks: MutableList<Task>

    var settings: Settings = Settings()
        private set

    private lateinit var encryptedBuildingsBase64: String

    private lateinit var userMasterKey: SecretKey

    lateinit var superUserMasterKey: SecretKey
        private set

    fun initMasterKeys(currentUserPassword: String) {

        userMasterKey = decryptKeyWithString(
            currentUser.encryptedUserMasterKeyBase64,
            currentUserPassword
        )

        (currentUser as? SuperUser)?.let {
            superUserMasterKey = decryptKeyWithString(it.encryptedSuperUMasterKeyBase64, currentUserPassword)
        }
    }

    fun setNewUsers(
        superUser: SuperUser,
        restrictedUser: RestrictedUser,
        userMasterKey: SecretKey,
        superUserMasterKey: SecretKey
    ) {
        this.superUser = superUser
        this.restrictedUser = restrictedUser
        this.userMasterKey = userMasterKey
        this.superUserMasterKey = superUserMasterKey
        currentUser = superUser
    }

    /**
     * Saves all data and creates a backup file with a time stamp.
     */
    fun saveAndCreateBackup() {
        saveData(ressourceFile) // save to file jlocker.dat

        // Check if backup directory exists. If not, create it.
        if (!backupDirectory.exists() && !backupDirectory.mkdir()) {
            println("Backup failed!")
        }

        //
        // Check if a buildings.dat file exists to copy it to the backup directory.
        //
        val today: Calendar = GregorianCalendar().apply {
            isLenient = false
        }

        val backupFile = File(
            backupDirectory, String.format(
                "jlocker-%04d-%02d-%02d.dat",
                today[Calendar.YEAR],
                today[Calendar.MONTH],
                today[Calendar.DAY_OF_MONTH]
            )
        )

        // if a backup from this day doesnt exist, create one!
        if (!backupFile.exists()) {
            saveData(backupFile)
        }

        //
        // Just keep a certain number of last saved building files
        //
        if (backupDirectory.exists()) { // if there are not backups yet, we dont have to delete any files

            // This filter only returns files (and not directories)
            val fileFilter = FileFilter { file -> !file.isDirectory }
            val files = backupDirectory.listFiles(fileFilter)
            for (i in 0 until files.size - settings.numOfBackups) {
                print("* delete backup file: \"" + files[i].name + "\"...")
                if (files[i].delete()) {
                    println(" successful!")
                } else {
                    println(" failed!")
                }
            }
        }
    }

    private fun saveData(file: File) {
        print("* saving " + file.name + "... ")

        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        encryptedBuildingsBase64 = encrypt(gson.toJson(buildingList), userMasterKey)
        FileWriter(file).use { writer ->
            gson.toJson(
                JsonRoot(
                    encryptedBuildingsBase64,
                    settings,
                    tasks,
                    superUser,
                    restrictedUser
                ), writer
            )
            println("successful")
            mainFrame.setStatusMessage("Speichern erfolgreich")
        }
    }

    /**
     * Loads the data from an arbitry file path and initializes the users,
     * buildings, tasks and settings objects. When called directly this is used
     * to load backup files. If you want to load the current "jlocker.dat" file
     * please use loadData() method instead.
     */
    @JvmOverloads
    fun loadFromCustomFile(file: File = ressourceFile, loadAsSuperUser: Boolean) {
        print("* reading " + file.name + "... ")
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

        FileReader(file).use { reader ->
            val root = gson.fromJson(reader, JsonRoot::class.java)

            superUser = root.superUser
            restrictedUser = root.restrictedUser

            currentUser = if (loadAsSuperUser) {
                superUser
            } else {
                restrictedUser
            }
            encryptedBuildingsBase64 = root.encryptedBuildingsBase64
            tasks = root.tasks.toMutableList()
            settings = root.settings
            println("successful")
            mainFrame.setStatusMessage("Laden erfolgreich")
        }
    }

    fun getLockerById(id: String) = buildingList.asSequence()
        .flatMap(Building::floors)
        .flatMap(Floor::walks)
        .flatMap(Walk::managementUnits)
        .filter {
            it.type == ManagementUnit.LOCKER_CABINET
        }.map {
            it.lockerCabinet
        }.flatMap {
            it.lockers
        }.firstOrNull {
            it.id == id
        }

    /**
     * Moves a student from one locker to another.
     */
    fun moveLockers(sourceLocker: Locker, destLocker: Locker) {

        if (sourceLocker.isFree) {
            throw IllegalStateException("The source locker does not have an owner who could be moved to a new locker.")
        }

        if (!destLocker.isFree) {
            throw IllegalStateException("The destination locker still has an owner who has to be unassigned before a new owner can be assigned.")
        }

        destLocker.moveInNewOwner(sourceLocker.pupil)
        sourceLocker.empty()
    }

    fun updateDummyRowsOfAllCabinets() {
        val cabinets = currentManagmentUnitList.map { it.lockerCabinet }
        val maxRows = cabinets.map { it.lockers.size }.maxOrNull() ?: 0
        cabinets.forEach { it.updateDummyRows(maxRows) }
    }

    fun reinstantiateManagementUnits(
        managementUnits: List<ManagementUnit>
    ) = managementUnits.map { it to ManagementUnit(it.type) }.map { (oldMu, newMu) ->
        ManagementUnit(oldMu.type).also { newMu ->
            when (oldMu.type) {
                ManagementUnit.ROOM -> {
                    newMu.room.setCaption(oldMu.room.roomName, oldMu.room.schoolClassName)
                }
                ManagementUnit.LOCKER_CABINET -> {
                    val newLockers = oldMu.lockerCabinet.lockers.map { locker: Locker -> Locker(locker) }
                    newMu.lockerCabinet.lockers = newLockers
                }
                ManagementUnit.STAIRCASE -> {
                    newMu.staircase.setCaption(oldMu.staircase.staircaseName)
                }
            }
        }
    }

    fun isLockerIdUnique(id: String) = getLockerById(id) == null

    fun initBuildingObject() {
        buildingList = unsealAndDeserializeBuildings(encryptedBuildingsBase64, userMasterKey)
    }

    val appTitle: String
    val appVersion: String

    val currentFloorList: List<Floor>
        get() = currentBuilding.floors

    val currentWalkList: List<Walk>
        get() = currentFloor.walks

    val currentManagmentUnitList: List<ManagementUnit>
        get() = currentWalk.managementUnits

    val currentBuilding: Building
        get() = buildingList[currentBuildingIndex]

    val currentFloor: Floor
        get() = currentFloorList[currentFloorIndex]

    val currentWalk: Walk
        get() = currentWalkList[currentWalkIndex]

    val currentManamentUnit: ManagementUnit
        get() = currentManagmentUnitList[currentManagementUnitIndex]

    val currentLockerList: List<Locker>
        get() = currentManamentUnit.lockerCabinet.lockers

    val currentLocker: Locker
        get() = currentLockerList[currentLockerIndex]

    val currentRoom: Room
        get() = currentManamentUnit.room

    val currentLockerCabinet: LockerCabinet
        get() = currentManamentUnit.lockerCabinet

    var currentBuildingIndex = 0
    var currentFloorIndex = 0
    var currentWalkIndex = 0
    var currentManagementUnitIndex = 0
    var currentLockerIndex = 0

    init {
        val bundle = ResourceBundle.getBundle("App")
        appTitle = bundle.getString("Application.title")
        appVersion = bundle.getString("Application.version")

        val url = MainFrame::class.java.protectionDomain.codeSource.location
        var homeDirectory = File(url.file)

        if (!homeDirectory.isDirectory) {
            homeDirectory = homeDirectory.parentFile
        }

        ressourceFile = File(homeDirectory, "jlocker.json")
        backupDirectory = File(homeDirectory, "Backup")

        println("* program directory is: \"$homeDirectory\"")
    }
}