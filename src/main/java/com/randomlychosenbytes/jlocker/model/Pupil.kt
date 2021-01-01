package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

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
    constructor() : this(
        firstName = "",
        lastName = "",
        heightInCm = 0,
        schoolClassName = "",
        rentedFromDate = "",
        rentedUntilDate = "",
        hasContract = false,
        paidAmount = 0,
        previouslyPaidAmount = 0
    )

    val remainingTimeInMonths: Int
        get() {
            if (rentedUntilDate == "") {
                return 0
            }
            val end = LocalDate.parse(rentedUntilDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            return Period.between(LocalDate.now(), end).months
        }
}