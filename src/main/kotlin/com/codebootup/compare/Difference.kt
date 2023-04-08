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

interface Difference

data class MissingDirectory(val directory: String) : Difference

data class ExtraDirectory(val directory: String) : Difference

data class MissingFile(val file: String) : Difference

data class ExtraFile(val file: String) : Difference

data class ContentDifference(
    val file: String,
    val deltas: List<ContentDelta>) : Difference{
}

data class ContentDelta(
    val original : ContentChunk,
    val revised : ContentChunk,
    val type: ContentDeltaType
){
    enum class ContentDeltaType{
        CHANGE, INSERT, DELETE
    }
}

data class ContentChunk(
    val position : Int,
    val lines: List<ContentLine>
)

data class ContentLine(val line: String)

class DifferenceToString(private val differences: List<Difference>){
    companion object{
        const val level1 = "\n"
        const val level2 = "\n\t"
        const val level3 = "\n\t\t"
        const val twoBlankLines = "\n\n"
    }
    override fun toString() : String{
        val contentDifferences =
            differences.filterIsInstance<ContentDifference>().joinToString(level1) { toString(it) }
        val missingFiles = differences.filterIsInstance<MissingFile>().joinToString(level2) { it.file }
        val missingDirectories = differences.filterIsInstance<MissingDirectory>().joinToString(level2) { it.directory }
        val extraFiles = differences.filterIsInstance<ExtraFile>().joinToString(level2) { it.file }
        val extraDirectories = differences.filterIsInstance<ExtraDirectory>().joinToString(level2) { it.directory }

        val missingDirectoriesString = if(missingDirectories.isNotBlank()) "Missing Directories:$level2$missingDirectories" else ""
        val missingFilesString = if(missingFiles.isNotBlank()) "Missing Files:$level2$missingFiles" else ""
        val extraDirectoriesString = if(extraDirectories.isNotBlank()) "Extra Directories:$level2$extraDirectories" else ""
        val extraFilesString = if(extraFiles.isNotBlank()) "Extra Files:$level2$extraFiles" else ""

        return missingDirectoriesString + twoBlankLines + missingFilesString + twoBlankLines + extraDirectoriesString + twoBlankLines + extraFilesString + twoBlankLines + contentDifferences
    }

    private fun toString(contentDifference: ContentDifference) : String{
        val deltas = contentDifference.deltas.map {
            val originalLines = it.original.lines.joinToString(separator = level3){ l -> l.line }
            val revisedLines = it.revised.lines.joinToString(separator = level3) { l -> l.line }

            when(it.type){
                ContentDelta.ContentDeltaType.CHANGE -> "${level2}Changed content at line ${it.original.position+1}:" +
                        "${level3}from ->" +
                        "${level3}$originalLines" +
                        "${level3}to ->" +
                        "${level3}$revisedLines\n"
                ContentDelta.ContentDeltaType.DELETE -> "${level2}Missing content at line ${it.original.position+1}:" +
                        "${level3}$originalLines\n"
                ContentDelta.ContentDeltaType.INSERT -> "${level2}Inserted content at line ${it.revised.position+1}:" +
                        "${level3}$revisedLines\n"
            }
        }
        val deltaAsString = deltas.joinToString("")
        return "${contentDifference.file}:$deltaAsString"
    }
}