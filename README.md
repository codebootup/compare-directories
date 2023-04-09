# compare-directories
Main use case is for test assertions where you want to compare two directories to identify any differences in tree 
structure or file content.  We have built this to facilitate our code generating testing but have split it out as 
we feel it is a useful utility by itself.

## Assert Differences
**Kotlin**
```
val expected = Path("src/test/resources/extraDirectoryFileTest/dirOriginal")
val actual = Path("src/test/resources/extraDirectoryFileTest/dirRevised")

AssertDirectories.assertThat(actual).isEqualTo(expected)
```
**Java**
```
final Path expected = Paths.get("src/test/resources/extraDirectoryFileTest/dirOriginal");
final Path actual = Paths.get("src/test/resources/extraDirectoryFileTest/dirRevised");

AssertDirectories.Companion.assertThat(actual).isEqualTo(original);
```

## Pretty Print Differences
It can also be used outside a test use case for example this is how to simply use it with java and kotlin

**Kotlin**
```
val compareDirectories: CompareDirectories = CompareDirectoriesTreesCommonsIoAndAssetJImpl()
val differences = compareDirectories.compare(
    Paths.get("src/test/resources/extraDirectoryFileTest/dirOriginal"),
    Paths.get("src/test/resources/extraDirectoryFileTest/dirRevised")
)
PrettyPrintDifferences().print(differences)
```
**Java**
```
final CompareDirectories cd = new CompareDirectoriesTreesCommonsIoAndAssetJImpl();
final Path actual = Paths.get("src/test/resources/extraDirectoryFileTest/dirRevised");
final Path original = Paths.get("src/test/resources/extraDirectoryFileTest/dirOriginal");
final List<Difference> differences = cd.compare(
        original,
        actual
);
new PrettyPrintDifferences().print(differences);
```

## Model
Digging deeper there is a rich difference model that strongly types each type of difference into:

* **ContentDifference** - Encapsulates the differences in a files content between the original and the revised
* **MissingDirectory** - Directory no longer present in the revised directory tree
* **MissingFile** - File no longer present in the revised directory tree
* **ExtraDirectory** - New directory found in the revised tree
* **ExtraFile** - New file found in the revised tree
