package org.lexxns.mocks

import org.lexxns.utils.CommandExecutor
import org.lexxns.utils.CreateComPortsCommand
import org.lexxns.utils.ListComPortsCommand
import org.lexxns.utils.RemoveComPortsCommand
import java.io.*
import kotlin.concurrent.thread



class MockSerialDevice {
    private var commandExecutor: CommandExecutor = CommandExecutor()
    private var file: RandomAccessFile? = null
    private var isRunning = false
    private var portName: String? = null

    private fun isComPortAvailable(portName: String): Boolean {
        val listCommand = ListComPortsCommand()
        val output = commandExecutor.execute(listCommand)
        return portName in output
    }

    fun connect(): Boolean {
        try {
            // Create a pair of virtual COM ports
            val createCommand = CreateComPortsCommand("COM10", "COM11")
            val createOutput = commandExecutor.execute(createCommand)
            println(createOutput)

            // Wait for the COM port to become available
            portName = "COM10"
            var attempts = 0
            while (!isComPortAvailable(portName!!) && attempts < 10) {
                Thread.sleep(1000)
                attempts++
            }

            if (!isComPortAvailable(portName!!)) {
                println("Timed out waiting for COM port to become available")
                return false
            }

            // Try to open the COM port
            val fileName = "\\\\.\\$portName"
            try {
                file = RandomAccessFile(fileName, "rw")
            } catch (e: Exception) {
                println("Error opening COM port: ${e.message}")
                return false
            }

            println("Connected to virtual COM port: $portName")
            return true
        } catch (e: Exception) {
            println("Error connecting to virtual COM port: ${e.message}")
            return false
        }
    }

    fun disconnect() {
        try {
            if (isRunning) {
                stop()
            }
            file?.close()
            file = null

            // Remove the virtual COM ports
            val removeCommand = RemoveComPortsCommand(0)
            val removeOutput = commandExecutor.execute(removeCommand)
            println(removeOutput)
        } catch (e: Exception) {
            println("Error disconnecting: ${e.message}")
        }
    }

    fun start() {
        if (file == null) {
            println("Error: Not connected to a COM port")
            return
        }

        isRunning = true
        thread {
            while (isRunning) {
                // Simulate incoming data
                val data = "Simulated data\n".toByteArray()
                file?.write(data)
                Thread.sleep(1000) // Wait for 1 second before sending next data
            }
        }
    }

    fun stop() {
        isRunning = false
    }

    fun sendData(data: ByteArray) {
        if (file == null) {
            println("Error: Not connected to a COM port")
            return
        }
        file?.write(data)
    }

    fun readData(): ByteArray {
        if (file == null) {
            println("Error: Not connected to a COM port")
            return ByteArray(0)
        }
        val available = file!!.length() - file!!.filePointer
        val buffer = ByteArray(available.toInt())
        file?.read(buffer)
        return buffer
    }

    fun close() {
        disconnect()
        commandExecutor.shutdown()
    }
}

fun main() {
    try {
        val device = MockSerialDevice()

        if (device.connect()) {
            device.start()

            // Example usage
            device.sendData("Hello from mock device".toByteArray())
            println(String(device.readData()))

            Thread.sleep(5000) // Run for 5 seconds

            device.disconnect()
        } else {
            println("Failed to connect to virtual COM port")
        }

        device.close()
    } catch (e: IllegalStateException) {
        println(e.message)
    }
}