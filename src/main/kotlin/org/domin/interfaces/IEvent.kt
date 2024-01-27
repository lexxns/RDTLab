package org.domin.interfaces

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.UUID

interface IEvent {
    @get:JsonIgnore
    val id: UUID
    val name: String
    var triggerTime: Long
    @get:JsonIgnore
    val eventCallback: (IEvent) -> Unit

    fun callback() {
        eventCallback(this)
    }
}