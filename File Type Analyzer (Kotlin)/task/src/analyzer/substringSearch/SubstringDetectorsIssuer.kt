package analyzer.substringSearch

object SubstringDetectorsIssuer {
    fun issueForAlgorithm(algorithm: SubstringSearchAlgorithm): BuildableSubstringDetector {
        return when (algorithm) {
            SubstringSearchAlgorithm.KMP -> KMPSubstringSearcher()
        }
    }
}