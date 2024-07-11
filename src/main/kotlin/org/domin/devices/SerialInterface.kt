package org.domin.devices

import org.domin.interfaces.IHardwareInterface
import org.domin.interfaces.ITelemetryProvider

class SerialInterface(
    override val name: String,
    override val telemetryProvider: ITelemetryProvider,
    val port: String,
    val baudRate: Int
) : IHardwareInterface {
    override val communicationMethod: CommunicationMethod = CommunicationMethod.Serial

    override fun connect() {

    }
}