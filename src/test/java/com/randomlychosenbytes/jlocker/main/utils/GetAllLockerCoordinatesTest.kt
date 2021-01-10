package com.randomlychosenbytes.jlocker.main.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.randomlychosenbytes.jlocker.EntityCoordinates
import com.randomlychosenbytes.jlocker.main.createModuleWrapperWithLockerCabinetOf
import com.randomlychosenbytes.jlocker.model.Building
import com.randomlychosenbytes.jlocker.model.Floor
import com.randomlychosenbytes.jlocker.model.Locker
import com.randomlychosenbytes.jlocker.model.Walk
import com.randomlychosenbytes.jlocker.utils.getAllLockerCoordinates
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetAllLockerCoordinatesTest {
    @BeforeEach
    fun setup() {
    }

    @Test
    fun `should map correct coordinates to lockers`() {

        val locker1 = Locker(id = "1")
        val locker2 = Locker(id = "2")
        val locker3 = Locker(id = "3")

        val locker4 = Locker(id = "4")
        val locker5 = Locker(id = "5")

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(
                    Floor("ground floor").apply {
                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(
                                listOf(
                                    createModuleWrapperWithLockerCabinetOf(locker1, locker2, locker3)
                                )
                            )
                        }))
                    },
                    Floor("1st floor").apply {
                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(
                                listOf(createModuleWrapperWithLockerCabinetOf(locker4, locker5))
                            )
                        }))
                    }
                ))
            })

        val lockerCoordinates = buildings.getAllLockerCoordinates()

        assertThat(lockerCoordinates.toList()).isEqualTo(
            listOf(
                EntityCoordinates(locker1, 0, 0, 0, 0, 0),
                EntityCoordinates(locker2, 0, 0, 0, 0, 1),
                EntityCoordinates(locker3, 0, 0, 0, 0, 2),

                EntityCoordinates(locker4, 0, 1, 0, 0, 0),
                EntityCoordinates(locker5, 0, 1, 0, 0, 1),
            )
        )
    }
}