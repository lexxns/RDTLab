package org.domin.routine

import org.domin.interfaces.IHardwareInterface
import org.domin.interfaces.ITestStep

class WriteDataTestStep(private val data: String, private val target: IHardwareInterface) : ITestStep {
    override suspend fun execute() {
        target.writeData(data)
    }
}