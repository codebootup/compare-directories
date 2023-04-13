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

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.Path

class CompareDirectoriesTreesCommonsIoAndAssetJImplTest {

    @Test
    fun `CompareDirectoriesTreesCommonsIoAndAssetJImplTest is subclass of CompareDirectories`() {
        assertThat(CompareDirectoriesTreesCommonsIoAndAssetJImpl()).isInstanceOf(CompareDirectories::class.java)
    }

    @Test
    fun `can detect extra file and directory`() {
        val testResources = CompareDirectoriesTreesCommonsIoAndAssetJImplTest::class.java.classLoader.getResource("extraDirectoryFileTest")
        val differences = compare(File(testResources.path))
        assertThat(differences).containsExactlyElementsOf(
            listOf(ExtraFile("extraDir${File.separator}extraFile.txt"), ExtraDirectory("extraDir")),
        )
    }

    @Test
    fun `can detect missing file and directory`() {
        val testResources = CompareDirectoriesTreesCommonsIoAndAssetJImplTest::class.java.classLoader.getResource("missingDirectoryFileTest")
        val differences = compare(File(testResources.path))
        assertThat(differences).containsExactlyElementsOf(
            listOf(MissingFile("missingDir${File.separator}missingFile.txt"), MissingDirectory("missingDir")),
        )
    }

    @Test
    fun `can detect content change`() {
        val testResources = CompareDirectoriesTreesCommonsIoAndAssetJImplTest::class.java.classLoader.getResource("contentDifferenceFileTest")
        val differences = compare(File(testResources.path))
        assertThat(differences).containsExactlyElementsOf(
            listOf(
                ContentDifference(
                    file = "same.txt",
                    deltas = listOf(
                        ContentDelta(
                            original = ContentChunk(position = 2, lines = listOf(ContentLine("line3"), ContentLine("line4"))),
                            revised = ContentChunk(position = 2, lines = listOf(ContentLine("line25"))),
                            type = ContentDelta.ContentDeltaType.CHANGE,
                        ),
                        ContentDelta(
                            original = ContentChunk(position = 6, lines = listOf(ContentLine("missing"))),
                            revised = ContentChunk(position = 5, lines = listOf()),
                            type = ContentDelta.ContentDeltaType.DELETE,
                        ),
                        ContentDelta(
                            original = ContentChunk(position = 10, lines = listOf()),
                            revised = ContentChunk(position = 8, lines = listOf(ContentLine("line10"))),
                            type = ContentDelta.ContentDeltaType.INSERT,
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `can pretty print differences`() {
        val testResources = CompareDirectoriesTreesCommonsIoAndAssetJImplTest::class.java.classLoader.getResource("lotsOfDifferencesTest")
        val differences = compare(File(testResources.path))
        if (System.getProperty("os.name").startsWith("Windows")) {
            assertThat(PrettyPrintDifferences().print(differences)).isEqualTo(
                "Missing Directories:\n" +
                    "\tmissingDir\n" +
                    "\tmissingDir2\n" +
                    "\n" +
                    "Missing Files:\n" +
                    "\tmissingDir2\\missingFile.txt\n" +
                    "\tmissingDir\\missingFile.txt\n" +
                    "\tmissingFile1\n" +
                    "\tmissingFile2\n" +
                    "\n" +
                    "Extra Directories:\n" +
                    "\textraDir\n" +
                    "\textraDir2\n" +
                    "\n" +
                    "Extra Files:\n" +
                    "\textraDir2\\extraFile.txt\n" +
                    "\textraDir\\extraFile.txt\n" +
                    "\n" +
                    "same.txt:\n" +
                    "\tChanged content at line 3:\n" +
                    "\t\tfrom ->\n" +
                    "\t\tline3\n" +
                    "\t\tline4\n" +
                    "\t\tto ->\n" +
                    "\t\tline25\n" +
                    "\n" +
                    "\tMissing content at line 7:\n" +
                    "\t\tmissing\n" +
                    "\n" +
                    "\tInserted content at line 9:\n" +
                    "\t\tline10\n" +
                    "\n" +
                    "same2.txt:\n" +
                    "\tChanged content at line 3:\n" +
                    "\t\tfrom ->\n" +
                    "\t\tline3\n" +
                    "\t\tline4\n" +
                    "\t\tto ->\n" +
                    "\t\tline25\n" +
                    "\n" +
                    "\tMissing content at line 7:\n" +
                    "\t\tmissing\n" +
                    "\n" +
                    "\tInserted content at line 9:\n" +
                    "\t\tline10\n",
            )
        } else {
            assertThat(PrettyPrintDifferences().print(differences)).isEqualTo(
                "Missing Directories:\n" +
                    "\tmissingDir\n" +
                    "\tmissingDir2\n" +
                    "\n" +
                    "Missing Files:\n" +
                    "\tmissingDir/missingFile.txt\n" +
                    "\tmissingDir2/missingFile.txt\n" +
                    "\tmissingFile1\n" +
                    "\tmissingFile2\n" +
                    "\n" +
                    "Extra Directories:\n" +
                    "\textraDir\n" +
                    "\textraDir2\n" +
                    "\n" +
                    "Extra Files:\n" +
                    "\textraDir/extraFile.txt\n" +
                    "\textraDir2/extraFile.txt\n" +
                    "\n" +
                    "same.txt:\n" +
                    "\tChanged content at line 3:\n" +
                    "\t\tfrom ->\n" +
                    "\t\tline3\n" +
                    "\t\tline4\n" +
                    "\t\tto ->\n" +
                    "\t\tline25\n" +
                    "\n" +
                    "\tMissing content at line 7:\n" +
                    "\t\tmissing\n" +
                    "\n" +
                    "\tInserted content at line 9:\n" +
                    "\t\tline10\n" +
                    "\n" +
                    "same2.txt:\n" +
                    "\tChanged content at line 3:\n" +
                    "\t\tfrom ->\n" +
                    "\t\tline3\n" +
                    "\t\tline4\n" +
                    "\t\tto ->\n" +
                    "\t\tline25\n" +
                    "\n" +
                    "\tMissing content at line 7:\n" +
                    "\t\tmissing\n" +
                    "\n" +
                    "\tInserted content at line 9:\n" +
                    "\t\tline10\n",
            )
        }
    }

    @Test
    fun `can throw assertion error when there are differences`() {
        val testResources = File(CompareDirectoriesTreesCommonsIoAndAssetJImplTest::class.java.classLoader.getResource("extraDirectoryFileTest").path)
        val expected = Path("${testResources.absolutePath}${File.separator}dirOriginal")
        val actual = Path("${testResources.absolutePath}${File.separator}dirRevised")

        assertThatExceptionOfType(AssertionError::class.java).isThrownBy {
            AssertDirectories.assertThat(actual).isEqualTo(expected)
        }
    }

    private fun compare(testResources: File): List<Difference> {
        return CompareDirectoriesTreesCommonsIoAndAssetJImpl().compare(
            original = Path("${testResources.absolutePath}${File.separator}dirOriginal"),
            revised = Path("${testResources.absolutePath}${File.separator}dirRevised"),
        )
    }
}
