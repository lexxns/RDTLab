package org.lexxns.devices

import org.lexxns.interfaces.IHardwareInterface
import org.lexxns.interfaces.ITelemetryProvider
import org.lexxns.providers.SimpleTelemetryProvider


class HardwareInterfaceBuilder {
    var name: String = ""
    private var interfaceConfig: InterfaceConfig = MockConfig
    private var telemetryProvider: ITelemetryProvider = SimpleTelemetryProvider()

    fun build(): IHardwareInterface {
        return when (val config = interfaceConfig) {
            is MockConfig -> MockInterface(name, telemetryProvider)
            is SerialConfig -> SerialInterface(name, telemetryProvider, config.port, config.baudRate)
        }
    }

    fun configureMock() {
        interfaceConfig = MockConfig
    }

    fun configureSerial(port: String, baudRate: Int) {
        interfaceConfig = SerialConfig(port, baudRate)
    }

    // Add other configuration methods for different interface types as needed
}

sealed class InterfaceConfig
data object MockConfig : InterfaceConfig()
class SerialConfig(val port: String, val baudRate: Int) : InterfaceConfig()

fun HardwareInterfaceBuilder.serial(port: String, baudRate: Int) = apply {
    configureSerial(port, baudRate)
}