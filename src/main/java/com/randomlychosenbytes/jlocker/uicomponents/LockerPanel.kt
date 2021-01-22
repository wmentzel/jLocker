package com.randomlychosenbytes.jlocker.uicomponents

import com.randomlychosenbytes.jlocker.State
import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.model.Locker
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel

class LockerPanel(var locker: Locker) : JLabel() {

    init {
        if (isFirst) {
            setSelectedColor()
            isFirst = false
            dataManager.currentLockerPanel = this
        }

        setAppropriateColor()

        // standard color
        text = locker.id

        // If true the component paints every pixel within its bounds.
        isOpaque = true

        // change font
        font = Font(Font.DIALOG, Font.BOLD, 20)

        // font aligment
        horizontalAlignment = JLabel.CENTER

        // assign mouse events
        addMouseListener(MouseListener())
    }

    companion object {
        var isFirst = true
    }

    private inner class MouseListener : MouseAdapter() {
        override fun mouseReleased(e: MouseEvent) {
            State.mainFrame.selectLocker(e.source as LockerPanel)
        }
    }

    fun setAppropriateColor() {
        listOf(
            locker.isOutOfOrder to Color.OutOfOrder,
            locker.isFree to Color.Free,
            (!locker.isFree && locker.pupil.remainingTimeInMonths <= 1) to Color.OneMonthRentRemaining,
            (!locker.isFree && locker.pupil.hasContract) to Color.Rented,
            true to Color.NoContract
        ).first { (predicate, _) ->
            predicate
        }.let { (_, color) ->
            setColor(color)
        }
    }

    fun setSelectedColor() {
        setColor(Color.Selected)
    }

    private fun setColor(color: Color) {
        background = color.background
        foreground = color.foreground
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