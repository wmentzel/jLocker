package com.randomlychosenbytes.jlocker.main.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.randomlychosenbytes.jlocker.main.createModuleWrapperWithLockerCabinetOf
import com.randomlychosenbytes.jlocker.model.*
import com.randomlychosenbytes.jlocker.utils.renameSchoolClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RenameSchoolClassTest {

    @Test
    fun shouldRenameClass() {

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

        val numRenamed = buildings.renameSchoolClass("12", "13")

        assertThat(locker1.pupil.schoolClassName).isEqualTo("13")
        assertThat(locker2.pupil.schoolClassName).isEqualTo("13")
        assertThat(numRenamed).isEqualTo(2)
    }

    @Test
    fun shouldRenameClass2() {

        val locker1 = Locker(id = "1").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Don"
                lastName = "Draper"
                heightInCm = 175
                schoolClassName = "7.1"
            })
        }

        val locker2 = Locker(id = "2").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Peggy"
                lastName = "Olsen"
                heightInCm = 150
                schoolClassName = "7.2"
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

        val numRenamed = buildings.renameSchoolClass("7", "8")

        assertThat(locker1.pupil.schoolClassName).isEqualTo("8.1")
        assertThat(locker2.pupil.schoolClassName).isEqualTo("8.2")
        assertThat(numRenamed).isEqualTo(2)
    }
}