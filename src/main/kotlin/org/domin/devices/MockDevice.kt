package org.domin.devices
import net.datafaker.Faker
import org.domin.providers.SimpleTelemetryProvider
import java.time.ZoneOffset
import java.time.ZonedDateTime

class MockDevice : SimpleTelemetryProvider() {
    private val faker = Faker()

    override fun pollTelemetry(): Map<String, Any> {
        return mapOf(
            "temperature" to faker.number().randomDouble(2, -20, 50),
            "pressure" to faker.number().randomDouble(2, 0, 100),
            "timestamp" to ZonedDateTime.now(ZoneOffset.UTC).toString()
        )
    }
}