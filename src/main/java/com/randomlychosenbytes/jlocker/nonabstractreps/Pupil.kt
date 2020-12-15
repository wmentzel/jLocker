package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.manager.getCalendarFromString
import com.randomlychosenbytes.jlocker.manager.getDifferenceInMonths
import java.util.*

class Pupil(
    @Expose var firstName: String,
    @Expose var lastName: String,
    @Expose var heightInCm: Int,
    @Expose var schoolClassName: String, // TODO: use LocalDate
    @Expose var rentedFromDate: String, // TODO: use LocalDate
    @Expose var rentedUntilDate: String,
    @Expose var hasContract: Boolean,
    @Expose var paidAmount: Int,
    @Expose var previouslyPaidAmount: Int,
) {
    constructor(firstName: String, lastName: String) : this(
        firstName = firstName,
        lastName = lastName,
        heightInCm = 0,
        schoolClassName = "",
        rentedFromDate = "",
        rentedUntilDate = "",
        hasContract = false,
        paidAmount = 0,
        previouslyPaidAmount = 0
    )

    val remainingTimeInMonths: Long
        get() {
            if (rentedUntilDate == "" || rentedFromDate == "") {
                return 0
            }

            val today: Calendar = GregorianCalendar().apply {
                isLenient = false
                time
            }

            val end = getCalendarFromString(rentedUntilDate)
            return getDifferenceInMonths(today, end!!)
        }
}