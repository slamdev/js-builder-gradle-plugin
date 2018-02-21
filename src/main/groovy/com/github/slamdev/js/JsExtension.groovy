package com.github.slamdev.js

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.Property

@CompileStatic
class JsExtension {

    Property<File> baseDirectory

    Property<String> buildCommand

    Property<File> buildOutputDirectory

    Property<String> testCommand

    Property<File> testOutputDirectory

    Property<File> testSourceDirectory

    Property<File> downloadOutputDirectory

    Property<String> downloadCommand

    JsExtension(Project project) {
        baseDirectory = project.objects.property(File)
        setBaseDirectory(project.file('.'))
        buildOutputDirectory = project.objects.property(File)
        setBuildOutputDirectory(project.buildDir)
        testSourceDirectory = project.objects.property(File)
        setTestSourceDirectory(project.file('jest'))
        downloadCommand = project.objects.property(String)
        setDownloadCommand('yarn --mutex network')
        testCommand = project.objects.property(String)
        setTestCommand('yarn --mutex network test')
        buildCommand = project.objects.property(String)
        setBuildCommand('yarn --mutex network build')
        testOutputDirectory = project.objects.property(File)
        setTestOutputDirectory(project.file('testResults'))
        downloadOutputDirectory = project.objects.property(File)
        setDownloadOutputDirectory(project.file('downloadResults'))
    }

    void setBaseDirectory(File baseDirectory) {
        this.baseDirectory.set(baseDirectory)
    }

    void setBuildOutputDirectory(File buildDirectory) {
        this.buildOutputDirectory.set(buildDirectory)
    }

    void setTestSourceDirectory(File testDirectory) {
        this.testSourceDirectory.set(testDirectory)
    }

    void setDownloadCommand(String downloadCommand) {
        this.downloadCommand.set(downloadCommand)
    }

    void setTestCommand(String testCommand) {
        this.testCommand.set(testCommand)
    }

    void setBuildCommand(String buildCommand) {
        this.buildCommand.set(buildCommand)
    }

    void setTestOutputDirectory(File testOutputDirectory) {
        this.testOutputDirectory.set(testOutputDirectory)
    }

    void setDownloadOutputDirectory(File downloadOutputDirectory) {
        this.downloadOutputDirectory.set(downloadOutputDirectory)
    }
}
