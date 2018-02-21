package com.github.slamdev.js

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

@CompileStatic
class JsBuilderPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        JsExtension extension = project.extensions.create('js', JsExtension, project)
        project.tasks.create('downloadJsDependencies', DownloadJsDependenciesTask) { DownloadJsDependenciesTask task ->
            task.setDownloadCommand(extension.downloadCommand)
            task.setDownloadOutputDirectory(extension.downloadOutputDirectory)
            task.calculatePackageJsonFile(extension.baseDirectory)
            task.group = 'js'
        }
        project.tasks.create('buildJs', BuildJsTask) { BuildJsTask task ->
            task.setBuildCommand(extension.buildCommand)
            task.setBuildOutputDirectory(extension.buildOutputDirectory)
            task.setBaseDirectory(extension.baseDirectory)
            task.setTestSourceDirectory(extension.testSourceDirectory)
            task.setTestOutputDirectory(extension.testOutputDirectory)
            task.setDownloadOutputDirectory(extension.downloadOutputDirectory)
            task.group = 'js'
            task.dependsOn('downloadJsDependencies')
            project.tasks.findByName('assemble')?.dependsOn('buildJs')
        }
        project.tasks.create('testJs', TestJsTask) { TestJsTask task ->
            task.setTestCommand(extension.testCommand)
            task.setBuildOutputDirectory(extension.buildOutputDirectory)
            task.setBaseDirectory(extension.baseDirectory)
            task.setTestSourceDirectory(extension.testSourceDirectory)
            task.setTestOutputDirectory(extension.testOutputDirectory)
            task.setDownloadOutputDirectory(extension.downloadOutputDirectory)
            task.group = 'js'
            task.dependsOn('downloadJsDependencies')
            project.tasks.findByName('check')?.dependsOn('testJs')
        }
        project.tasks.create('cleanJs', CleanTask) { CleanTask task ->
            task.setToRemove(
                    combine(project, extension.buildOutputDirectory,
                            extension.testOutputDirectory, extension.downloadOutputDirectory)
            )
            task.group = 'js'
            project.tasks.findByName('clean')?.dependsOn('cleanJs')
        }
        project.tasks.create('cleanJsDependencies', CleanTask) { CleanTask task ->
            task.setToRemove(combine(project, extension.baseDirectory.map {
                new File(it, 'node_modules')
            } as Property<File>, extension.downloadOutputDirectory))
            task.group = 'js'
        }
    }

    private static ListProperty<File> combine(Project project, Property<File>... properties) {
        ListProperty<File> list = project.objects.listProperty(File)
        properties.each { Property<File> property -> list.add(property) }
        list
    }
}
