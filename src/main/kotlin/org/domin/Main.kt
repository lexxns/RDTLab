package org.domin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.domin.devices.HardwareInterfaceBuilder
import org.domin.routine.Test
import org.domin.routine.TestRoutine
import org.domin.routine.WriteDataTestStep

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

