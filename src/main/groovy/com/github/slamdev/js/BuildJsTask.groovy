package com.github.slamdev.js

import com.github.slamdev.js.internal.CommandLineExecutor
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.api.internal.ConventionTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.api.tasks.util.PatternFilterable

@CacheableTask
class BuildJsTask extends ConventionTask {

    private final Property<File> baseDirectory

    private final Property<String> buildCommand

    private final Property<File> buildOutputDirectory

    private final Property<File> testSourceDirectory

    private final Property<File> testOutputDirectory

    private final Property<File> downloadOutputDirectory

    BuildJsTask() {
        baseDirectory = project.objects.property(File)
        buildCommand = project.objects.property(String)
        buildOutputDirectory = project.objects.property(File)
        testSourceDirectory = project.objects.property(File)
        testOutputDirectory = project.objects.property(File)
        downloadOutputDirectory = project.objects.property(File)
    }

    @TaskAction
    void run() {
        CommandLineExecutor executor = new CommandLineExecutor(project: project)
        String prefix = "${project.path == ':' ? '' : project.path}:js:build:"
        executor.exec(getBuildOutputDirectory().toPath(), prefix, getBuildCommand(), getBaseDirectory().toPath())
    }

    @Internal
    File getBaseDirectory() {
        baseDirectory.orNull
    }

    void setBaseDirectory(Provider<File> baseDirectory) {
        this.baseDirectory.set(baseDirectory)
    }

    @PathSensitive(PathSensitivity.RELATIVE)
    @OutputDirectory
    File getBuildOutputDirectory() {
        buildOutputDirectory.orNull
    }

    void setBuildOutputDirectory(Provider<File> buildOutputDirectory) {
        this.buildOutputDirectory.set(buildOutputDirectory)
    }

    @SuppressWarnings('UnnecessaryPackageReference')
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    FileTree getFilesToCache() {
        java.util.Optional.ofNullable(baseDirectory.orNull)
                .map { project.fileTree(it) }
                .map { PatternFilterable tree -> tree.exclude('node_modules') }
                .map { ConfigurableFileTree tree -> excludeFile(tree, buildOutputDirectory) }
                .map { ConfigurableFileTree tree -> excludeFile(tree, testSourceDirectory) }
                .map { ConfigurableFileTree tree -> excludeFile(tree, testOutputDirectory) }
                .map { ConfigurableFileTree tree -> excludeFile(tree, downloadOutputDirectory) }
                .orElse(null) as FileTree
    }

    @Input
    String getBuildCommand() {
        buildCommand.orNull
    }

    void setBuildCommand(Property<String> buildCommand) {
        this.buildCommand.set(buildCommand)
    }

    @Input
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    File getTestSourceDirectory() {
        testSourceDirectory.orNull
    }

    void setTestSourceDirectory(Property<File> testSourceDirectory) {
        this.testSourceDirectory.set(testSourceDirectory)
    }

    @Input
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    File getTestOutputDirectory() {
        testOutputDirectory.orNull
    }

    @Input
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    File getDownloadOutputDirectory() {
        downloadOutputDirectory.orNull
    }

    void setDownloadOutputDirectory(Property<File> downloadOutputDirectory) {
        this.downloadOutputDirectory.set(downloadOutputDirectory)
    }

    void setTestOutputDirectory(Property<File> testOutputDirectory) {
        this.testSourceDirectory.set(testOutputDirectory)
    }

    static ConfigurableFileTree excludeFile(ConfigurableFileTree tree, File file) {
        if (file == null) {
            return tree
        }
        String basePath = tree.dir
        if (file.toString().contains(basePath)) {
            String exclude = tree.dir.toPath().relativize(file.toPath())
            tree = tree.exclude(exclude) as ConfigurableFileTree
        }
        tree
    }
}
