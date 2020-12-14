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
import java.util.stream.Collectors
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
        private set;

    lateinit var tasks: MutableList<Task>

    var settings: Settings = Settings()
        private set

    private lateinit var encryptedBuildingsBase64: String

    var currentBuildingIndex = 0
    var currentFloorIndex = 0
    var currentWalkIndex = 0
    var currentManagementUnitIndex = 0
    var currentLockerIndex = 0


    lateinit var userMasterKey: SecretKey
    lateinit var superUserMasterKey: SecretKey

    private val bundle = ResourceBundle.getBundle("App")

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
        val today: Calendar = GregorianCalendar()
        today.isLenient = false
        today.time
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
            val fileFilter = FileFilter { file: File -> !file.isDirectory }
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

    fun loadDefaultFile(loadAsSuperUser: Boolean) {
        loadFromCustomFile(ressourceFile, loadAsSuperUser)
    }

    /**
     * Loads the data from an arbitry file path and initializes the users,
     * buildings, tasks and settings objects. When called directly this is used
     * to load backup files. If you want to load the current "jlocker.dat" file
     * please use loadData() method instead.
     */
    fun loadFromCustomFile(file: File, loadAsSuperUser: Boolean) {
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
    fun moveLockers(sourceLocker: Locker, destLocker: Locker, withCodes: Boolean) {
        val destCopy = destLocker.copy
        destLocker.setTo(sourceLocker)
        sourceLocker.setTo(destCopy)
        if (withCodes) {
            destLocker.setCodes(sourceLocker.getCodes(superUserMasterKey), superUserMasterKey)
            sourceLocker.setCodes(destCopy.getCodes(superUserMasterKey), superUserMasterKey)
        }
    }

    fun updateAllCabinets() {
        val mus: List<ManagementUnit> = curManagmentUnitList
        val maxRows = mus.map { mu: ManagementUnit -> mu.lockerCabinet.lockers.size }.maxOrNull() ?: 0
        mus.map { obj: ManagementUnit -> obj.lockerCabinet }.forEach { c: LockerCabinet -> c.updateCabinet(maxRows) }
    }

    fun reinstantiateManagementUnits(
        managementUnits: List<ManagementUnit>
    ) = managementUnits.map { mu: ManagementUnit ->
        val newMu = ManagementUnit(mu.type)
        when (mu.type) {
            ManagementUnit.ROOM -> {
                newMu.room.setCaption(mu.room.roomName, mu.room.schoolClassName)
            }
            ManagementUnit.LOCKER_CABINET -> {
                val newLockers = mu.lockerCabinet.lockers
                    .stream()
                    .map { locker: Locker? -> Locker(locker!!) }
                    .collect(Collectors.toList())
                newMu.lockerCabinet.lockers = newLockers
            }
            ManagementUnit.STAIRCASE -> {
                newMu.staircase.setCaption(mu.staircase.staircaseName)
            }
        }
        newMu
    }

    val appTitle: String
        get() = bundle.getString("Application.title")
    val appVersion: String
        get() = bundle.getString("Application.version")

    fun isLockerIdUnique(id: String): Boolean {
        return getLockerById(id) == null
    }

    val curFloorList: List<Floor>
        get() = curBuilding.floors

    val curWalkList: List<Walk>
        get() = curFloor.walks

    val curManagmentUnitList: List<ManagementUnit>
        get() = curWalk.managementUnits

    val curBuilding: Building
        get() = buildingList[currentBuildingIndex]
    val curFloor: Floor
        get() = curFloorList.get(currentFloorIndex)
    val curWalk: Walk
        get() = curWalkList.get(currentWalkIndex)
    val curManamentUnit: ManagementUnit
        get() = curManagmentUnitList.get(currentManagementUnitIndex)
    val curLockerList: List<Locker>
        get() = curManamentUnit.lockerCabinet.lockers
    val curLocker: Locker
        get() = curLockerList[currentLockerIndex]
    val curRoom: Room
        get() = curManamentUnit.room
    val curLockerCabinet: LockerCabinet
        get() = curManamentUnit.lockerCabinet

    fun hasDataChanged(): Boolean {
        return hasDataChanged
    }

    fun initBuildingObject() {
        buildingList = unsealAndDeserializeBuildings(encryptedBuildingsBase64, userMasterKey)
    }

    init {
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