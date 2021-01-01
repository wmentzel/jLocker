package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import java.util.*

class Task(@Expose var description: String) {

    @Expose
    var isDone = false

    @Expose
    var creationDate: String // TODO: use LocalDate

    init {
        val today: Calendar = GregorianCalendar()

        creationDate = String.format(
            "%02d.%02d.%02d",
            today[Calendar.DATE],
            today[Calendar.MONTH] + 1,
            today[Calendar.YEAR]
        )
    }
}