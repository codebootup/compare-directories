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
import org.assertj.core.internal.Diff
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.streams.toList

/**
 * Implementation for [CompareDirectories]
 * This class uses assertj's [Diff] for comparison of two files or paths.
 */
class CompareDirectoriesTreesCommonsIoAndAssetJImpl : CompareDirectories {

    override fun compare(original: Path, revised: Path): List<Difference> {

        val originalFile = File(original.absolutePathString())
        val revisedFile = File(revised.absolutePathString())

        return getDirectoryAndFileTreeDifferences(original = originalFile, revised = revisedFile) +
            getContentDifferences(original = originalFile, revised = revisedFile)
    }

    private fun getContentDifferences(original: File, revised: File): List<Difference> {
        val originalFiles = files(original)
        val revisedFiles = files(revised)

        val oFiles = originalFiles
            .filter { it.isFile }
            .associateBy { it.canonicalPath.substring(original.canonicalPath.length + 1) }

        val rFiles = revisedFiles
            .filter { it.isFile }
            .associateBy { it.canonicalPath.substring(revised.canonicalPath.length + 1) }

        val filesToCompare = oFiles
            .keys
            .filter { oFiles[it] != null && rFiles[it] != null }

        return filesToCompare
            .map {
                val diff = Diff().diff(
                    rFiles[it],
                    Charset.defaultCharset(),
                    oFiles[it],
                    Charset.defaultCharset(),
                )
                val deltas = diff.map { d ->
                    ContentDelta(
                        original = ContentChunk(
                            position = d.original.position,
                            lines = d.original.lines.filter { l -> l.isNotBlank() }.map { l -> ContentLine(l) },
                        ),
                        revised = ContentChunk(
                            position = d.revised.position,
                            lines = d.revised.lines.filter { l -> l.isNotBlank() }.map { l -> ContentLine(l) },
                        ),
                        type = ContentDelta.ContentDeltaType.valueOf(d.type.toString()),
                    )
                }
                ContentDifference(
                    file = it,
                    deltas = deltas,
                )
            }
            .filter { it.deltas.isNotEmpty() }
    }

    private fun getDirectoryAndFileTreeDifferences(original: File, revised: File): List<Difference> {
        val originalFiles = sortedRelativeFiles(original)
        val revisedFiles = sortedRelativeFiles(revised)

        val missing = originalFiles
            .filter { !revisedFiles.contains(it) }
            .map { File("${original.canonicalPath}${File.separator}$it") }
            .toList()

        val new = revisedFiles
            .filter { !originalFiles.contains(it) }
            .map { File("${revised.canonicalPath}${File.separator}$it") }
            .toList()

        val missingFiles = missing.filter { it.isFile }
            .map { MissingFile(it.canonicalPath.substring(original.canonicalPath.length + 1)) }

        val missingDirectories = missing.filter { it.isDirectory }
            .map { MissingDirectory(it.canonicalPath.substring(original.canonicalPath.length + 1)) }

        val extraFiles = new.filter { it.isFile }
            .map { ExtraFile(it.canonicalPath.substring(revised.canonicalPath.length + 1)) }

        val extraDirectories = new.filter { it.isDirectory }
            .map { ExtraDirectory(it.canonicalPath.substring(revised.canonicalPath.length + 1)) }

        return missingFiles + missingDirectories + extraFiles + extraDirectories
    }

    private fun sortedRelativeFiles(dir: File): List<String> {
        return FileUtils.listFilesAndDirs(
            dir,
            TrueFileFilter.TRUE,
            TrueFileFilter.TRUE,
        )
            .map { it.canonicalPath.substring(dir.canonicalPath.length) }
            .filter { it.isNotBlank() }
            .stream()
            .sorted()
            .toList()
    }

    private fun files(dir: File): List<File> {
        return FileUtils.listFilesAndDirs(
            dir,
            TrueFileFilter.TRUE,
            TrueFileFilter.TRUE,
        ).map { it }
    }
}
