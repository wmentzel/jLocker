package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose

class Settings {
    @Expose
    var lockerOverviewFontSize = 20

    @Expose
    var numOfBackups = 10

    @Expose
    var lockerMinSizes = mutableListOf(0, 0, 140, 150, 175)
}