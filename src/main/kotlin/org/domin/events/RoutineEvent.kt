package org.domin.events

import org.domin.interfaces.IEvent
import org.domin.interfaces.IRoutineEvent
import java.util.UUID

class RoutineEvent(
    override val id: UUID = UUID.randomUUID(),
    override val name: String,
    override val critical: Boolean = false,
    override val eventCallback: (IEvent) -> Unit = {}
): IRoutineEvent {
    override var triggerTime: Long = System.currentTimeMillis()
}