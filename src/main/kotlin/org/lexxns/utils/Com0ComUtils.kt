package org.lexxns.utils

import java.io.File

object Com0ComUtils {
    val com0comPath: String by lazy { findCom0ComPath() }

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

        throw IllegalStateException("Could not find com0com installation. Please ensure it's installed correctly.")
    }

    fun runCommand(command: String): Pair<Int, String> {
        val process = Runtime.getRuntime().exec(command)
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()
        return Pair(exitCode, output)
    }
}

class ListComPortsCommand : AdminCommand() {
    override fun execute(): String {
        val (_, output) = Com0ComUtils.runCommand("${Com0ComUtils.com0comPath}\\setupc.exe list")
        return output
    }
}

class CreateComPortsCommand(val port1: String, val port2: String) : AdminCommand() {
    override fun execute(): String {
        val (exitCode, output) = Com0ComUtils.runCommand("${Com0ComUtils.com0comPath}\\setupc.exe install PortName=$port1 PortName=$port2")
        if (exitCode != 0) {
            throw RuntimeException("Failed to create virtual COM ports. Output: $output")
        }
        return "Created virtual COM ports: $port1 and $port2"
    }
}

class RemoveComPortsCommand(val pair: Int) : AdminCommand() {
    override fun execute(): String {
        val (exitCode, output) = Com0ComUtils.runCommand("${Com0ComUtils.com0comPath}\\setupc.exe remove $pair")
        if (exitCode != 0) {
            throw RuntimeException("Failed to remove virtual COM ports. Output: $output")
        }
        return "Removed virtual COM port pair: $pair"
    }
}