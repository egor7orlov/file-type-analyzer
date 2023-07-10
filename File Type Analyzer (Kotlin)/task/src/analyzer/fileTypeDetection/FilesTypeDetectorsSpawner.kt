package analyzer.fileTypeDetection

import analyzer.substringSearch.BuildableSubstringDetector
import analyzer.substringSearch.SubstringDetectorsIssuer
import analyzer.substringSearch.SubstringSearchAlgorithm
import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread

class FilesTypeDetectorsSpawner {
    private lateinit var filesPaths: List<Path>

    private lateinit var algorithm: SubstringSearchAlgorithm

    private lateinit var searchedFileTypes: List<SearchedFileType>

    private val executionThreads = mutableListOf<Thread>()

    fun forFiles(filesPaths: List<Path>): FilesTypeDetectorsSpawner {
        this.filesPaths = filesPaths

        return this
    }

    fun checkForFileTypes(searchedFileTypes: List<SearchedFileType>): FilesTypeDetectorsSpawner {
        // Sorting from highest to lowest priority to avoid redundant substring search attempts.
        // In the worst case scenario it will execute substring search for every element of file types list.
        this.searchedFileTypes = searchedFileTypes.sortedByDescending { it.priority }

        return this
    }

    fun usingAlgorithm(algorithm: SubstringSearchAlgorithm): FilesTypeDetectorsSpawner {
        this.algorithm = algorithm

        return this
    }

    fun spawn(): FilesTypeDetectorsSpawner {
        filesPaths.forEach {
            executionThreads.add(thread {
                val result = FileTypeDetector(
                    filePath = it,
                    searchedFileTypes = searchedFileTypes,
                    substringDetector = SubstringDetectorsIssuer.issueForAlgorithm(algorithm),
                ).execute().result

                println(result)
            })
        }

        return this
    }

    fun await(): FilesTypeDetectorsSpawner {
        executionThreads.forEach { it.join() }

        return this
    }
}

private class FileTypeDetector(
    private val filePath: Path,
    private val searchedFileTypes: List<SearchedFileType>,
    private val substringDetector: BuildableSubstringDetector
) {
    private lateinit var detectedFileType: String

    val result: String
        get() = "${filePath.fileName}: $detectedFileType"

    fun execute(): FileTypeDetector {
        var detectedTypeWithHighestPriority: SearchedFileType? = null

        for (fileType in searchedFileTypes) {
            if (
                detectedTypeWithHighestPriority != null
                && detectedTypeWithHighestPriority.priority > fileType.priority
            ) {
                continue
            }

            val fileContents = Files.readString(filePath.toAbsolutePath())
            val isPatternSubstring = substringDetector
                .inText(fileContents)
                .searchForPattern(fileType.pattern)
                .isPatternSubstring

            if (isPatternSubstring) {
                detectedTypeWithHighestPriority = fileType
            }
        }


        detectedFileType = detectedTypeWithHighestPriority?.name ?: "Unknown file type"

        return this
    }
}