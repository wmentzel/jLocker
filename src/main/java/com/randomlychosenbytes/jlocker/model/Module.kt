package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

abstract class Module {
    @SerializedName("type")
    @Expose
    private val typeName: String = this::class.qualifiedName!!
}