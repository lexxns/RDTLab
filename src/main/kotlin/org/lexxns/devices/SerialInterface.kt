package org.lexxns.devices

import org.lexxns.interfaces.IHardwareInterface
import org.lexxns.interfaces.ITelemetryProvider

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