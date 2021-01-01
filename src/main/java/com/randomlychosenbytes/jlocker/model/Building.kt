package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose

class Building(
    name: String
) : Entity(name) {
    @Expose
    val floors: MutableList<Floor> = mutableListOf()
}