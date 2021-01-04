package com.randomlychosenbytes.jlocker.main

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.randomlychosenbytes.jlocker.ShortenClassRoomDistances
import com.randomlychosenbytes.jlocker.model.*
import org.junit.jupiter.api.Test

class ShortenClassRoomDistancesTest {

    @Test
    fun shouldReportIfClassRoomDoesNotExist() {
        val building = Building("main building")
        val groundFloor = Floor("ground floor")

        building.floors.addAll(listOf(groundFloor))
        val walk = Walk("main walk")
        groundFloor.walks.add(walk)

        walk.moduleWrappers.addAll(
            listOf(

                createModuleWrapperWithLockerCabinetOf(
                    Locker(id = "1").apply {
                        moveInNewOwner(Pupil().apply {
                            firstName = "Don"
                            lastName = "Draper"
                            heightInCm = 175
                            schoolClassName = "12"
                        })
                    },
                    Locker(id = "2")
                )
            )
        )

        val scd = ShortenClassRoomDistances(
            buildings = listOf(building),
            lockerMinSizes = listOf(0, 0, 140, 150, 175),
            className = "12",
            createTask = { /* no-op */ }
        )

        val status = scd.check()
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.ClassRoomForSpecifiedClassDoesNotExist)
    }

    @Test
    fun shouldReportIfNonReachableLockersExist() {
        val building = Building("main building")
        val groundFloor = Floor("ground floor")
        val firstFloor = Floor("first floor")

        building.floors.addAll(listOf(groundFloor, firstFloor))
        val walk1 = Walk("main walk")
        groundFloor.walks.add(walk1)

        val walk2 = Walk("main walk")
        firstFloor.walks.add(walk2)

        walk1.moduleWrappers.addAll(
            listOf(
                createModuleWrapperWithLockerCabinetOf(
                    Locker(id = "1").apply {
                        moveInNewOwner(Pupil().apply {
                            firstName = "Don"
                            lastName = "Draper"
                            heightInCm = 175
                            schoolClassName = "12"
                        })
                    }
                ),
                ModuleWrapper(Room("", "12")),
                ModuleWrapper(Staircase("main staircase"))
            )
        )

        walk2.moduleWrappers.addAll(
            listOf(

                createModuleWrapperWithLockerCabinetOf(
                    Locker(id = "2") // unreachable locker
                ),
            )
        )

        val scd = ShortenClassRoomDistances(
            buildings = listOf(building),
            lockerMinSizes = listOf(0, 0, 140, 150, 175),
            className = "12",
            createTask = { /* no-op */ }
        )

        val status = scd.check()
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.NonReachableLockersExist)
    }

    @Test
    fun shouldReportIfNoLockersAreFree() {
        val building = Building("main building")
        val groundFloor = Floor("ground floor")
        val firstFloor = Floor("first floor")

        building.floors.addAll(listOf(groundFloor, firstFloor))
        val walk = Walk("main walk")
        groundFloor.walks.add(walk)


        walk.moduleWrappers.addAll(
            listOf(
                createModuleWrapperWithLockerCabinetOf(
                    Locker(id = "1").apply {
                        moveInNewOwner(Pupil().apply {
                            firstName = "Don"
                            lastName = "Draper"
                            heightInCm = 175
                            schoolClassName = "12"
                        })
                    },
                    Locker(id = "2").apply {
                        moveInNewOwner(Pupil().apply {
                            firstName = "Peggy"
                            lastName = "Olsen"
                            heightInCm = 150
                            schoolClassName = "12"
                        })
                    },
                ),
                ModuleWrapper(Room("", "12"))
            )
        )

        val scd = ShortenClassRoomDistances(
            buildings = listOf(building),
            lockerMinSizes = listOf(0, 0, 140, 150, 175),
            className = "12",
            createTask = { /* no-op */ }
        )

        val status = scd.check()
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.NoFreeLockersAvailable)
    }

    @Test
    fun shouldMovePupilsAcrossWalks() {
    }

    @Test
    fun shouldMovePupilsAcrossFloors() {
    }

    @Test
    fun shouldMovePupilsAcrossBuildings() {
    }


    @Test
    fun shouldMovePupilToCorrectFreeLocker() {
        val building = Building("main building")
        val floor = Floor("ground floor")
        building.floors += floor
        val walk = Walk("main walk")
        floor.walks.add(walk)

        walk.moduleWrappers.addAll(
            listOf(
                createModuleWrapperWithLockerCabinetOf(
                    Locker(id = "1").apply {
                        moveInNewOwner(Pupil().apply {
                            firstName = "Don"
                            lastName = "Draper"
                            heightInCm = 175
                            schoolClassName = "12"
                        })
                    },
                    Locker(id = "2").apply {
                        moveInNewOwner(Pupil().apply {
                            firstName = "Peggy"
                            lastName = "Olsen"
                            heightInCm = 150
                            schoolClassName = "12"
                        })
                    },
                    Locker(id = "3").apply {
                        moveInNewOwner(Pupil().apply {
                            firstName = "Peter"
                            lastName = "Campbell"
                            heightInCm = 150
                            schoolClassName = "11"
                        })
                    }
                ),
                createModuleWrapperWithLockerCabinetOf(
                    Locker(id = "4"),
                    Locker(id = "5"),
                    Locker(id = "6")
                ),

                createModuleWrapperWithLockerCabinetOf(
                    Locker(id = "7"),
                    Locker(id = "8"),
                    Locker(id = "9")
                ),
                ModuleWrapper(Room("some classroom", "12")),
                ModuleWrapper(Staircase("main staircase"))
            )
        )

        val scd = ShortenClassRoomDistances(
            buildings = listOf(building),
            lockerMinSizes = listOf(0, 0, 140, 150, 175),
            className = "12",
            createTask = { /* no-op */ }
        )

        val status = scd.check()
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.Success)
        scd.execute()

        // should be moved to highest locker
        assertThat(walk.moduleWrappers[2].lockerCabinet.lockers[0].isFree).isFalse()
        assertThat(walk.moduleWrappers[2].lockerCabinet.lockers[0].pupil.lastName).isEqualTo("Draper")
        assertThat(walk.moduleWrappers[0].lockerCabinet.lockers[1].isFree).isTrue()

        // should be moved to second highest locker
        assertThat(walk.moduleWrappers[2].lockerCabinet.lockers[1].isFree).isFalse()
        assertThat(walk.moduleWrappers[2].lockerCabinet.lockers[1].pupil.lastName).isEqualTo("Olsen")
        assertThat(walk.moduleWrappers[0].lockerCabinet.lockers[0].isFree).isTrue()

        // should not have been moved
        assertThat(walk.moduleWrappers[0].lockerCabinet.lockers[2].isFree).isFalse()
        assertThat(walk.moduleWrappers[0].lockerCabinet.lockers[2].pupil.lastName).isEqualTo("Campbell")

    }

    @Test
    fun `should optimize class room distances across different buildings`() {

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(
                    Floor("ground floor").apply {
                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(listOf(
                                createModuleWrapperWithLockerCabinetOf(
                                    Locker(id = "1").apply {
                                        moveInNewOwner(Pupil().apply {
                                            firstName = "Don"
                                            lastName = "Draper"
                                            heightInCm = 175
                                            schoolClassName = "12"
                                        })
                                    }
                                ),
                                ModuleWrapper(Staircase("entry"))
                            ))
                        }))
                    }
                ))
            },
            Building("second building").apply {
                floors.addAll(listOf(
                    Floor("ground floor").apply {
                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(
                                listOf(
                                    createModuleWrapperWithLockerCabinetOf(
                                        Locker(id = "2")
                                    ),
                                    ModuleWrapper(Room("some classroom", "12")),
                                    ModuleWrapper(Staircase("entry"))
                                )
                            )
                        }))
                    }
                ))
            })

        val scd = ShortenClassRoomDistances(
            buildings = buildings,
            lockerMinSizes = listOf(0, 0, 140, 150, 175),
            className = "12",
            createTask = { /* no-op */ }
        )

        val status = scd.check()
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.Success)
        scd.execute()

        assertThat((buildings[0].floors[0].walks[0].moduleWrappers[0].module as LockerCabinet).lockers[0].isFree).isTrue()
        assertThat((buildings[1].floors[0].walks[0].moduleWrappers[0].module as LockerCabinet).lockers[0].isFree).isFalse()
        assertThat((buildings[1].floors[0].walks[0].moduleWrappers[0].module as LockerCabinet).lockers[0].pupil.firstName).isEqualTo(
            "Don"
        )
        assertThat((buildings[1].floors[0].walks[0].moduleWrappers[0].module as LockerCabinet).lockers[0].pupil.lastName).isEqualTo(
            "Draper"
        )
    }

    @Test
    fun `should optimize class room distances across floors`() {

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(
                    Floor("ground floor").apply {
                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(listOf(
                                createModuleWrapperWithLockerCabinetOf(
                                    Locker(id = "1").apply {
                                        moveInNewOwner(Pupil().apply {
                                            firstName = "Don"
                                            lastName = "Draper"
                                            heightInCm = 175
                                            schoolClassName = "12"
                                        })
                                    }
                                ),
                                ModuleWrapper(Staircase("main staircase"))
                            ))
                        }))
                    },
                    Floor("1st floor").apply {

                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(
                                listOf(
                                    ModuleWrapper(Staircase("main staircase"))
                                )
                            )
                        }))
                    },
                    Floor("2nd floor").apply {
                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(
                                listOf(
                                    ModuleWrapper(Staircase("main staircase"))
                                )
                            )
                        }))
                    },
                    Floor("3rd floor").apply {
                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(
                                listOf(
                                    createModuleWrapperWithLockerCabinetOf(
                                        Locker(id = "2")
                                    ),
                                    ModuleWrapper(Room("some classroom", "12")),
                                    ModuleWrapper(Staircase("main staircase"))
                                )
                            )
                        }))
                    }
                ))
            })


        val scd = ShortenClassRoomDistances(
            buildings = buildings,
            lockerMinSizes = listOf(0, 0, 140, 150, 175),
            className = "12",
            createTask = { /* no-op */ }
        )

        val status = scd.check()
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.Success)
        scd.execute()

        assertThat((buildings[0].floors[0].walks[0].moduleWrappers[0].module as LockerCabinet).lockers[0].isFree).isTrue()
        assertThat((buildings[0].floors[3].walks[0].moduleWrappers[0].module as LockerCabinet).lockers[0].isFree).isFalse()
        assertThat((buildings[0].floors[3].walks[0].moduleWrappers[0].module as LockerCabinet).lockers[0].pupil.firstName).isEqualTo(
            "Don"
        )
        assertThat((buildings[0].floors[3].walks[0].moduleWrappers[0].module as LockerCabinet).lockers[0].pupil.lastName).isEqualTo(
            "Draper"
        )
    }

    private val ModuleWrapper.lockerCabinet get() = module as LockerCabinet
    private val ModuleWrapper.room get() = module as Room
    private val ModuleWrapper.staircase get() = module as Staircase
}