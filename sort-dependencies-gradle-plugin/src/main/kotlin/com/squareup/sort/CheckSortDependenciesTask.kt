package com.squareup.sort

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject

@CacheableTask
abstract class CheckSortDependenciesTask @Inject constructor(
  private val execOps: ExecOperations
) : BaseSortDependenciesTask() {

  init {
    group = JavaBasePlugin.VERIFICATION_GROUP
    description = "Checks if the dependencies block in a Gradle build script is sorted"
  }

  @get:InputFile
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val buildScript: RegularFileProperty

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun action() {
    val buildScript = buildScript.get().asFile.absolutePath
    val verbose = verbose.getOrElse(false)

    logger.info("Checking if '$buildScript' is sorted.")

    val result = execOps.exec { execSpec ->
      execSpec.commandLine = listOf(
        "java",
        "-cp", sortProgram.asPath,
        "com.squareup.sort.MainKt",
        buildScript,
        "--mode", "check"
      )
      if (verbose) {
        execSpec.args("--verbose")
      }
    }

    val resultText = when (result.exitValue) {
      0 -> "Dependencies are correctly sorted."
      2 -> "Dependencies are not correctly sorted."
      3 -> "There were parse errors."
      else -> "The result code ${result.exitValue} is not known."
    }

    outputDirectory.asFile.get().mkdirs()
    val outputPath : Path = Paths.get(outputDirectory.asFile.get().absolutePath, "result.txt")
    File(outputPath.toUri()).writeText(resultText)
  }
}
