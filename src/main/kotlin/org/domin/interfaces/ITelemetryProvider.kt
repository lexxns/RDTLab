package org.domin.interfaces

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

interface ITelemetryProvider {
    val scheduler: ScheduledExecutorService
    val subscribers: MutableList<(Map<String, Any>) -> Unit>
    val events: MutableList<ITelemetryEvent>
    val name: String

    fun pollTelemetry(): Map<String, Any>
    fun registerEventTrigger(event: ITelemetryEvent)
    fun removeEventTrigger(eventName: String)
    fun startPolling(interval: Int, timeUnit: TimeUnit = TimeUnit.SECONDS)
    fun stopPolling()
    fun shutdown()
    fun pollTelemetryAndHandle()
    fun notifySubscribers(telemetryData: Map<String, Any>)
    fun subscribeToTelemetry(callback: (Map<String, Any>) -> Unit)
    fun unsubscribeFromTelemetry(callback: (Map<String, Any>) -> Unit)
}