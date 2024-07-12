package org.lexxns

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.lexxns.devices.HardwareInterfaceBuilder
import org.lexxns.routine.Test
import org.lexxns.routine.TestRoutine
import org.lexxns.routine.WriteDataTestStep

fun Map<*, *>.toPrettyJsonString(): String {
    val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    return objectMapper.writeValueAsString(this)
}

fun printReport(report: Map<*,*>) {
    println("Test report:")
    println(report.toPrettyJsonString())
}

fun main() {
    val device = HardwareInterfaceBuilder().apply {
        name = "A"
        configureMock()
    }.build()
    val deviceB = HardwareInterfaceBuilder().apply {
        name = "B"
        configureMock()
    }.build()
    val routine = TestRoutine(listOf(
        WriteDataTestStep("Hello", device),
        WriteDataTestStep("World", deviceB)
    ))
    val test = Test().apply {
        addRoutine(routine)
    }

    val devices = listOf(device, deviceB)
    val testRunner = TestRunner(devices, test)
    testRunner.run()

    printReport(testRunner.report!!)
}

