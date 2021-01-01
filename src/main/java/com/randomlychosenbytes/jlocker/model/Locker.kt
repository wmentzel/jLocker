package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.randomlychosenbytes.jlocker.DataManager
import com.randomlychosenbytes.jlocker.decrypt
import com.randomlychosenbytes.jlocker.encrypt
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
        listOf(
            isOutOfOrder to Color.OutOfOrder,
            isFree to Color.Free,
            (_pupil?.let { it.remainingTimeInMonths <= 1 } == true) to Color.OneMonthRentRemaining,
            (_pupil?.hasContract == true) to Color.Rented,
            true to Color.NoContract
        ).first { (predicate, _) ->
            predicate
        }.let { (_, color) ->
            setColor(color)
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
        setColor(Color.Selected)
    }

    private fun setUpMouseListener() {
        if (this.mouseListeners.isEmpty()) {
            addMouseListener(MouseListener())
        }
    }

    private fun setColor(color: Color) {
        background = color.background
        foreground = color.foreground
    }

    val isFree: Boolean
        get() = _pupil == null

    fun getCurrentCode(sukey: SecretKey): String {
        return getCode(currentCodeIndex, sukey)
    }

    private inner class MouseListener : MouseAdapter() {
        override fun mouseReleased(e: MouseEvent) {
            DataManager.mainFrame.selectLocker(e.source as Locker)
        }
    }

    constructor(id: String) : this(
        id = id,
        pupil = null,
        currentCodeIndex = 0,
        lockCode = "",
        isOutOfOrder = false,
        note = "",
        encryptedCodes = null
    )

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
        setColor(Color.Free)
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

    enum class Color(
        val background: java.awt.Color,
        val foreground: java.awt.Color
    ) {
        OutOfOrder(
            java.awt.Color(255, 0, 0),
            java.awt.Color(255, 255, 255),
        ),
        Rented(
            java.awt.Color(0, 102, 0),
            java.awt.Color(255, 255, 255),
        ),
        Free(
            java.awt.Color(255, 255, 255),
            java.awt.Color(0, 0, 0),
        ),
        Selected(
            java.awt.Color(255, 255, 0),
            java.awt.Color(0, 0, 0),
        ),
        NoContract(
            java.awt.Color(0, 0, 255),
            java.awt.Color(255, 255, 255),
        ),
        OneMonthRentRemaining(
            java.awt.Color(255, 153, 0),
            java.awt.Color(0, 0, 0)
        )
    }
}