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
class TestJsTask extends ConventionTask {

    private final Property<File> baseDirectory

    private final Property<String> testCommand

    private final Property<File> buildOutputDirectory

    private final Property<File> testSourceDirectory

    private final Property<File> testOutputDirectory

    private final Property<File> downloadOutputDirectory

    TestJsTask() {
        baseDirectory = project.objects.property(File)
        testCommand = project.objects.property(String)
        buildOutputDirectory = project.objects.property(File)
        testSourceDirectory = project.objects.property(File)
        testOutputDirectory = project.objects.property(File)
        downloadOutputDirectory = project.objects.property(File)
    }

    @TaskAction
    void run() {
        CommandLineExecutor executor = new CommandLineExecutor(project: project)
        String prefix = "${project.path == ':' ? '' : project.path}:js:test:"
        executor.exec(getTestOutputDirectory().toPath(), prefix, getTestCommand(), getBaseDirectory().toPath())
    }

    @Internal
    File getBaseDirectory() {
        baseDirectory.orNull
    }

    void setBaseDirectory(Provider<File> baseDirectory) {
        this.baseDirectory.set(baseDirectory)
    }

    @Internal
    File getBuildOutputDirectory() {
        buildOutputDirectory.orNull
    }

    void setBuildOutputDirectory(Provider<File> buildDirectory) {
        this.buildOutputDirectory.set(buildDirectory)
    }

    @SuppressWarnings('UnnecessaryPackageReference')
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    FileTree getFilesToCache() {
        java.util.Optional.ofNullable(baseDirectory.orNull)
                .map { project.fileTree(it) }
                .map { PatternFilterable tree -> tree.exclude('node_modules') }
                .map { ConfigurableFileTree tree -> excludeFile(tree, buildOutputDirectory) }
                .map { ConfigurableFileTree tree -> excludeFile(tree, testOutputDirectory) }
                .map { ConfigurableFileTree tree -> excludeFile(tree, downloadOutputDirectory) }
                .orElse(null) as FileTree
    }

    @Input
    String getTestCommand() {
        testCommand.orNull
    }

    void setTestCommand(Property<String> testCommand) {
        this.testCommand.set(testCommand)
    }

    @SuppressWarnings('UnnecessaryPackageReference')
    @InputFiles
    @SkipWhenEmpty
    @PathSensitive(PathSensitivity.RELATIVE)
    Set<File> getTestSourceDirectory() {
        java.util.Optional.ofNullable(testSourceDirectory.orNull)
                .map { project.fileTree(it) }
                .map { it.files }
                .orElse(null) as Set<File>
    }

    void setTestSourceDirectory(Property<File> testSourceDirectory) {
        this.testSourceDirectory.set(testSourceDirectory)
    }

    @OutputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File getTestOutputDirectory() {
        testOutputDirectory.orNull
    }

    void setTestOutputDirectory(Property<File> testOutputDirectory) {
        this.testOutputDirectory.set(testOutputDirectory)
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
