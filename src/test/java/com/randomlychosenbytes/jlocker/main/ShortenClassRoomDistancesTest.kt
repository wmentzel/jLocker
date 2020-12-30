package com.randomlychosenbytes.jlocker.main

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.randomlychosenbytes.jlocker.abstractreps.ModuleWrapper
import com.randomlychosenbytes.jlocker.algorithms.ShortenClassRoomDistances
import com.randomlychosenbytes.jlocker.manager.DataManager.moveLockers
import com.randomlychosenbytes.jlocker.nonabstractreps.*
import org.junit.jupiter.api.Test

class ShortenClassRoomDistancesTest {

    @Test
    fun shouldMovePupilToCorrectFreeLocker() {
        val building = Building("main building")
        val floor = Floor("ground floor")
        building.floors += floor
        val walk = Walk("main walk")
        floor.walks.add(walk)
        walk.moduleWrappers.addAll(
            listOf(
                ModuleWrapper(LockerCabinet()),
                ModuleWrapper(LockerCabinet()),
                ModuleWrapper(LockerCabinet()),
                ModuleWrapper(Room()),
                ModuleWrapper(Staircase())
            )
        )

        walk.moduleWrappers[0].lockerCabinet.lockers.addAll(
            listOf(
                Locker(id = "1").apply {
                    moveInNewOwner(Pupil().apply {
                        firstName = "Don"
                        lastName = "Draper"
                        heightInCm = 150
                        schoolClassName = "12"
                    })
                },
                Locker(id = "2"),
                Locker(id = "3")
            ),
        )

        walk.moduleWrappers[1].lockerCabinet.lockers.addAll(
            listOf(
                Locker(id = "4"),
                Locker(id = "5"),
                Locker(id = "6")
            ),
        )

        walk.moduleWrappers[2].lockerCabinet.lockers.addAll(
            listOf(
                Locker(id = "7"),
                Locker(id = "8"),
                Locker(id = "9")
            ),
        )

        walk.moduleWrappers[3].room.apply {
            setCaption("Some Classroom", "12")
        }

        walk.moduleWrappers[4].staircase.apply {
            setCaption("MainStaircase")
        }

        val scd = ShortenClassRoomDistances(
            buildings = listOf(building),
            lockerMinSizes = listOf(0, 0, 140, 150, 175),
            classRoomNodeId = "0-0-0-3",
            className = "12",
            createTask = { /* no-op */ },
            moveLocker = { l1, l2 ->
                moveLockers(l1, l2)
            }
        )

        val status = scd.check()
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.Success)
        scd.execute()

        assertThat(walk.moduleWrappers[2].lockerCabinet.lockers[1].pupil).isNotNull()
        assertThat(walk.moduleWrappers[0].lockerCabinet.lockers[0].isFree).isTrue()
    }

    private val ModuleWrapper.lockerCabinet get() = module as LockerCabinet
    private val ModuleWrapper.room get() = module as Room
    private val ModuleWrapper.staircase get() = module as Staircase
}