package org.lexxns.mocks

import java.io.*
import kotlin.concurrent.thread

class MockSerialDevice {
    private var file: RandomAccessFile? = null
    private var isRunning = false
    private var portName: String? = null
    private val com0comPath: String
    private lateinit var elevatedProcess: Process
    private lateinit var processInput: PrintWriter
    private lateinit var processOutput: BufferedReader

    init {
        com0comPath = findCom0ComPath()
        if (com0comPath.isEmpty()) {
            throw IllegalStateException("Could not find com0com installation. Please ensure it's installed correctly.")
        }
        startElevatedProcess()
    }

    private fun findCom0ComPath(): String {
        val possiblePaths = listOf(
            "C:\\Program Files\\com0com",
            "C:\\Program Files (x86)\\com0com",
            "C:\\com0com"
        )

        for (path in possiblePaths) {
            if (File("$path\\setupc.exe").exists()) {
                return path
            }
        }

        return ""
    }

    private fun startElevatedProcess() {
        val elevateVbs = createElevateScript()
        elevatedProcess = Runtime.getRuntime().exec(
            arrayOf("cscript", "//Nologo", elevateVbs.absolutePath, "cmd.exe")
        )
        processInput = PrintWriter(elevatedProcess.outputStream, true)
        processOutput = BufferedReader(InputStreamReader(elevatedProcess.inputStream))

        // Wait for the elevated process to start
        while (!processOutput.ready()) {
            Thread.sleep(100)
        }

        elevateVbs.delete()
    }

    private fun createElevateScript(): File {
        val script = """
        Set UAC = CreateObject("Shell.Application")
        UAC.ShellExecute "cmd.exe", "", "", "runas", 1
        """.trimIndent()

        val tempFile = File.createTempFile("elevate", ".vbs")
        tempFile.writeText(script)
        tempFile.deleteOnExit()
        return tempFile
    }

    private fun runCommand(command: String): Pair<Int, String> {
        processInput.println(command)
        processInput.println("echo %ERRORLEVEL%")

        val output = StringBuilder()
        var line: String?
        while (processOutput.readLine().also { line = it } != null) {
            if (line == "%ERRORLEVEL%") break
            output.append(line).append("\n")
        }
        val errorLevel = processOutput.readLine()?.toIntOrNull() ?: -1

        return Pair(errorLevel, output.toString())
    }

    private fun isComPortAvailable(portName: String): Boolean {
        val (_, output) = runCommand("$com0comPath\\setupc.exe list")
        return portName in output
    }

    private fun getAvailableComPorts(): List<String> {
        val (_, output) = runCommand("$com0comPath\\setupc.exe list")
        return output.split("\n")
            .filter { it.contains("COM") }
            .mapNotNull { line ->
                line.split(" ").find { it.startsWith("COM") }
            }
    }

    fun connect(): Boolean {
        try {
            // Create a pair of virtual COM ports
            val (exitCode, output) = runCommand("$com0comPath\\setupc.exe install PortName=COM10 PortName=COM11")
            if (exitCode != 0) {
                println("Failed to create virtual COM ports. Output: $output")
                return false
            }

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
            val (exitCode, output) = runCommand("$com0comPath\\setupc.exe remove 0")
            if (exitCode != 0) {
                println("Warning: Failed to remove virtual COM ports. Output: $output")
            } else {
                println("Disconnected and removed virtual COM ports")
            }
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
        elevatedProcess.destroy()
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