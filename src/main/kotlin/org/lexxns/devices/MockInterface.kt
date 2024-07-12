package org.lexxns.devices
import net.datafaker.Faker
import org.lexxns.interfaces.IHardwareInterface
import org.lexxns.interfaces.ITelemetryProvider
import java.time.ZoneOffset
import java.time.ZonedDateTime

class MockInterface(override val name: String,
                    override val telemetryProvider: ITelemetryProvider
) : IHardwareInterface {
    private val faker = Faker()
    override val communicationMethod = CommunicationMethod.Mock

    override fun connect() {
        println("Mock connection established")
    }

    override fun disconnect() {
        println("Mock connection closed")
    }

    override fun readData(): Map<String, Any> {
        return mapOf(
            "temperature" to faker.number().randomDouble(2, -20, 50),
            "pressure" to faker.number().randomDouble(2, 0, 100),
            "timestamp" to ZonedDateTime.now(ZoneOffset.UTC).toString()
        )
    }

    override fun writeData(data: Any) {
        println("Mock data written")
    }

}