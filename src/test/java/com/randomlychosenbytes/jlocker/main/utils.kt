package com.randomlychosenbytes.jlocker.main

import com.randomlychosenbytes.jlocker.model.Locker
import com.randomlychosenbytes.jlocker.model.LockerCabinet
import com.randomlychosenbytes.jlocker.model.ModuleWrapper

fun createModuleWrapperWithLockerCabinetOf(vararg lockers: Locker) = ModuleWrapper(LockerCabinet().apply {
    this.lockers.addAll(lockers)
})