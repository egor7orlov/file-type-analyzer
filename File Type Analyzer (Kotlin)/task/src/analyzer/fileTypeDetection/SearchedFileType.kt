package analyzer.fileTypeDetection

import java.io.File

data class SearchedFileType(
    val priority: Int,
    val pattern: String,
    val name: String
) {
    companion object {
        fun parseFromDbFile(dbFilePath: String): List<SearchedFileType> {
            return File(dbFilePath).readLines().map {
                val (priority, pattern, name) = it.split(";")

                SearchedFileType(
                    priority = priority.toInt(),
                    pattern = pattern.removeSurrounding("\""),
                    name = name.removeSurrounding("\"")
                )
            }
        }
    }
}