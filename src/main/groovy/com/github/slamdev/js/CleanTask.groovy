package com.github.slamdev.js

import org.gradle.api.internal.ConventionTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Destroys
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

class CleanTask extends ConventionTask {

    private final ListProperty<File> toRemove

    CleanTask() {
        toRemove = project.objects.listProperty(File)
    }

    @TaskAction
    void run() {
        toRemove.get().each { File dir -> cleanDirectory(dir) }
    }

    @SkipWhenEmpty
    @Destroys
    List<File> getToRemove() {
        toRemove.orNull
    }

    void setToRemove(ListProperty<File> toRemove) {
        this.toRemove.set(toRemove)
    }

    static void cleanDirectory(File dir) {
        dir.listFiles().each { File file ->
            removeDirectory(file)
        }
    }

    private static void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles()
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile)
                }
            }
            dir.delete()
        } else {
            dir.delete()
        }
    }
}
