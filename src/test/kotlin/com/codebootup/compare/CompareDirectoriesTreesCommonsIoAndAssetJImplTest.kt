package com.codebootup.compare

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import kotlin.io.path.Path

class CompareDirectoriesTreesCommonsIoAndAssetJImplTest {

    @Test
    fun `CompareDirectoriesTreesCommonsIoAndAssetJImplTest is subclass of CompareDirectories`(){
        assertThat(CompareDirectoriesTreesCommonsIoAndAssetJImpl()).isInstanceOf(CompareDirectories::class.java)
    }
    @Test
    fun `can detect extra file and directory`(){
        val testResources = CompareDirectoriesTreesCommonsIoAndAssetJImplTest::class.java.classLoader.getResource("extraDirectoryFileTest")
        val differences = compare(File(testResources.path))

        assertThat(differences).containsExactlyElementsOf(
            listOf(ExtraFile("extraDir${File.separator}extraFile.txt"), ExtraDirectory("extraDir"))
        )
    }

    @Test
    fun `can detect missing file and directory`(){
        val testResources = CompareDirectoriesTreesCommonsIoAndAssetJImplTest::class.java.classLoader.getResource("missingDirectoryFileTest")
        val differences = compare(File(testResources.path))

        assertThat(differences).containsExactlyElementsOf(
            listOf(MissingFile("missingDir${File.separator}missingFile.txt"), MissingDirectory("missingDir"))
        )
    }

    private fun compare(testResources: File): List<Difference> {
        return CompareDirectoriesTreesCommonsIoAndAssetJImpl().compare(
            original = Path("${testResources.absolutePath}${File.separator}dirOriginal"),
            revised = Path("${testResources.absolutePath}${File.separator}dirRevised"),
        )
    }
}