package org.domin.interfaces

import java.util.*
import kotlin.collections.HashMap

interface ITestResultCollector {
    fun logEvent(event: IEvent)
    fun startCollectingTelemetry()
    fun stopCollectingTelemetry()
    fun addTelemetryData(dataPoint: Map<String, Any>)
    fun getEvents(): HashMap<UUID, IEvent>
    fun getTelemetryData(): List<Map<String, Any>>
    fun getLatestTelemetryData(): Map<String, Any>
    fun generateSummary(): Map<String, Any>
}