package com.codebootup.compare

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import kotlin.io.path.Path

class CompareDirectoriesTreesCommonsIoAndAssetJImplTest {

    @Test
    fun `can detect extra file and directory`(){
        val testResources = CompareDirectoriesTreesCommonsIoAndAssetJImplTest::class.java.classLoader.getResource("extraDirectoryTest")
        val differences = compare(File(testResources.path))

        assertThat(differences).containsExactlyElementsOf(
            listOf(ExtraFile("extraDir${File.separator}dummy1.txt"), ExtraDirectory("extraDir"))
        )
    }

    private fun compare(testResources: File): List<Difference> {
        return CompareDirectoriesTreesCommonsIoAndAssetJImpl().compare(
            original = Path("${testResources.absolutePath}${File.separator}dirOriginal"),
            revised = Path("${testResources.absolutePath}${File.separator}dirRevised"),
        )
    }
}