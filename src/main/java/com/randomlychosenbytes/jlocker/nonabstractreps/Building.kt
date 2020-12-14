package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose

class Building(
    name: String,
    @Expose
    var notes: String
) : Entity(name) {
    @Expose
    val floors: List<Floor> = mutableListOf()
}