package org.lexxns

import org.lexxns.collectors.SimpleResultCollector
import org.lexxns.events.RoutineEvent
import org.lexxns.interfaces.IHardwareInterface
import org.lexxns.routine.Test

class TestRunner(
        devicesUnderTest: List<IHardwareInterface>,
        private val test: Test
) {
    private var testCompleted = false
    var report: Map<String, Any>? = null
    private val testDataCollector = SimpleResultCollector(
            devicesUnderTest.map { it.telemetryProvider }
    )

    private fun setupTestEnvironment() {
        // Setup test environment
    }

    private fun executePreTestActions() {
        testDataCollector.startCollectingTelemetry()
        testDataCollector.subscribeToEvents()
        testDataCollector.logEvent(RoutineEvent(name="ready_to_start"))
    }

    private fun executeTest(test: Test) {
        val startTime = System.currentTimeMillis()  // Starting time
        testDataCollector.logEvent(RoutineEvent(name="executing_test"))

        test.run()

        testCompleted = true
        testDataCollector.logEvent(RoutineEvent(name="test_completed"))
    }

    private fun executePostTestActions() {
        testDataCollector.stopCollectingTelemetry()
        testDataCollector.unsubscribeFromEvents()
    }

    private fun collectTestResults() {
        // Gather test data
    }

    private fun generateTestReport() {
        testDataCollector.logEvent(RoutineEvent(name="generating_test_report"))
        val routineEvents = testDataCollector.getRoutineEvents()
        val telemetryEvents = testDataCollector.getTelemetryEvents()
        val telemetryData = testDataCollector.getTelemetryData()
        val reportSummary = testDataCollector.generateSummary()

        // Create a report
        report = mapOf(
                "summary" to reportSummary,
                "routine_events" to routineEvents,
                "telemetry_events" to telemetryEvents
//              "all_data" to telemetryData
        )
    }

    private fun cleanup() {
        testDataCollector.logEvent(RoutineEvent(name="test_cleanup_completed"))
    }

    fun run() {
        try {
            setupTestEnvironment()
            executePreTestActions()
            executeTest(test)
            executePostTestActions()
        } catch (e: Exception) {
            println("An error occurred: $e")
        } finally {
            collectTestResults()
            generateTestReport()
            cleanup()
        }
    }

    private fun isTestComplete(): Boolean {
        return testCompleted
    }
}