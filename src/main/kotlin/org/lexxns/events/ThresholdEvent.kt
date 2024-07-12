package org.lexxns.events

import org.lexxns.interfaces.IEvent
import org.lexxns.interfaces.ITelemetryEvent
import java.util.UUID

class ThresholdEvent<T : Number>(
    override val id: UUID = UUID.randomUUID(),
    override val name: String,
    private val telemetryKey: String,
    override val eventCallback: (IEvent) -> Unit,
    private val threshold: T,
    private val comparisonOperator: (T, T) -> Boolean
) : ITelemetryEvent {
    override var isActive: Boolean = false
    override var triggerTime: Long = System.currentTimeMillis()
    override var endTime: Long? = null

    @Suppress("UNCHECKED_CAST")
    override fun condition(telemetryData: Map<String, Any>): Boolean {
        val telemetryValue = telemetryData[telemetryKey] as? T ?: return false
        return comparisonOperator(telemetryValue, threshold)
    }
}