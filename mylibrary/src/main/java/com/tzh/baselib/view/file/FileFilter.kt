package com.tzh.baselib.view.file

import java.io.File

class FileFilter(
    private val allowedExtensions: List<String> = emptyList(),
    private val showHiddenFiles: Boolean = false
) {
    fun filter(files: Array<File>): List<File> {
        return files.filter { file ->
            (showHiddenFiles || !file.name.startsWith(".")) &&
                    (allowedExtensions.isEmpty() || file.isDirectory ||
                            file.extension.toLowerCase() in allowedExtensions)
        }
    }
}