package org.lexxns.collectors

import org.lexxns.events.ThresholdEvent
import org.lexxns.interfaces.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SimpleResultCollector(private val devices: List<ITelemetryProvider>) : ITestResultCollector {
    private val telemetryData = ArrayList<Map<String, Any>>()
    private val telemetryEvents = HashMap<UUID, ITelemetryEvent>()
    private val routineEvents = HashMap<UUID, IRoutineEvent>()

    init {
        devices.forEach { it.subscribeToTelemetry(::addTelemetryData) }
    }

    fun subscribeToEvents() {
        devices.forEach { device -> device.registerEventTrigger(
            ThresholdEvent(
                name = "threshold_test",
                telemetryKey = "temperature",
                threshold = 40,
                comparisonOperator = { a, b -> a > b },
                eventCallback = {
                    event -> logEvent(event)
                }
            )
        )}
    }

    fun unsubscribeFromEvents() {
        devices.forEach { device -> device.removeEventTrigger("threshold_test") }
    }

    override fun logEvent(event: IEvent) {
        when (event) {
            is ITelemetryEvent -> {
                telemetryEvents[event.id] = event
            }
            is IRoutineEvent -> {
                routineEvents[event.id] = event
            }
            else -> {
                println("Unknown event type: $event")
            }
        }
    }

    override fun startCollectingTelemetry() {
        telemetryData.clear()
        devices.forEach { it.startPolling(interval = 20, timeUnit = TimeUnit.MICROSECONDS) }
    }

    override fun stopCollectingTelemetry() {
        devices.forEach { it.stopPolling() }
    }

    override fun addTelemetryData(dataPoint: Map<String, Any>) {
        telemetryData.add(dataPoint)
    }

    override fun getEvents(): HashMap<UUID, IEvent> {
        return HashMap<UUID, IEvent>().apply {
            putAll(telemetryEvents)
            putAll(routineEvents)
        }
    }

    fun getTelemetryEvents(): List<ITelemetryEvent> {
        return telemetryEvents.values.sortedBy { it.triggerTime }
    }

    fun getRoutineEvents(): List<IRoutineEvent> {
        return routineEvents.values.sortedBy { it.triggerTime }
    }

    override fun getTelemetryData(): List<Map<String, Any>> {
        return telemetryData
    }

    override fun getLatestTelemetryData(): Map<String, Any> {
        return if (telemetryData.isNotEmpty()) telemetryData.last() else hashMapOf()
    }

    override fun generateSummary(): Map<String, Any> {
        val summary = mutableMapOf<String, Any>(
                "total_events" to (telemetryEvents.size + routineEvents.size),
                "telemetry_data_points" to telemetryData.size
        )

        val telemetrySummary = mutableMapOf<String, MutableMap<String, Any>>()

        if (telemetryData.isNotEmpty()) {
            val keys = telemetryData.first().keys
            for (key in keys) {
                telemetryData.map { it[key] }.also { values ->
                    val numericValues = values.filterIsInstance<Number>()
                    if (numericValues.isNotEmpty()) {
                        telemetrySummary[key] = mutableMapOf(
                                "count" to numericValues.size,
                                "average" to numericValues.averageAsDouble(),
                                "maximum" to numericValues.maxAsDouble(),
                                "minimum" to numericValues.minAsDouble()
                        )
                    } else {
                        telemetrySummary[key] = mutableMapOf(
                                "count" to values.size
                        )
                    }
                }
            }
        }

        if (telemetrySummary.isNotEmpty()) {
            summary["telemetry"] = telemetrySummary
        }

        return summary
    }

    private fun List<Number>.averageAsDouble(): Double = map { it.toDouble() }.average()
    private fun List<Number>.maxAsDouble(): Double {
        if (isEmpty()) {
            throw IllegalArgumentException("Cannot find max of an empty list.")
        }
        return maxOfOrNull { it.toDouble() } ?: throw IllegalStateException("Max calculation failed")
    }
    private fun List<Number>.minAsDouble(): Double {
        if (isEmpty()) {
            throw IllegalArgumentException("Cannot find min of an empty list.")
        }
        return minOfOrNull { it.toDouble() } ?: throw IllegalStateException("Min calculation failed")
    }
}
