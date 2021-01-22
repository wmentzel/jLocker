package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose

class LockerCabinet : Module() {
    @Expose
    var lockers: MutableList<Locker> = mutableListOf()
}