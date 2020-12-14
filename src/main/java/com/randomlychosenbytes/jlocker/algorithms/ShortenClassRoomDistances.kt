package com.randomlychosenbytes.jlocker.algorithms

import com.randomlychosenbytes.jlocker.abstractreps.EntityCoordinates
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit
import com.randomlychosenbytes.jlocker.nonabstractreps.Building
import com.randomlychosenbytes.jlocker.nonabstractreps.Locker
import com.randomlychosenbytes.jlocker.nonabstractreps.Pair
import org.jgrapht.alg.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import java.text.DecimalFormat
import java.util.*

class ShortenClassRoomDistances(
    private val buildings: List<Building>,
    private val lockerMinSizes: List<Int>,
    private val classRoomNodeId: String,
    private val className: String,
    private val createTask: (String) -> Unit,
    private val moveLocker: (Locker, Locker, Boolean) -> Unit
) {
    //
    // Calibration for the algorithm: edge weights
    //
    private val weightedGraph: SimpleWeightedGraph<String?, DefaultWeightedEdge> =
        SimpleWeightedGraph(DefaultWeightedEdge::class.java)

    private val allLockersEntityCoordinatesList: MutableList<EntityCoordinates<Locker>> = mutableListOf()
    private val freeLockersEntityCoordinatesList: MutableList<EntityCoordinates<Locker>> = mutableListOf()
    private val classLockersEntityCoordinatesList: MutableList<EntityCoordinates<Locker>> = mutableListOf()
    private val classLockerToDistancePairList: MutableList<Pair<EntityCoordinates<Locker>, Int>> = mutableListOf()
    private val freeLockerToDistancePairList: MutableList<Pair<EntityCoordinates<Locker>, Int>> = mutableListOf()
    private var unreachableLockers: MutableList<String> = mutableListOf()

    fun check(): Int {

        // creates a weighted graph
        connectManagementUnitsAndLockers()
        connectWalksOnFloor()
        connectFloorsByStaircases()
        connectBuildinsByStaircases()

        // create a list of all free lockers
        // and all the lockers that belong to people of that class
        for (lockerEntityCoordinates in allLockersEntityCoordinatesList) {
            if (lockerEntityCoordinates.entity.isFree) {
                freeLockersEntityCoordinatesList.add(lockerEntityCoordinates)
            } else if (lockerEntityCoordinates.entity.schoolClassName == className) {
                classLockersEntityCoordinatesList.add(lockerEntityCoordinates)
            }
        }

        if (freeLockersEntityCoordinatesList.isEmpty()) {
            return NO_EMPTY_LOCKERS_AVAILABLE
        }

        if (classLockersEntityCoordinatesList.isEmpty()) {
            return CLASS_HAS_NO_PUPILS
        }

        //
        // Sort free lockers, beginning with the one that is the closest one
        //
        unreachableLockers = mutableListOf()
        for (freeLockerECoord in freeLockersEntityCoordinatesList) {
            val dist = getDistance(freeLockerECoord, classRoomNodeId)
            if (dist != -1) {
                freeLockerToDistancePairList.add(Pair(freeLockerECoord, dist))
            } else {
                // Create list that contains the ids of all lockers that cant 
                // be reached from the classroom
                unreachableLockers.add(freeLockerECoord.entity.id)
            }
        }
        freeLockerToDistancePairList.sortWith(EntityDistanceComparator())

        //
        // Sort class lockers, beginning with the one that is the farthest 
        // distance to the class room
        //
        for (classLockerECoord in classLockersEntityCoordinatesList) {
            val distance = getDistance(classLockerECoord, classRoomNodeId)
            if (distance != -1) {
                classLockerToDistancePairList.add(Pair(classLockerECoord, distance))
            } else {
                // Create a list that contains the ids of all lockers that can't
                // be reached from the classroom
                unreachableLockers.add(classLockerECoord.entity.id)
            }
        }
        classLockerToDistancePairList.sortWith(EntityDistanceComparator())
        classLockerToDistancePairList.reverse()

        // Output how many lockers cant be reached
        return if (unreachableLockers.isEmpty()) {
            SUCCESS
        } else {
            NON_REACHABLE_LOCKERS_EXIST
        }
    }

    /**
     * Does the actual moving based on the data gathered before.
     * and returns a list of the moving operations.
     */
    fun execute(): String {
        val statusMessage = StringBuilder()

        statusMessage.append("Es gibt ").append(classLockerToDistancePairList.size).append(" Schließfächer der Klasse ")
            .append(className).append("\n")
        statusMessage.append("Es wurden ").append(freeLockerToDistancePairList.size)
            .append(" freie Schließfächer gefunden!\n\n")
        statusMessage.append("Es wurden ").append(lockerMinSizes.size).append(" Minmalgrößen (cm) angelegt!\n")
        for (size in lockerMinSizes) {
            statusMessage.append(size).append(" ")
        }
        statusMessage.append("\n\n")
        for (classLockerToDistancePair in classLockerToDistancePairList) {
            // search until you find a free locker that is nearer and suits the pupils size
            for (freeLockerIndex in freeLockerToDistancePairList.indices) {
                val freeLockerToDistancePair = freeLockerToDistancePairList[freeLockerIndex]

                // Is distance of the new locker shorter to the classroom?
                if (classLockerToDistancePair.y > freeLockerToDistancePair.y) {
                    val srcLocker = classLockerToDistancePair.x.entity
                    val destLocker = freeLockerToDistancePair.x.entity

                    // determine minimum size for this locker
                    val index = freeLockerToDistancePair.x.lValue

                    // if no minimum size exists for this locker row, don't move
                    if (index < lockerMinSizes.size) {
                        // TODO lowest locker has coordinate 4, highest coordinate 0...
                        // this doesn't make sense
                        val lockerMinSize = lockerMinSizes[Math.abs(index - (lockerMinSizes.size - 1))]
                        if (srcLocker.heightInCm >= lockerMinSize) {
                            statusMessage.append(srcLocker.id).append(" -> ").append(destLocker.id).append("\n")
                            statusMessage.append("Besitzergröße: ")
                                .append(classLockerToDistancePair.x.entity.heightInCm).append(" cm\n")
                            statusMessage.append("Minimalgröße: ").append(lockerMinSize).append("\n")
                            val distanceReduction = (1.0f - freeLockerToDistancePair.y / classLockerToDistancePair.y
                                .toFloat()) * 100
                            val df = DecimalFormat("##.#")
                            statusMessage.append("Entfernung verkürzt um: ")
                                .append(df.format(distanceReduction.toDouble())).append("%\n\n")
                            moveLocker(srcLocker, destLocker, false)
                            freeLockerToDistancePairList.removeAt(freeLockerIndex) // this one is now occupied, so remove it
                            val taskText = ("Klassenumzug" + " ("
                                    + destLocker.schoolClassName + "): "
                                    + srcLocker.id + " -> "
                                    + destLocker.id +
                                    " Inhaber(in) "
                                    + destLocker.firstName
                                    + " " + destLocker.lastName)

                            createTask(taskText)
                            break
                        }
                    }
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
    private fun connectManagementUnitsAndLockers() {
        for (b in buildings.indices) {
            val floors = buildings[b].floors
            for (f in floors.indices) {
                val walks = floors[f].walks
                for (w in walks.indices) {
                    val managementUnits = walks[w].managementUnits
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
                        if (munit.getType() == ManagementUnit.LOCKER_CABINET) {
                            val lockers = managementUnits[m].lockerCabinet.lockers

                            // connect each locker with its ManagementUnit
                            for (l in lockers.indices) {
                                val locker = lockers[l]
                                weightedGraph.addVertex(locker.id)

                                // Connect lockers with their ManagmentUnit
                                val edge = weightedGraph.addEdge(currentMUnitID, locker.id)
                                weightedGraph.setEdgeWeight(edge, managementUnitToLockerEdgeWeight.toDouble())

                                // fill locker in the list containing all
                                // locker coordinates
                                allLockersEntityCoordinatesList.add(EntityCoordinates(locker, b, f, w, m, l))
                            }
                        }
                    }
                }
            }
        }
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
                    val lastMUnitIndex = walks[w - 1].managementUnits.size - 1
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
                    val managementUnits = walks[w].managementUnits
                    for (m in managementUnits.indices) {
                        val munit = managementUnits[m]

                        // connect every managementUnit with the munits above that have the same name
                        if (munit.getType() == ManagementUnit.STAIRCASE) {
                            val currentMUnitID = createNodeId(b, f, w, m)
                            val ids = findStaircasesOnFloor(b, f + 1, munit.staircase.staircaseName)
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

    /**
     * Returns the IDs of all ManagementUnits on the given floor with the given name
     */
    private fun findStaircasesOnFloor(b: Int, f: Int, name: String): List<String> {
        val floors = buildings[b].floors
        val entityIds: MutableList<String> = mutableListOf()

        // does the floor exist?
        if (floors.size > f) {
            val walks = floors[f].walks
            for (w in walks.indices) {
                val managementUnits = walks[w].managementUnits
                for (m in managementUnits.indices) {
                    val managementUnit = managementUnits[m]
                    if (managementUnit.getType() == ManagementUnit.STAIRCASE) {
                        if (managementUnit.staircase.staircaseName == name) {
                            entityIds.add(createNodeId(b, f, w, m))
                        }
                    }
                }
            }
        }
        return entityIds
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
                    val managementUnits = walks[w].managementUnits
                    for (m in managementUnits.indices) {
                        val munit = managementUnits[m]

                        // connect every managementUnit with the munits above that have the same name
                        if (munit.getType() == ManagementUnit.STAIRCASE) {
                            val staircaseName = munit.staircase.staircaseName
                            val staircaseIds = findStaircasesForBuilding(b - 1, staircaseName)
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
        val entityIds: MutableList<String> = mutableListOf()
        val floors = building.floors
        for (f in floors.indices) {
            val walks = floors[f].walks
            for (w in walks.indices) {
                val managementUnits = walks[w].managementUnits
                for (m in managementUnits.indices) {
                    val managementUnit = managementUnits[m]
                    if (managementUnit.getType() == ManagementUnit.STAIRCASE) {
                        if (managementUnit.staircase.staircaseName == name) {
                            entityIds.add(createNodeId(b, f, w, m))
                        }
                    }
                }
            }
        }
        return entityIds
    }

    /**
     * Returns a value of distance between a locker and a class room.
     */
    private fun getDistance(locker: EntityCoordinates<Locker>, classRoomNodeId: String): Int {
        val lockerID = locker.entity.id
        val shortest = DijkstraShortestPath(weightedGraph, lockerID, classRoomNodeId)
        val path = shortest.path
        return path?.weight?.toInt() ?: -1
        // not reachable 
    }

    private fun createNodeId(b: Int, f: Int, w: Int, m: Int) = "$b-$f-$w-$m"

    /**
     * Compares the Y value of a pair containing an EntityCoordintes object
     * and distance as integer
     */
    private class EntityDistanceComparator : Comparator<Pair<EntityCoordinates<Locker>, Int>> {
        override fun compare(p1: Pair<EntityCoordinates<Locker>, Int>, p2: Pair<EntityCoordinates<Locker>, Int>): Int {
            val dist1 = p1.y as Int
            val dist2 = p2.y as Int
            if (dist1 == dist2) {
                return 0
            }
            return if (dist1 > dist2) +1 else -1
        }
    }

    companion object {
        private const val buildingToBuildingEdgeWeight = 100.0f
        private const val walkToWalkEdgeWeight = 5.0f
        private const val floorToFloorEdgeWeight = 20.0f
        private const val managementUnitToMUEdgeWeight = 2.0f
        private const val managementUnitToLockerEdgeWeight = 1.0f

        //
        // Status codes
        //
        const val CLASS_HAS_NO_ROOM = 0
        const val NO_EMPTY_LOCKERS_AVAILABLE = 1
        const val CLASS_HAS_NO_PUPILS = 2
        const val NON_REACHABLE_LOCKERS_EXIST = 3
        const val NO_MINIMUM_SIZE_DEFINED_FOR_ROW = 4
        const val SUCCESS = 5
    }
}