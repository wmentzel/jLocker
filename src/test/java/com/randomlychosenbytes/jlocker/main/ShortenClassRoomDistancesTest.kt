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
        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(Floor("ground floor").apply {
                    walks.addAll(listOf(Walk("main walk").apply {
                        moduleWrappers.addAll(
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
                    }))
                }))
            })

        val scd = ShortenClassRoomDistances(
            buildings = buildings,
            lockerMinSizes = listOf(0, 0, 140, 150, 175),
            className = "12",
            createTask = { /* no-op */ }
        )

        val status = scd.check()
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.ClassRoomForSpecifiedClassDoesNotExist)
    }

    @Test
    fun shouldReportIfNonReachableLockersExist() {

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(Floor("ground floor").apply {
                    walks.addAll(listOf(Walk("main walk").apply {
                        moduleWrappers.addAll(listOf(createModuleWrapperWithLockerCabinetOf(
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
                        ))
                    }))
                },
                    Floor("first floor").apply {
                        walks.addAll(listOf(Walk("main walk").apply {
                            moduleWrappers.addAll(
                                listOf(
                                    createModuleWrapperWithLockerCabinetOf(
                                        Locker(id = "2") // unreachable locker
                                    ),
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
        assertThat(status).isEqualTo(ShortenClassRoomDistances.Status.NonReachableLockersExist)
    }

    @Test
    fun shouldReportIfNoLockersAreFree() {

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(
                    listOf(
                        Floor("ground floor").apply {
                            walks.addAll(listOf(Walk("main walk").apply {
                                moduleWrappers.addAll(
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
                            }))
                        }
                    )
                )
            })

        val scd = ShortenClassRoomDistances(
            buildings = buildings,
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

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(
                    listOf(
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
                                ))
                            }))
                        }
                    )
                )
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

        // should be moved to highest locker
        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[2].lockerCabinet.lockers[0].isFree).isFalse()
        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[2].lockerCabinet.lockers[0].pupil.lastName).isEqualTo(
            "Draper"
        )
        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[0].lockerCabinet.lockers[0].isFree).isTrue()

        // should be moved to second highest locker
        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[2].lockerCabinet.lockers[1].isFree).isFalse()
        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[2].lockerCabinet.lockers[1].pupil.lastName).isEqualTo(
            "Olsen"
        )
        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[0].lockerCabinet.lockers[0].isFree).isTrue()

        // should not have been moved
        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[0].lockerCabinet.lockers[2].isFree).isFalse()
        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[0].lockerCabinet.lockers[2].pupil.lastName).isEqualTo(
            "Campbell"
        )

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

        assertThat(buildings[0].floors[0].walks[0].moduleWrappers[0].lockerCabinet.lockers[0].isFree).isTrue()
        assertThat(buildings[0].floors[3].walks[0].moduleWrappers[0].lockerCabinet.lockers[0].isFree).isFalse()
        assertThat(buildings[0].floors[3].walks[0].moduleWrappers[0].lockerCabinet.lockers[0].pupil.firstName).isEqualTo(
            "Don"
        )
        assertThat(buildings[0].floors[3].walks[0].moduleWrappers[0].lockerCabinet.lockers[0].pupil.lastName).isEqualTo(
            "Draper"
        )
    }
}