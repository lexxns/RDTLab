package org.domin.routine

import org.domin.interfaces.ITestRoutine

class Test {
    private val routines = mutableListOf<ITestRoutine>()

    fun addRoutine(routine: ITestRoutine): Test {
        routines.add(routine)
        return this
    }

    fun build(): Test {
        return this
    }

    fun run() {
        val routine = build()
        routine.run()
    }
}