package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit
import com.randomlychosenbytes.jlocker.manager.DataManager

class Walk(name: String) : Entity(name) {
    @Expose
    var managementUnits: List<ManagementUnit> = mutableListOf()

    fun setCurLockerIndex(locker: Locker) {

        for (m in managementUnits.indices) {
            val lockers = managementUnits[m].lockerList
            val l = lockers.indexOf(locker)
            if (l != -1) {
                val dm = DataManager.getInstance()
                dm.setCurrentLockerIndex(l)
                dm.setCurrentMUnitIndex(m)
            }
        }
    }
}