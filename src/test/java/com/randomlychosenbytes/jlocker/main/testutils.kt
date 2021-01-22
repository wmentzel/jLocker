package com.randomlychosenbytes.jlocker.main

import com.randomlychosenbytes.jlocker.model.*

fun createModuleWrapperWithLockerCabinetOf(vararg lockers: Locker) = ModuleWrapper(LockerCabinet().apply {
    this.lockers.addAll(lockers)
})

val ModuleWrapper.lockerCabinet
    get() = module as LockerCabinet

val ModuleWrapper.room
    get() = module as Room

val ModuleWrapper.staircase
    get() = module as Staircase