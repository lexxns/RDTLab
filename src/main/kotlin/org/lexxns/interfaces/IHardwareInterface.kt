package org.lexxns.interfaces

import org.lexxns.devices.CommunicationMethod

interface IHardwareInterface {
    val name: String
    val telemetryProvider: ITelemetryProvider
    val communicationMethod: CommunicationMethod

    fun connect(){}
    fun disconnect(){}
    fun readData(): Any { return Any() }
    fun writeData(data: Any){}
}


interface Strategy {
    fun execute()
}