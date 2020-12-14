package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.manager.DataManager
import com.randomlychosenbytes.jlocker.manager.Utils
import java.awt.Color
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.crypto.SecretKey
import javax.swing.JLabel

class Locker(
    id: String,
    @Expose var firstName: String,
    @Expose var lastName: String,
    @Expose var heightInCm: Int,
    @Expose var schoolClassName: String, // TODO: use LocalDate
    @Expose var rentedFromDate: String, // TODO: use LocalDate
    @Expose var rentedUntilDate: String,
    @Expose var hasContract: Boolean,
    @Expose var paidAmount: Int,
    @Expose var previouslyPaidAmount: Int,
    @Expose var lockCode: String,
    @Expose var isOutOfOrder: Boolean,
    @Expose var note: String,
    @Expose var currentCodeIndex: Int,
    encryptedCodes: Array<String>?
) : JLabel(), Cloneable {

    @Expose
    var id: String = ""
        set(value) {
            text = value
            field = value
        }

    @Expose
    var encryptedCodes: Array<String>? = encryptedCodes
        private set

    // transient
    var isSelected = false
        private set

    fun getCodes(sukey: SecretKey): Array<String?> {
        val decCodes = arrayOfNulls<String>(5)
        for (i in 0..4) {
            decCodes[i] = getCode(i, sukey)
        }
        return decCodes
    }

    private fun getCode(i: Int, sukey: SecretKey): String {
        if (encryptedCodes == null) {
            return "00-00-00"
        }
        val code = Utils.decrypt(encryptedCodes!![i], sukey)
        return code.substring(0, 2) + "-" + code.substring(2, 4) + "-" + code.substring(4, 6)
    }

    fun setCodes(codes: Array<String>, superUserMasterKey: SecretKey?) {
        // codes is unencrypted... encrypting and saving in encCodes

        // The Value of code[i] looks like "00-00-00"
        // it is saved without the "-", so we have
        // to remove them.
        encryptedCodes = (0..4).map { i ->
            val code = codes[i].replace("-", "")
            Utils.encrypt(code, superUserMasterKey)
        }.toTypedArray()
    }

    val remainingTimeInMonths: Long
        get() {
            if (rentedUntilDate == "" || rentedFromDate == "" || isFree) {
                return 0
            }

            val today: Calendar = GregorianCalendar().apply {
                isLenient = false
                time
            }

            val end = Utils.getCalendarFromString(rentedUntilDate)
            return Utils.getDifferenceInMonths(today, end)
        }

    fun setAppropriateColor() {
        if (hasContract) {
            setColor(RENTED_COLOR)
        } else {
            setColor(NOCONTRACT_COLOR)
        }
        if (remainingTimeInMonths <= 1) {
            setColor(ONEMONTHREMAINING_COLOR)
        }
        if (isFree) {
            setColor(FREE_COLOR)
        }
        if (isOutOfOrder) {
            setColor(OUTOFORDER_COLOR)
        }
    }

    fun empty() {
        lastName = ""
        firstName = ""
        heightInCm = 0
        schoolClassName = ""
        rentedFromDate = ""
        rentedUntilDate = ""
        hasContract = false
        paidAmount = 0
        previouslyPaidAmount = 0
        currentCodeIndex = if (encryptedCodes == null) {
            0
        } else {
            (currentCodeIndex + 1) % encryptedCodes!!.size
        }
        setAppropriateColor()
    }

    fun setTo(newdata: Locker) {
        lastName = newdata.lastName
        firstName = newdata.firstName
        heightInCm = newdata.heightInCm
        schoolClassName = newdata.schoolClassName
        rentedFromDate = newdata.rentedFromDate
        rentedUntilDate = newdata.rentedUntilDate
        hasContract = newdata.hasContract
        paidAmount = newdata.paidAmount
        previouslyPaidAmount = newdata.previouslyPaidAmount
        setAppropriateColor()
    }

    fun setNextCode() {
        currentCodeIndex = (currentCodeIndex + 1) % 5
    }

    fun setSelected() {
        isSelected = true
        setColor(SELECTED_COLOR)
    }

    fun setUpMouseListener() {
        if (this.mouseListeners.size == 0) {
            addMouseListener(MouseListener())
        }
    }

    fun setColor(index: Int) {
        background = BACKGROUND_COLORS[index]
        foreground = FOREGROUND_COLORS[index]
    }

    val isFree: Boolean
        get() = firstName == ""

    fun getCurrentCode(sukey: SecretKey): String {
        return getCode(currentCodeIndex, sukey)
    }

    @get:Throws(CloneNotSupportedException::class)
    val copy: Locker
        get() = clone() as Locker

    /**
     * TODO move to MainFrame
     */
    private inner class MouseListener : MouseAdapter() {
        override fun mouseReleased(e: MouseEvent) {
            val dm = DataManager.getInstance()
            if (dm.curLockerList.size > 0) {
                dm.curLocker.setAppropriateColor()
            }
            val locker = e.source as Locker
            dm.curWalk.setCurLockerIndex(locker)
            locker.setSelected()
            DataManager.getInstance().mainFrame.showLockerInformation()
        }
    }

    constructor() : this(
        id = "",
        firstName = "",
        lastName = "",
        heightInCm = 0,
        schoolClassName = "",
        rentedFromDate = "",
        rentedUntilDate = "",
        hasContract = false,
        paidAmount = 0,
        previouslyPaidAmount = 0,
        currentCodeIndex = 0,
        lockCode = "",
        isOutOfOrder = false,
        note = "",
        encryptedCodes = null
    )

    constructor(locker: Locker) : this(
        id = locker.id,
        firstName = locker.firstName,
        lastName = locker.lastName,
        heightInCm = locker.heightInCm,
        schoolClassName = locker.schoolClassName,
        rentedFromDate = locker.rentedFromDate,
        rentedUntilDate = locker.rentedUntilDate,
        hasContract = locker.hasContract,
        paidAmount = locker.paidAmount,
        previouslyPaidAmount = locker.previouslyPaidAmount,
        currentCodeIndex = locker.currentCodeIndex,
        lockCode = locker.lockCode,
        isOutOfOrder = locker.isOutOfOrder,
        note = locker.note,
        encryptedCodes = locker.encryptedCodes
    )

    init {
        this.id = id

        // standard color
        setColor(FREE_COLOR)
        text = id

        // If true the component paints every pixel within its bounds.
        isOpaque = true

        // change font
        font = Font(Font.DIALOG, Font.BOLD, 20)

        // font aligment
        horizontalAlignment = CENTER

        // assign mouse events
        setUpMouseListener()
    }

    companion object {
        const val OUTOFORDER_COLOR = 0
        const val RENTED_COLOR = 1
        const val FREE_COLOR = 2
        const val SELECTED_COLOR = 3
        const val NOCONTRACT_COLOR = 4
        const val ONEMONTHREMAINING_COLOR = 5

        // TODO: use enum
        private val BACKGROUND_COLORS = arrayOf(
            Color(255, 0, 0),
            Color(0, 102, 0),
            Color(255, 255, 255),
            Color(255, 255, 0),
            Color(0, 0, 255),
            Color(255, 153, 0)
        )

        // TODO: use enum
        private val FOREGROUND_COLORS = arrayOf(
            Color(255, 255, 255),
            Color(255, 255, 255),
            Color(0, 0, 0),
            Color(0, 0, 0),
            Color(255, 255, 255),
            Color(0, 0, 0)
        )
    }
}