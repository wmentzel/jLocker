package com.randomlychosenbytes.jlocker

import com.randomlychosenbytes.jlocker.model.*
import com.randomlychosenbytes.jlocker.utils.moveOwner
import org.jgrapht.alg.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs

class ShortenClassRoomDistances(
    private val buildings: List<Building>,
    private val lockerMinSizes: List<Int>,

    private val className: String,
    private val createTask: (String) -> Unit
) {
    //
    // Calibration for the algorithm: edge weights
    //
    private val weightedGraph = SimpleWeightedGraph<String?, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

    private lateinit var classLockerToDistancePairList: List<Pair<EntityCoordinates<Locker>, Int>>
    private lateinit var freeLockerToDistancePairList: List<Pair<EntityCoordinates<Locker>, Int>>
    private lateinit var unreachableLockers: MutableList<String>
    private lateinit var classRoomNodeId: String

    fun check(): Status {

        var classRoomNodeId: String? = null

        buildings.forEachIndexed { bIndex, building ->
            building.floors.forEachIndexed { fIndex, floor ->
                floor.walks.forEachIndexed { wIndex, walk ->
                    walk.moduleWrappers.forEachIndexed { mwIndex, moduleWrapper ->
                        if ((moduleWrapper.module as? Room)?.schoolClassName == className) {
                            classRoomNodeId = "$bIndex-$fIndex-$wIndex-$mwIndex"
                        }
                    }
                }
            }
        }

        classRoomNodeId?.let {
            this.classRoomNodeId = it
        } ?: kotlin.run {
            return Status.ClassRoomForSpecifiedClassDoesNotExist
        }

        val freeLockersEntityCoordinatesList: MutableList<EntityCoordinates<Locker>> = mutableListOf()
        val classLockersEntityCoordinatesList: MutableList<EntityCoordinates<Locker>> = mutableListOf()

        // creates a weighted graph
        val allLockersEntityCoordinatesList = connectManagementUnitsAndLockers()
        connectWalksOnFloor()
        connectFloorsByStaircases()
        connectBuildinsByStaircases()

        // create a list of all free lockers
        // and all the lockers that belong to people of that class
        for (lockerEntityCoordinates in allLockersEntityCoordinatesList) {
            if (lockerEntityCoordinates.entity.isFree) {
                freeLockersEntityCoordinatesList.add(lockerEntityCoordinates)
            }

            if (!lockerEntityCoordinates.entity.isFree && lockerEntityCoordinates.entity.pupil.schoolClassName == className) {
                classLockersEntityCoordinatesList.add(lockerEntityCoordinates)
            }
        }

        if (freeLockersEntityCoordinatesList.isEmpty()) {
            return Status.NoFreeLockersAvailable
        }

        if (classLockersEntityCoordinatesList.isEmpty()) {
            return Status.ClassHasNoPupils
        }

        //
        // Sort free lockers, beginning with the one that is the closest one
        //
        val unreachableLockers = mutableListOf<String>()

        val freeLockerToDistancePairList = mutableListOf<Pair<EntityCoordinates<Locker>, Int>>()
        for (freeLockerECoord in freeLockersEntityCoordinatesList) {
            val dist = getDistance(freeLockerECoord, this.classRoomNodeId)
            if (dist == -1) {
                // Create list that contains the ids of all lockers that cant
                // be reached from the classroom
                unreachableLockers.add(freeLockerECoord.entity.id)
            } else {
                freeLockerToDistancePairList.add(freeLockerECoord to dist)
            }
        }

        this.freeLockerToDistancePairList =
            freeLockerToDistancePairList.sortedWith(EntityDistanceComparator(reversed = false))

        //
        // Sort class lockers, beginning with the one that is the farthest 
        // distance to the class room
        //

        val classLockerToDistancePairList = mutableListOf<Pair<EntityCoordinates<Locker>, Int>>()

        for (classLockerECoord in classLockersEntityCoordinatesList) {
            val distance = getDistance(classLockerECoord, this.classRoomNodeId)
            if (distance == -1) {
                // Create a list that contains the ids of all lockers that can't
                // be reached from the classroom
                unreachableLockers.add(classLockerECoord.entity.id)
            } else {
                classLockerToDistancePairList.add(classLockerECoord to distance)
            }
        }

        this.classLockerToDistancePairList =
            classLockerToDistancePairList.sortedWith(EntityDistanceComparator(reversed = true))
        this.unreachableLockers = unreachableLockers

        // Output how many lockers cant be reached
        return if (unreachableLockers.isEmpty()) {
            Status.Success
        } else {
            Status.NonReachableLockersExist
        }
    }

    /**
     * Does the actual moving based on the data gathered before.
     * and returns a list of the moving operations.
     */
    fun execute(): String {
        val statusMessage = StringBuilder()

        statusMessage.append(
            """
            |Es gibt ${classLockerToDistancePairList.size} Schließfächer der Klasse $className\n
            |Es wurden ${freeLockerToDistancePairList.size} freie Schließfächer gefunden!\n\n
            |Es wurden ${lockerMinSizes.size} Minmalgrößen (cm) angelegt!\n
            """.trimMargin()
        )

        for (size in lockerMinSizes) {
            statusMessage.append("$size ")
        }

        val freeLockerToDistancePairList = freeLockerToDistancePairList.toMutableList()

        statusMessage.append("\n\n")
        for (classLockerToDistancePair in classLockerToDistancePairList) {
            // search until you find a free locker that is nearer and suits the pupils size
            for (freeLockerToDistancePair in freeLockerToDistancePairList) {
                // Is distance of the new locker shorter to the classroom?
                if (classLockerToDistancePair.second > freeLockerToDistancePair.second) {
                    val srcLocker = classLockerToDistancePair.first.entity
                    val destLocker = freeLockerToDistancePair.first.entity

                    // determine minimum size for this locker
                    val index = freeLockerToDistancePair.first.lValue

                    // if no minimum size exists for this locker row, don't move
                    if (index >= lockerMinSizes.size) {
                        continue
                    }
                    // TODO lowest locker has coordinate 4, highest coordinate 0...
                    // this doesn't make sense
                    val lockerMinSize = lockerMinSizes[abs(index - (lockerMinSizes.size - 1))]

                    if (srcLocker.pupil.heightInCm < lockerMinSize) {
                        continue
                    }

                    statusMessage.append(srcLocker.id).append(" -> ").append(destLocker.id).append("\n")
                    statusMessage.append("Besitzergröße: ")
                        .append(classLockerToDistancePair.first.entity.pupil.heightInCm).append(" cm\n")
                    statusMessage.append("Minimalgröße: ").append(lockerMinSize).append("\n")

                    val distanceReduction =
                        (1.0f - freeLockerToDistancePair.second / classLockerToDistancePair.second
                            .toFloat()) * 100
                    val df = DecimalFormat("##.#")
                    statusMessage.append("Entfernung verkürzt um: ")
                        .append(df.format(distanceReduction.toDouble())).append("%\n\n")

                    moveOwner(srcLocker, destLocker)

                    freeLockerToDistancePairList.remove(freeLockerToDistancePair) // this one is now occupied, so remove it
                    val taskText = "Klassenumzug (${destLocker.pupil.schoolClassName} ): ${srcLocker.id} -> "
                    "${destLocker.id} Inhaber(in) ${destLocker.pupil.firstName} ${destLocker.pupil.lastName}"

                    createTask(taskText)
                    break
                }
            }
        }
        return statusMessage.toString()
    }

    val idsOfUnreachableLockers: String
        get() = unreachableLockers.withIndex().joinToString(separator = ", ") {
            if (it.index % 15 == 0) {
                "${it.value}\n"
            } else {
                it.value
            }
        }

    /**
     * Connects MangementUnits on a floor with each other and each ManamentUnit
     * with its lockers.
     */
    private fun connectManagementUnitsAndLockers(): MutableList<EntityCoordinates<Locker>> {

        val allLockersEntityCoordinatesList: MutableList<EntityCoordinates<Locker>> = mutableListOf()

        for (b in buildings.indices) {
            val floors = buildings[b].floors
            for (f in floors.indices) {
                val walks = floors[f].walks
                for (w in walks.indices) {
                    val managementUnits = walks[w].moduleWrappers
                    var previousMUnitId: String? = null

                    // Connect ManagementUnits with each other
                    for (m in managementUnits.indices) {
                        val munit = managementUnits[m]
                        val currentMUnitID = createNodeId(b, f, w, m)
                        weightedGraph.addVertex(currentMUnitID)

                        // connect with previous munit
                        if (m > 0) {
                            val edge = weightedGraph.addEdge(previousMUnitId, currentMUnitID)
                            weightedGraph.setEdgeWeight(edge, managementUnitToMUEdgeWeight.toDouble())
                        }

                        // vertices have been connected, set prevMUnitID for next run
                        previousMUnitId = currentMUnitID
                        (munit.module as? LockerCabinet)?.lockers?.let { lockers ->

                            // connect each locker with its ManagementUnit
                            for (l in lockers.indices) {
                                val locker = lockers[l]
                                val lockerEntityCoordinates = EntityCoordinates(locker, b, f, w, m, l)
                                weightedGraph.addVertex(locker.id)

                                // Connect lockers with their ManagmentUnit
                                val edge = weightedGraph.addEdge(currentMUnitID, locker.id)

                                // ignore if no id has been set, or same id is assigned more than once
                                if (locker.id.isEmpty()) {
                                    println("Error Locker $lockerEntityCoordinates cannot be connected to its management unit because it does not have a valid id.")
                                    continue
                                }

                                if (edge == null) {
                                    println("""Error Locker with id "${locker.id}" cannot be connected to its management unit because another locker with the same id was already connected.""")
                                    continue
                                }

                                weightedGraph.setEdgeWeight(edge, managementUnitToLockerEdgeWeight.toDouble())

                                // fill locker in the list containing all
                                // locker coordinates
                                allLockersEntityCoordinatesList.add(lockerEntityCoordinates)
                            }
                        }
                    }
                }
            }
        }

        return allLockersEntityCoordinatesList
    }

    /**
     * Connects the walks with each other on every floor
     */
    private fun connectWalksOnFloor() {
        for (b in buildings.indices) {
            val floors = buildings[b].floors
            for (f in floors.indices) {
                val walks = floors[f].walks

                // we start with w = 1 because we connect every walk with the
                // walks before
                for (w in 1 until walks.size) {
                    val lastMUnitIndex = walks[w - 1].moduleWrappers.size - 1
                    val edge =
                        weightedGraph.addEdge(createNodeId(b, f, w - 1, lastMUnitIndex), createNodeId(b, f, w, 0))
                    weightedGraph.setEdgeWeight(edge, walkToWalkEdgeWeight.toDouble())
                }
            }
        }
    }

    /**
     * Connects the floors of a building with each other
     */
    private fun connectFloorsByStaircases() {
        for (b in buildings.indices) {
            val floors = buildings[b].floors
            for (f in floors.indices) {
                val walks = floors[f].walks
                for (w in walks.indices) {
                    val managementUnits = walks[w].moduleWrappers
                    for (m in managementUnits.indices) {
                        val munit = managementUnits[m]

                        // connect every managementUnit with the munits above that have the same name
                        (munit.module as? Staircase)?.let { staircase ->

                            if (buildings[b].floors.size - 1 >= f + 1) {
                                val currentMUnitID = createNodeId(b, f, w, m)
                                val ids = findStaircasesOnFloor(b, f + 1, staircase.staircaseName)
                                for (id in ids) {
                                    val edge = weightedGraph.addEdge(currentMUnitID, id)
                                    weightedGraph.setEdgeWeight(edge, floorToFloorEdgeWeight.toDouble())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the IDs of all ManagementUnits on the given floor with the given name
     */
    private fun findStaircasesOnFloor(b: Int, f: Int, name: String): List<String> {
        val walks = buildings[b].floors[f].walks
        return findStaircasesWithName(name, walks).map { (w, m) ->
            createNodeId(b, f, w, m)
        }
    }

    private fun findStaircasesWithName(name: String, walks: List<Walk>): List<Pair<Int, Int>> {

        val walkToManagementUnit: MutableList<Pair<Int, Int>> = mutableListOf()

        for (w in walks.indices) {
            val managementUnits = walks[w].moduleWrappers
            for (m in managementUnits.indices) {
                val managementUnit = managementUnits[m]
                if (managementUnit.module is Staircase) {
                    if ((managementUnit.module as? Staircase)?.staircaseName == name) {
                        walkToManagementUnit.add(w to m)
                    }
                }
            }
        }

        return walkToManagementUnit
    }

    /**
     * Connects buildings by staircases
     */
    private fun connectBuildinsByStaircases() {
        // start with b = 1 so we connect with previous buildings
        for (b in 1 until buildings.size) {
            val floors = buildings[b].floors
            for (f in floors.indices) {
                val walks = floors[f].walks
                for (w in walks.indices) {
                    val managementUnits = walks[w].moduleWrappers
                    for (m in managementUnits.indices) {
                        val munit = managementUnits[m]

                        // connect every managementUnit with the munits above that have the same name
                        (munit.module as? Staircase)?.let { staircase ->
                            val staircaseIds = findStaircasesForBuilding(b - 1, staircase.staircaseName)
                            val currentStaircaseId = createNodeId(b, f, w, m)
                            for (staircaseId in staircaseIds) {
                                val edge = weightedGraph.addEdge(currentStaircaseId, staircaseId)
                                weightedGraph.setEdgeWeight(edge, buildingToBuildingEdgeWeight.toDouble())
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the IDs of all Staircases for a given building
     */
    private fun findStaircasesForBuilding(b: Int, name: String): List<String> {
        val building = buildings[b]
        val floors = building.floors

        return floors.indices.flatMap { f ->
            val walks = floors[f].walks
            findStaircasesWithName(name, walks).map { (w, m) ->
                createNodeId(b, f, w, m)
            }
        }
    }

    /**
     * Returns a value of distance between a locker and a class room.
     */
    private fun getDistance(
        locker: EntityCoordinates<Locker>,
        classRoomNodeId: String
    ) = DijkstraShortestPath(weightedGraph, locker.entity.id, classRoomNodeId).path?.weight?.toInt() ?: -1

    private fun createNodeId(b: Int, f: Int, w: Int, m: Int) = "$b-$f-$w-$m"

    /**
     * Compares the Y value of a pair containing an EntityCoordintes object
     * and distance as integer
     */
    private class EntityDistanceComparator(val reversed: Boolean = false) :
        Comparator<Pair<EntityCoordinates<Locker>, Int>> {

        override fun compare(p1: Pair<EntityCoordinates<Locker>, Int>, p2: Pair<EntityCoordinates<Locker>, Int>): Int {
            val dist1 = p1.second
            val dist2 = p2.second

            if (dist1 == dist2) {
                return 0
            }
            return if (dist1 > dist2) {
                if (reversed) {
                    -1
                } else {
                    +1
                }
            } else {
                if (reversed) {
                    +1
                } else {
                    -1
                }
            }
        }
    }

    enum class Status {
        ClassRoomForSpecifiedClassDoesNotExist,
        ClassRoomIsNotReachable,
        NoFreeLockersAvailable, // nothing to optimize?
        ClassHasNoPupils, // no lockers with pupils of class exist
        NonReachableLockersExist,
        NoMinimumSizeDefinedForRow,
        Success,
    }

    companion object {
        private const val buildingToBuildingEdgeWeight = 100.0f
        private const val walkToWalkEdgeWeight = 5.0f
        private const val floorToFloorEdgeWeight = 20.0f
        private const val managementUnitToMUEdgeWeight = 2.0f
        private const val managementUnitToLockerEdgeWeight = 1.0f
    }
}