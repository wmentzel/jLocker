package com.randomlychosenbytes.jlocker

import com.randomlychosenbytes.jlocker.nonabstractreps.Locker

/**
 * Moves a student from one locker to another.
 */
fun moveOwner(sourceLocker: Locker, destLocker: Locker) {

    if (sourceLocker.isFree) {
        throw IllegalStateException("The source locker does not have an owner who could be moved to a new locker.")
    }

    if (!destLocker.isFree) {
        throw IllegalStateException("The destination locker still has an owner who has to be unassigned before a new owner can be assigned.")
    }

    destLocker.moveInNewOwner(sourceLocker.pupil)
    sourceLocker.empty()
}