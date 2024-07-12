package org.lexxns.utils

import java.io.File
import java.io.Serializable
import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors


interface Command : Serializable {
    fun execute()
    fun requiresAdmin(): Boolean
}

abstract class AdminCommand : Command {
    override fun requiresAdmin() = true
}

class EchoCommand(private val message: String) : Command {
    override fun execute() {
        println(message)
    }

    override fun requiresAdmin() = false
}

class CreateFileCommand(val path: String) : AdminCommand() {
    override fun execute() {
        File(path).createNewFile()
        println("File created at: $path")
    }
}

class DeleteFileCommand(private val fileName: String) : AdminCommand() {
    override fun execute() {
        println("Deleting file: $fileName")
    }
}


class CommandExecutor {
    private val executor = Executors.newSingleThreadExecutor()
    private var isElevated = false

    fun execute(command: Command) {
        if (command.requiresAdmin() && !isElevated) {
            elevatePrivileges(command)
        } else {
            executor.submit { command.execute() }
        }
    }

    private fun elevatePrivileges(adminCommand: Command) {
        println("Requesting admin privileges...")
        val javaBin = "${System.getProperty("java.home")}${File.separator}bin${File.separator}java"

        val currentClasspath = System.getProperty("java.class.path")
        println("Current classpath: $currentClasspath")

        val commandClass = adminCommand.javaClass.name
        val elevatedExecutorClass = ElevatedCommandExecutor::class.java.name
        val serializedCommand = serializeCommand(adminCommand)
        val javaArgs = "-cp \"$currentClasspath\" $elevatedExecutorClass $commandClass $serializedCommand"

        val batchFile = createBatchFile(javaBin, javaArgs)

        val powershellCommand = """
            Start-Process -FilePath '$batchFile' -Verb RunAs -Wait -PassThru | Out-Null
            if ($?) { 
                Write-Host "Process completed successfully"
            } else { 
                Write-Host "Process failed"
            }
        """.trimIndent()

        val command = listOf(
            "powershell",
            "-Command",
            powershellCommand
        )

        try {
            println("Executing command: ${command.joinToString(" ")}")
            val processBuilder = ProcessBuilder(command)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            println("Process output: $output")

            if (!process.waitFor(60, TimeUnit.SECONDS)) {
                throw RuntimeException("Elevation process timed out. Output: $output")
            }

            val exitCode = process.exitValue()
            if (exitCode != 0) {
                throw RuntimeException("Elevation process failed with exit code: $exitCode. Output: $output")
            }

            println("Elevation process completed. Output: $output")
            isElevated = true
        } catch (e: Exception) {
            println("Error during elevation: ${e.message}")
            e.printStackTrace()
        } finally {
            batchFile.delete()
        }
    }

    private fun createBatchFile(javaBin: String, javaArgs: String): File {
        val batchContent = """
            @echo off
            echo Running Java command...
            echo Command: "$javaBin" $javaArgs
            "$javaBin" $javaArgs
            echo Java command completed with exit code %errorlevel%
            echo.
        """.trimIndent()

        val batchFile = File.createTempFile("run_elevated_", ".bat")
        batchFile.writeText(batchContent)
        batchFile.deleteOnExit()
        return batchFile
    }

    private fun serializeCommand(command: Command): String {
        // This is a simple serialization. In a real-world scenario, you might want to use
        // a more robust serialization method, like JSON or Protocol Buffers.
        return when (command) {
            is CreateFileCommand -> command.path
            else -> throw UnsupportedOperationException("Unsupported command type: ${command.javaClass.name}")
        }
    }

    fun shutdown() {
        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
    }
}

object ElevatedCommandExecutor {
    @JvmStatic
    fun main(args: Array<String>) {
        println("ElevatedCommandExecutor started with args: ${args.joinToString(", ")}")
        if (args.size >= 2) {
            val commandClassName = args[0]
            val serializedCommand = args[1]
            try {
                println("Attempting to create instance of: $commandClassName")
                val command = deserializeCommand(commandClassName, serializedCommand)
                println("Instance created, executing command")
                command.execute()
                println("Admin command executed successfully")
            } catch (e: Exception) {
                System.err.println("Error executing admin command: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("Insufficient arguments provided")
        }
    }

    private fun deserializeCommand(className: String, serializedData: String): Command {
        return when (className) {
            CreateFileCommand::class.java.name -> CreateFileCommand(serializedData)
            else -> throw UnsupportedOperationException("Unsupported command type: $className")
        }
    }
}

fun main() {
    val executor = CommandExecutor()

    try {
        executor.execute(EchoCommand("Hello, World!"))
        executor.execute(CreateFileCommand("C:\\adminfile.txt"))

        // Allow time for commands to complete
        Thread.sleep(5000)
    } catch (e: Exception) {
        println("Error executing commands: ${e.message}")
        e.printStackTrace()
    } finally {
        executor.shutdown()
    }
}