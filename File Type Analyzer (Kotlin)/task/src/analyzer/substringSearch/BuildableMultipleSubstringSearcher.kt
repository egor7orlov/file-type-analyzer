package analyzer.substringSearch

interface BuildableMultipleSubstringSearcher : BuildableSubstringDetector {
    val occurrences: Array<Int>
}