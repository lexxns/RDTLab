package org.domin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.domin.devices.MockDevice

fun Map<*, *>.toPrettyJsonString(): String {
    val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    return objectMapper.writeValueAsString(this)
}

fun printReport(report: Map<*,*>) {
    println("Test report:")
    println(report.toPrettyJsonString())
}

fun main() {
    val device = MockDevice("A")
    val deviceB = MockDevice("B")
    val devices = listOf(device, deviceB)
    val testRunner = TestRunner(devices)
    testRunner.run()

    printReport(testRunner.report!!)
}

