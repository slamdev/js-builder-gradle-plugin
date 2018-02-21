package com.github.slamdev.js

import com.github.slamdev.js.internal.CommandLineExecutor
import org.gradle.api.internal.ConventionTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

@CacheableTask
class DownloadJsDependenciesTask extends ConventionTask {

    private final Property<File> packageJsonFile

    private final Property<String> downloadCommand

    private final Property<File> downloadOutputDirectory

    DownloadJsDependenciesTask() {
        packageJsonFile = project.objects.property(File)
        downloadCommand = project.objects.property(String)
        downloadOutputDirectory = project.objects.property(File)
    }

    @TaskAction
    void run() {
        CommandLineExecutor executor = new CommandLineExecutor(project: project)
        String prefix = "${project.path == ':' ? '' : project.path}:js:deps:"
        executor.exec(getDownloadOutputDirectory().toPath(), prefix, getDownloadCommand(),
                getPackageJsonFile().parentFile.toPath())
    }

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFile
    File getPackageJsonFile() {
        packageJsonFile.orNull
    }

    void setPackageJsonFile(Property<File> packageJsonFile) {
        this.packageJsonFile.set(packageJsonFile)
    }

    void calculatePackageJsonFile(Property<File> baseDirectory) {
        packageJsonFile.set(baseDirectory.map { new File(it, 'package.json') })
    }

    @Input
    String getDownloadCommand() {
        downloadCommand.orNull
    }

    void setDownloadCommand(Property<String> downloadCommand) {
        this.downloadCommand.set(downloadCommand)
    }

    @PathSensitive(PathSensitivity.RELATIVE)
    @OutputDirectory
    File getDownloadOutputDirectory() {
        downloadOutputDirectory.orNull
    }

    void setDownloadOutputDirectory(Property<File> downloadOutputDirectory) {
        this.downloadOutputDirectory.set(downloadOutputDirectory)
    }
}
