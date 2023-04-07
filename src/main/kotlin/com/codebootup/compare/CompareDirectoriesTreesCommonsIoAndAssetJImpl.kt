/*
 *  Copyright 2023-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.codebootup.compare

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.streams.toList

class CompareDirectoriesTreesCommonsIoAndAssetJImpl : CompareDirectories {

    override fun compare(original: Path, revised: Path): List<Difference> {
        return getDirectoryAndFileTreeDifferences(original = original, revised = revised)
    }

    private fun getDirectoryAndFileTreeDifferences(original: Path, revised: Path) : List<Difference>{
        val originalDir = File(original.absolutePathString())
        val revisedDir = File(revised.absolutePathString())

        val originalFiles = FileUtils.listFilesAndDirs(
            originalDir,
            TrueFileFilter.TRUE,
            TrueFileFilter.TRUE
        )
        .map { it.absolutePath.substring(original.absolutePathString().length) }
        .filter { it.isNotBlank() }
        .stream()
        .sorted()
        .toList()

        val revisedFiles = FileUtils.listFilesAndDirs(
            revisedDir,
            TrueFileFilter.TRUE,
            TrueFileFilter.TRUE
        )
        .map { it.absolutePath.substring(revised.absolutePathString().length) }
        .filter { it.isNotBlank() }
        .stream()
        .sorted()
        .toList()

        val missing = originalFiles
            .filter { !revisedFiles.contains(it) }
            .map { File("${original.absolutePathString()}${File.separator}$it") }
            .toList()

        val new = revisedFiles
            .filter { !originalFiles.contains(it) }
            .map { File("${revised.absolutePathString()}${File.separator}$it") }
            .toList()

        val missingFiles = missing.filter { it.isFile }
            .map { MissingFile(it.absolutePath.substring(original.absolutePathString().length)) }

        val missingDirectories = missing.filter { it.isDirectory }
            .map { MissingFile(it.absolutePath.substring(original.absolutePathString().length)) }

        val extraFiles = new.filter { it.isFile }
            .map { ExtraFile(it.absolutePath.substring(original.absolutePathString().length)) }

        val extraDirectories = new.filter { it.isDirectory }
            .map { ExtraDirectory(it.absolutePath.substring(original.absolutePathString().length)) }

        return missingFiles + missingDirectories + extraFiles + extraDirectories
    }

}