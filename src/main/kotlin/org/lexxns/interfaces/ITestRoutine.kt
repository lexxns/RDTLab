package org.lexxns.interfaces

interface ITestRoutine {
    val steps: List<ITestStep>

    fun run()
    fun addStep(step: ITestStep): ITestRoutine
}