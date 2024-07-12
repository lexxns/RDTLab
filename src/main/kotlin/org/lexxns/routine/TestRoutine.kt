package org.lexxns.routine

import org.lexxns.interfaces.ITestRoutine
import org.lexxns.interfaces.ITestStep

class TestRoutine(override val steps: List<ITestStep>) : ITestRoutine {
    override fun run() {
        steps.forEach { it.run {
            println("Running step: $it")
        } }
    }

    override fun addStep(step: ITestStep): ITestRoutine {
        val newSteps = steps.toMutableList()
        newSteps.add(step)
        return TestRoutine(newSteps)
    }
}