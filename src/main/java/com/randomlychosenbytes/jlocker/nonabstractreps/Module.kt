package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.swing.JPanel

abstract class Module : JPanel() {
    @SerializedName("type")
    @Expose
    private val typeName: String = this::class.qualifiedName!!
}