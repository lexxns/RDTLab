package org.domin

import org.domin.collectors.SimpleResultCollector
import org.domin.events.RoutineEvent
import org.domin.interfaces.ITelemetryProvider
import java.util.concurrent.TimeUnit

class TestRunner(
        private val devicesUnderTest: List<ITelemetryProvider>
) {
    private var testCompleted = false
    var report: Map<String, Any>? = null
    private val testDataCollector = SimpleResultCollector(devicesUnderTest)

    private fun setupTestEnvironment() {
        // Setup test environment
    }

    private fun executePreTestActions() {
        testDataCollector.startCollectingTelemetry()
        testDataCollector.subscribeToEvents()
        testDataCollector.logEvent(RoutineEvent(name="ready_to_start"))
    }

    private fun executeTest() {
        val startTime = System.currentTimeMillis()  // Starting time
        val duration = 10000  // Duration in milliseconds (10 seconds)
        testDataCollector.logEvent(RoutineEvent(name="executing_test"))

        while (System.currentTimeMillis() < startTime + duration) {
            val latestData = testDataCollector.getLatestTelemetryData()

            if (isTestComplete()) {
                break
            }

            // Perform the terminal output logic with latestData, if needed

            // Sleep for a short duration to avoid busy-waiting
            Thread.sleep(100)
        }

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
            executeTest()
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