package org.lexxns.routine

import org.lexxns.interfaces.IHardwareInterface
import org.lexxns.interfaces.ITestStep

class WriteDataTestStep(private val data: String, private val target: IHardwareInterface) : ITestStep {
    override suspend fun execute() {
        target.writeData(data)
    }
}