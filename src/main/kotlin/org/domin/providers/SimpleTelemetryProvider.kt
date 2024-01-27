package org.domin.providers

import org.domin.interfaces.ITelemetryEvent
import org.domin.interfaces.ITelemetryProvider
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

open class SimpleTelemetryProvider : ITelemetryProvider {
    override val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    override val subscribers: MutableList<(Map<String, Any>) -> Unit> = mutableListOf()
    override val events: MutableList<ITelemetryEvent> = mutableListOf()
    override val name: String = "SimpleTelemetryProvider"

    override fun pollTelemetry(): Map<String, Any> {
        return mapOf("test" to "test")
    }

    override fun pollTelemetryAndHandle() {
        val telemetryData = pollTelemetry()
        checkEvents(telemetryData)
        notifySubscribers(telemetryData)
    }

    private fun checkEvents(telemetryData: Map<String, Any>) {
        val currentTime = System.currentTimeMillis()

        events.forEach { event ->
            val isActiveConditionMet = event.condition(telemetryData)

            if (isActiveConditionMet) {
                if (!event.isActive) {
                    // Event became active, record start time and call callback with no duration
                    event.isActive = true
                    event.triggerTime = currentTime
                    event.callback()
                } else {
                    // Event is still active, but we ignore it as it's in a sustained state
                }
            } else {
                if (event.isActive) {
                    event.isActive = false
                    event.endTime = currentTime
                    event.callback()
                }
            }
        }
    }

    override fun startPolling(interval: Int, timeUnit: TimeUnit) {
        scheduler.scheduleAtFixedRate(
            ::pollTelemetryAndHandle, 0,
            interval.toLong(), timeUnit
        )
    }

    override fun stopPolling() {
        scheduler.shutdown()
    }

    override fun notifySubscribers(telemetryData: Map<String, Any>) {
        subscribers.forEach { it(telemetryData) }
    }

    override fun subscribeToTelemetry(callback: (Map<String, Any>) -> Unit) {
        subscribers.add(callback)
    }

    override fun unsubscribeFromTelemetry(callback: (Map<String, Any>) -> Unit) {
        subscribers.remove(callback)
    }

    override fun registerEventTrigger(event: ITelemetryEvent) {
        events.add(event)
    }

    override fun removeEventTrigger(eventName: String) {
        events.removeIf { it.name == eventName }
    }

    override fun shutdown() {
        stopPolling()
        subscribers.clear()
    }
}