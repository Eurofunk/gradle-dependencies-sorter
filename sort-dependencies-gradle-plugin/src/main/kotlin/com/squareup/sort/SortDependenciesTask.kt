package com.squareup.sort

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class SortDependenciesTask @Inject constructor(
  private val execOps: ExecOperations
) : BaseSortDependenciesTask() {

  init {
    group = JavaBasePlugin.VERIFICATION_GROUP
    description = "Sorts the dependencies block in a Gradle build script"

    @Suppress("LeakingThis")
    doNotTrackState("This task modifies build scripts in place.")
  }

  // Not really "internal", but the input and the output are the same: this task will potentially modify the build
  // script in place.
  @get:Internal
  abstract val buildScript: RegularFileProperty

  @TaskAction
  fun action() {
    val buildScript = buildScript.get().asFile.absolutePath
    val verbose = verbose.getOrElse(false)

    logger.info("Sorting '$buildScript'.")

    execOps.javaexec { javaExecSpec ->
      with(javaExecSpec) {
        mainClass.set("com.squareup.sort.MainKt")
        classpath = sortProgram
        args = buildList {
          add(buildScript)
          add("--mode")
          add("sort")
          if (verbose) {
            add("--verbose")
          }
        }
      }
    }
  }
}
