package com.randomlychosenbytes.jlocker.main

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.randomlychosenbytes.jlocker.generateKey
import com.randomlychosenbytes.jlocker.model.Locker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LockerTest {
    @BeforeEach
    fun setup() {
    }

    @Test
    fun `should encrypt and decrypt codes correctly`() {

        val superUserMasterKey = generateKey()

        val codes = listOf(
            "11-11-11",
            "22-22-22",
            "33-33-33",
            "44-44-44",
            "55-55-55"
        )

        val locker = Locker(id = "1").apply {
            setCodes(codes.toTypedArray(), superUserMasterKey)
        }

        assertThat(locker.getCodes(superUserMasterKey).toList()).isEqualTo(codes)
    }
}