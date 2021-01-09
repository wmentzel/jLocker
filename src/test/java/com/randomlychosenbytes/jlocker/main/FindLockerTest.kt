package com.randomlychosenbytes.jlocker.main

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.randomlychosenbytes.jlocker.EntityCoordinates
import com.randomlychosenbytes.jlocker.model.*
import com.randomlychosenbytes.jlocker.utils.findLockers
import com.randomlychosenbytes.jlocker.utils.getAllLockerCoordinates
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class FindLockerTest {

    @BeforeEach
    fun setup() {
    }

    @Test
    fun shouldFindLockersMatchingCriteria() {

        val building = Building("main building")
        val groundFloor = Floor("ground floor")
        val firstFloor = Floor("first floor")

        building.floors.addAll(listOf(groundFloor, firstFloor))
        val walk = Walk("main walk")
        groundFloor.walks.add(walk)

        val locker1 = Locker(id = "1").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Don"
                lastName = "Draper"
                heightInCm = 175
                schoolClassName = "12"
            })
        }

        val locker2 = Locker(id = "2").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Peggy"
                lastName = "Olsen"
                heightInCm = 150
                schoolClassName = "12"
            })
        }

        walk.moduleWrappers.addAll(
            listOf(
                createModuleWrapperWithLockerCabinetOf(locker1, locker2),
                ModuleWrapper(Room("", "12"))
            )
        )

        assertThat(
            listOf(building).getAllLockerCoordinates().findLockers(
                id = "1",
                firstName = "Don",
                lastName = "Draper",
                height = "175",
                schoolClass = "12"
            ).toList()
        ).isEqualTo(
            listOf(EntityCoordinates(locker1, 0, 0, 0, 0, 0))
        )

        assertThat(
            listOf(building).getAllLockerCoordinates().findLockers(
                firstName = "Peggy",
            ).toList()
        ).isEqualTo(
            listOf(EntityCoordinates(locker2, 0, 0, 0, 0, 1))
        )
    }
}