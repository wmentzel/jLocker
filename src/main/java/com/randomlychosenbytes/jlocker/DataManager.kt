package com.randomlychosenbytes.jlocker

import com.google.gson.GsonBuilder
import com.randomlychosenbytes.jlocker.State.Companion.mainFrame
import com.randomlychosenbytes.jlocker.model.*
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
class DataManager {

    lateinit var ressourceFile: File
    lateinit var backupDirectory: File

    private lateinit var encryptedBuildingsBase64: String
    private lateinit var userMasterKey: SecretKey
    lateinit var superUserMasterKey: SecretKey
        private set

    private lateinit var restrictedUser: RestrictedUser
    private lateinit var superUser: SuperUser

    var tasks: MutableList<Task> = mutableListOf()

    var settings: Settings = Settings()
        private set

    var buildingList: MutableList<Building> = mutableListOf()
        private set

    fun initPath(path: File) {
        ressourceFile = File(path, "jlocker.json")
        backupDirectory = File(path, "Backup")
    }

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
        .flatMap(Walk::moduleWrappers)
        .filter {
            it.module is LockerCabinet
        }.map {
            it.module
        }.filterIsInstance<LockerCabinet>().flatMap {
            it.lockers
        }.firstOrNull {
            it.id == id
        }

    fun isLockerIdUnique(id: String) = getLockerById(id) == null

    fun initBuildingObject() {
        buildingList = unsealAndDeserializeBuildings(encryptedBuildingsBase64, userMasterKey).toMutableList()
    }

    private fun getAllLockers() = sequence {
        State.dataManager.buildingList.forEachIndexed { bIndex, building ->
            building.floors.forEachIndexed { fIndex, floor ->
                floor.walks.forEachIndexed { wIndex, walk ->
                    walk.moduleWrappers.map { it.module }.mapIndexed { index, module ->
                        (module as? LockerCabinet)?.let {
                            index to module
                        }
                    }.filterNotNull().forEach { (mwIndex, module) ->
                        module.lockers.forEachIndexed { lIndex, locker ->
                            yield(EntityCoordinates(locker, bIndex, fIndex, wIndex, mwIndex, lIndex))
                        }
                    }
                }
            }
        }
    }

    fun findLockers(
        id: String? = null,
        lastName: String? = null,
        firstName: String? = null,
        onlyWithContract: Boolean = false,
        schoolClass: String? = null,
        height: String? = null,
        money: String? = null,
        remainingTimeInMonths: String? = null,
        fromDate: String? = null,
        untilDate: String? = null,
        lock: String? = null,
        onlyEmpty: Boolean = false
    ) = getAllLockers().mapNotNull(fun(foundLockerCoords: EntityCoordinates<Locker>): EntityCoordinates<Locker>? {
        val locker = foundLockerCoords.entity

        if (onlyEmpty && !locker.isFree) {
            return null
        }

        if (id != null) {
            if (locker.id != id) {
                return null
            }
        }

        if (lastName != null) {
            if (locker.isFree || lastName != locker.pupil.lastName) {
                return null
            }
        }

        if (firstName != null) {
            if (locker.isFree || firstName != locker.pupil.firstName) {
                return null
            }
        }

        if (onlyWithContract) {
            if (locker.isFree || !locker.pupil.hasContract) {
                return null
            }
        }



        if (schoolClass != null) {
            if (locker.isFree || schoolClass != locker.pupil.schoolClassName) {
                return null
            }
        }

        if (height != null) {
            if (locker.isFree || height.toIntOrNull() != locker.pupil.heightInCm) {
                return null
            }
        }

        if (money != null) {
            if (locker.isFree || money.toIntOrNull() != locker.pupil.paidAmount) {
                return null
            }
        }

        if (remainingTimeInMonths != null) {
            if (locker.isFree || remainingTimeInMonths.toIntOrNull() != locker.pupil.remainingTimeInMonths) {
                return null
            }
        }

        if (fromDate != null) {
            if (locker.isFree || fromDate != locker.pupil.rentedFromDate) {
                return null
            }
        }

        if (untilDate != null) {
            if (locker.isFree || untilDate != locker.pupil.rentedUntilDate) {
                return null
            }
        }

        if (lock != null) {
            if (lock != locker.lockCode) {
                return null
            }
        }

        return foundLockerCoords
    })

    val appTitle: String
    val appVersion: String

    lateinit var currentUser: User
        private set

    val currentFloorList
        get() = currentBuilding.floors

    val currentWalkList
        get() = currentFloor.walks

    val currentManagmentUnitList
        get() = currentWalk.moduleWrappers

    val currentBuilding
        get() = buildingList[currentBuildingIndex]

    val currentFloor
        get() = currentFloorList[currentFloorIndex]

    val currentWalk
        get() = currentWalkList[currentWalkIndex]

    val currentManamentUnit
        get() = currentManagmentUnitList[currentManagementUnitIndex]

    val currentLockerList
        get() = (currentManamentUnit.module as? LockerCabinet)?.lockers ?: emptyList()

    val currentLocker: Locker
        get() = currentLockerList[currentLockerIndex]

    var currentBuildingIndex = 0
    var currentFloorIndex = 0
    var currentWalkIndex = 0
    var currentManagementUnitIndex = 0
    var currentLockerIndex = 0

    init {
        val bundle = ResourceBundle.getBundle("App")
        appTitle = bundle.getString("Application.title")
        appVersion = bundle.getString("Application.version")
    }
}