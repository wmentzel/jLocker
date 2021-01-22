package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose

class Floor(name: String) : Entity(name) {
    @Expose
    var walks: MutableList<Walk> = mutableListOf()
}