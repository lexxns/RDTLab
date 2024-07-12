package org.lexxns.events

import org.lexxns.interfaces.IEvent
import org.lexxns.interfaces.IRoutineEvent
import java.util.UUID

class RoutineEvent(
    override val id: UUID = UUID.randomUUID(),
    override val name: String,
    override val critical: Boolean = false,
    override val eventCallback: (IEvent) -> Unit = {}
): IRoutineEvent {
    override var triggerTime: Long = System.currentTimeMillis()
}