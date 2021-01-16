package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose

class Room(
    @Expose
    var name: String,

    @Expose
    var schoolClassName: String,
) : Module()