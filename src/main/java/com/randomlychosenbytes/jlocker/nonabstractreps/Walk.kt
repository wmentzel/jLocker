package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.abstractreps.ModuleWrapper

class Walk(name: String) : Entity(name) {
    @Expose
    var moduleWrappers: MutableList<ModuleWrapper> = mutableListOf()
}