package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.abstractreps.ModuleWrapper
import com.randomlychosenbytes.jlocker.manager.DataManager
import com.randomlychosenbytes.jlocker.manager.DataManager.currentLockerIndex
import com.randomlychosenbytes.jlocker.manager.DataManager.currentManagementUnitIndex

class Walk(name: String) : Entity(name) {
    @Expose
    var moduleWrappers: MutableList<ModuleWrapper> = mutableListOf()

    fun setCurLockerIndex(locker: Locker) {

        for (m in moduleWrappers.indices) {
            val lockers = moduleWrappers[m].lockerCabinet.lockers
            val l = lockers.indexOf(locker)
            if (l != -1) {
                DataManager.also {
                    currentLockerIndex = l
                    currentManagementUnitIndex = m
                }
            }
        }
    }
}