package analyzer.substringSearch

interface BuildableSubstringDetector {
    val isPatternSubstring: Boolean

    fun inText(text: String): BuildableSubstringDetector

    fun searchForPattern(pattern: String): BuildableSubstringDetector
}