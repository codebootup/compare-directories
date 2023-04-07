package com.codebootup.compare

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import kotlin.io.path.Path

class CompareDirectoriesTreesCommonsIoAndAssetJImplTest {

    @Test
    fun `can detect extra file and directory`(){
        val differences = compare(File("src/test/resources/extraDirectoryTest"))

        assertThat(differences).containsExactlyElementsOf(
            listOf(ExtraDirectory("extraDir"))
        )
    }

    private fun compare(testResources: File): List<Difference> {
        return CompareDirectoriesTreesCommonsIoAndAssetJImpl().compare(
            original = Path("${testResources.absolutePath}${File.separator}dirOriginal"),
            revised = Path("${testResources.absolutePath}${File.separator}dirRevised"),
        )
    }
}