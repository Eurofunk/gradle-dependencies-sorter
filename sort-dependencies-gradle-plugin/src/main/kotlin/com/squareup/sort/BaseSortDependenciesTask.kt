package com.squareup.sort

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class BaseSortDependenciesTask @Inject constructor() : DefaultTask() {

  @get:Classpath
  @get:InputFiles
  abstract val sortProgram: ConfigurableFileCollection

  @get:Optional
  @get:Option(option = "verbose", description = "Enables verbose logging.")
  @get:Input
  abstract val verbose: Property<Boolean>
}
