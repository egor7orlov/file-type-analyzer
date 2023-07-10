package analyzer

import analyzer.fileTypeDetection.FilesTypeDetectorsSpawner
import analyzer.fileTypeDetection.SearchedFileType
import analyzer.substringSearch.SubstringSearchAlgorithm
import java.io.File

fun main(args: Array<String>) {
    val (folderPath, fileTypesDbPath) = args
    val filesPaths = File(folderPath)
        .listFiles()
        ?.map { it.toPath() } ?: throw Exception()
    val fileTypeToTest = SearchedFileType.parseFromDbFile(fileTypesDbPath)

    FilesTypeDetectorsSpawner()
        .forFiles(filesPaths)
        .checkForFileTypes(fileTypeToTest)
        .usingAlgorithm(SubstringSearchAlgorithm.KMP)
        .spawn()
        .await()
}
