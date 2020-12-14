package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose

class Floor(name: String) : Entity(name) {
    @Expose
    var walks: List<Walk> = mutableListOf()
}