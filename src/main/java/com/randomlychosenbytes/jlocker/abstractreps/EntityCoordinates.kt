package com.randomlychosenbytes.jlocker.abstractreps

/**
 * Every Entity (ManamgentUnit, Locker, Room etc.) has a unique sequence of numbers.
 * I called them coordinates. Often it's necessary to save this coordinates alongside
 * the actual object we are dealing with. For instance in the SearchFrame: When we
 * double click on a query result we are automatically brought to the belonging
 * locker. For this we need to set the drop down menus to the building
 * (1st coordinate), floor (2nd coordinate) and walk (3rd coordinate) the locker
 * is in.
 */
class EntityCoordinates<T : Any>(
    var entity: T,
    var bValue: Int,
    var fValue: Int,
    var wValue: Int,
    var mValue: Int,
    var lValue: Int,
) {
    override fun toString(): String {
        return "$bValue-$fValue-$wValue-$mValue-$lValue"
    }
}