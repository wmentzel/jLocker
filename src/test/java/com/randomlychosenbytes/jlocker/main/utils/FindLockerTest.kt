package com.randomlychosenbytes.jlocker.main.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.randomlychosenbytes.jlocker.EntityCoordinates
import com.randomlychosenbytes.jlocker.main.createModuleWrapperWithLockerCabinetOf
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

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(Floor("ground floor").apply {
                    walks.addAll(listOf(Walk("main walk").apply {
                        moduleWrappers.addAll(
                            listOf(
                                createModuleWrapperWithLockerCabinetOf(locker1, locker2)
                            )
                        )
                    }))
                }))
            })

        assertThat(
            buildings.getAllLockerCoordinates().findLockers(
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
            buildings.getAllLockerCoordinates().findLockers(
                firstName = "Peggy",
            ).toList()
        ).isEqualTo(
            listOf(EntityCoordinates(locker2, 0, 0, 0, 0, 1))
        )
    }
}