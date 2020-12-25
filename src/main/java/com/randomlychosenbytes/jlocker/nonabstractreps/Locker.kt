package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.randomlychosenbytes.jlocker.manager.DataManager
import com.randomlychosenbytes.jlocker.manager.decrypt
import com.randomlychosenbytes.jlocker.manager.encrypt
import java.awt.Color
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.crypto.SecretKey
import javax.swing.JLabel

class Locker(
    id: String,
    pupil: Pupil?,
    @Expose var lockCode: String,
    @Expose var isOutOfOrder: Boolean,
    @Expose var note: String,
    @Expose var currentCodeIndex: Int,
    encryptedCodes: Array<String>?
) : JLabel() {

    @SerializedName("pupil")
    @Expose
    private var _pupil: Pupil? = pupil

    val pupil get() = _pupil ?: throw IllegalStateException()

    fun moveInNewOwner(pupil: Pupil) {
        _pupil = pupil
        setAppropriateColor()
    }

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

    fun getCodes(sukey: SecretKey) = (0..4).map { i ->
        getCode(i, sukey)
    }.toTypedArray()

    private fun getCode(i: Int, sukey: SecretKey): String {
        if (encryptedCodes == null) {
            return "00-00-00"
        }
        val code = decrypt(encryptedCodes!![i], sukey)
        return code.substring(0, 2) + "-" + code.substring(2, 4) + "-" + code.substring(4, 6)
    }

    fun setCodes(codes: Array<String>, superUserMasterKey: SecretKey?) {
        // codes is unencrypted... encrypting and saving in encCodes

        // The Value of code[i] looks like "00-00-00"
        // it is saved without the "-", so we have
        // to remove them.
        encryptedCodes = (0..4).map { i ->
            val code = codes[i].replace("-", "")
            encrypt(code, superUserMasterKey)
        }.toTypedArray()
    }

    fun setAppropriateColor() {

        if (isFree) {
            setColor(FREE_COLOR)
        } else {
            if (pupil.hasContract) {
                setColor(RENTED_COLOR)
            } else {
                setColor(NOCONTRACT_COLOR)
            }

            if (pupil.remainingTimeInMonths <= 1) {
                setColor(ONEMONTHREMAINING_COLOR)
            }
        }

        if (isOutOfOrder) {
            setColor(OUTOFORDER_COLOR)
        }
    }

    fun empty() {
        _pupil = null
        currentCodeIndex = if (encryptedCodes == null) {
            0
        } else {
            (currentCodeIndex + 1) % encryptedCodes!!.size
        }
        setAppropriateColor()
    }

    fun setNextCode() {
        currentCodeIndex = (currentCodeIndex + 1) % 5
    }

    fun setSelected() {
        isSelected = true
        setColor(SELECTED_COLOR)
    }

    private fun setUpMouseListener() {
        if (this.mouseListeners.isEmpty()) {
            addMouseListener(MouseListener())
        }
    }

    fun setColor(index: Int) {
        background = BACKGROUND_COLORS[index]
        foreground = FOREGROUND_COLORS[index]
    }

    val isFree: Boolean
        get() = _pupil == null

    fun getCurrentCode(sukey: SecretKey): String {
        return getCode(currentCodeIndex, sukey)
    }

    /**
     * TODO move to MainFrame
     */
    private inner class MouseListener : MouseAdapter() {
        override fun mouseReleased(e: MouseEvent) {
            if (DataManager.currentLockerList.size > 0) {
                DataManager.currentLocker.setAppropriateColor()
            }
            val locker = e.source as Locker
            DataManager.currentWalk.setCurLockerIndex(locker)
            locker.setSelected()
            DataManager.mainFrame.showLockerInformation()
        }
    }

    constructor() : this(
        id = "",
        pupil = null,
        currentCodeIndex = 0,
        lockCode = "",
        isOutOfOrder = false,
        note = "",
        encryptedCodes = null
    )

    constructor(locker: Locker) : this(
        id = locker.id,
        pupil = if (locker.isFree) null else locker.pupil,
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