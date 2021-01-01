package com.randomlychosenbytes.jlocker.main

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.randomlychosenbytes.jlocker.moveOwner
import com.randomlychosenbytes.jlocker.nonabstractreps.Locker
import com.randomlychosenbytes.jlocker.nonabstractreps.Pupil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoveOwnerTest {

    @Test
    fun shouldMoveOwnerToNewLocker() {

        val sourceLocker = Locker(id = "1").apply {
            moveInNewOwner(Pupil())
        }

        val destinationLocker = Locker(id = "2")

        moveOwner(sourceLocker, destinationLocker)

        assertThat(sourceLocker.isFree).isTrue()
        assertThat(destinationLocker.isFree).isFalse()
    }

    @Test
    fun shouldThrowIfDestinatinLockerIsNotFree() {

        val sourceLocker = Locker(id = "1").apply {
            moveInNewOwner(Pupil())
        }

        val destinationLocker = Locker(id = "2").apply {
            moveInNewOwner(Pupil())
        }

        val exception = assertThrows<IllegalStateException> {
            moveOwner(sourceLocker, destinationLocker)
        }

        assertThat(exception.message).isEqualTo("The destination locker still has an owner who has to be unassigned before a new owner can be assigned.")
    }

    @Test
    fun shouldThrowIfSourceLockerIsFree() {

        val sourceLocker = Locker(id = "1")

        val destinationLocker = Locker(id = "2").apply {
            moveInNewOwner(Pupil())
        }

        val exception = assertThrows<IllegalStateException> {
            moveOwner(sourceLocker, destinationLocker)
        }

        assertThat(exception.message).isEqualTo("The source locker does not have an owner who could be moved to a new locker.")
    }
}