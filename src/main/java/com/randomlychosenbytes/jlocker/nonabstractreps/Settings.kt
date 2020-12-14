package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose

class Settings {
    @Expose
    var lockerOverviewFontSize = 20

    @Expose
    var numOfBackups = 10

    @Expose
    var lockerMinSizes = listOf(0, 0, 140, 150, 175)
}