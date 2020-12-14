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
    @field:Expose private var id: String,
    @field:Expose var ownerName: String,
    @field:Expose var lastName: String,
    @field:Expose var ownerSize: Int,
    @field:Expose var ownerClass: String, // TODO: use LocalDate
    @field:Expose var rentedFromDate: String, // TODO: use LocalDate
    @field:Expose var rentedUntilDate: String,
    @field:Expose private var hasContract: Boolean,
    @field:Expose var paidAmount: Int,
    @field:Expose var previouslyPaidAmount: Int,
    currentCodeIndex: Int,
    encryptedCodes: Array<String?>?,
    lockCode: String,
    isOutOfOrder: Boolean,
    note: String
) : JLabel(), Cloneable {

    @Expose
    var isOutOfOrder: Boolean

    @Expose
    var lockCode: String

    @Expose
    var note: String

    @Expose
    var currentCodeIndex: Int

    @Expose
    var encryptedCodes: Array<String?>?
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
        encryptedCodes = arrayOfNulls(5)
        for (i in 0..4) {
            codes[i] = codes[i].replace("-", "")
        }
        for (i in 0..4) {
            encryptedCodes!![i] = Utils.encrypt(codes[i], superUserMasterKey)
        }
    }

    val remainingTimeInMonths: Long
        get() {
            if (rentedUntilDate == "" || rentedFromDate == "" || isFree) {
                return 0
            }
            val today: Calendar = GregorianCalendar()
            today.isLenient = false
            today.time
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
        ownerName = ""
        ownerSize = 0
        ownerClass = ""
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
        ownerName = newdata.ownerName
        ownerSize = newdata.ownerSize
        ownerClass = newdata.ownerClass
        rentedFromDate = newdata.rentedFromDate
        rentedUntilDate = newdata.rentedUntilDate
        hasContract = newdata.hasContract
        paidAmount = newdata.paidAmount
        previouslyPaidAmount = newdata.previouslyPaidAmount
        setAppropriateColor()
    }

    fun setId(id: String) {
        text = id.also { this.id = it }
    }

    fun setFirstName(name: String) {
        ownerName = name
    }

    fun setHeightInCm(size: Int) {
        ownerSize = size
    }

    fun setSchoolClass(_class: String) {
        ownerClass = _class
    }

    fun setFromDate(fromdate: String) {
        rentedFromDate = fromdate
    }

    fun setUntilDate(untildate: String) {
        rentedUntilDate = untildate
    }

    fun setHasContract(hascontract: Boolean) {
        hasContract = hascontract
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

    fun getId(): String {
        return id
    }

    fun hasContract(): Boolean {
        return hasContract
    }

    val isFree: Boolean
        get() = ownerName == ""

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

    init {
        previouslyPaidAmount = paidAmount
        this.isOutOfOrder = isOutOfOrder
        this.lockCode = lockCode
        this.note = note
        isSelected = false
        this.currentCodeIndex = currentCodeIndex
        this.encryptedCodes = encryptedCodes

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
}