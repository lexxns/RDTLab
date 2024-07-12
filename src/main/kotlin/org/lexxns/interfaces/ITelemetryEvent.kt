package org.lexxns.interfaces

interface ITelemetryEvent: IEvent {
    var isActive: Boolean
    var endTime: Long?

    fun condition(telemetryData: Map<String, Any>): Boolean
}