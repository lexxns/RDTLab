package org.domin.routine

import kotlinx.coroutines.delay
import org.domin.interfaces.ITestStep

class WaitTestStep(private val waitTimeMillis: Long) : ITestStep {
    override suspend fun execute() {
        delay(waitTimeMillis)
    }
}
