package org.lexxns.routine

import kotlinx.coroutines.delay
import org.lexxns.interfaces.ITestStep

class WaitTestStep(private val waitTimeMillis: Long) : ITestStep {
    override suspend fun execute() {
        delay(waitTimeMillis)
    }
}
