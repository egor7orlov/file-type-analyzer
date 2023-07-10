package analyzer.substringSearch

import java.lang.Exception

internal class KMPSubstringSearcher : BuildableMultipleSubstringSearcher {
    private lateinit var text: String

    private lateinit var pattern: String

    private lateinit var patternPrefixFunction: Array<Int>

    private lateinit var _occurrences: Array<Int>

    override val occurrences: Array<Int>
        get() = this._occurrences

    override val isPatternSubstring: Boolean
        get() {
            return this.occurrences.isNotEmpty()
        }

    override fun inText(text: String): KMPSubstringSearcher {
        this.text = text

        return this
    }

    override fun searchForPattern(pattern: String): KMPSubstringSearcher {
        this.pattern = pattern
        this.patternPrefixFunction = this.calculatePrefixFunctionForPattern(pattern)

        search()

        return this
    }

    private fun calculatePrefixFunctionForPattern(pattern: String): Array<Int> {
        val prefixFunction = Array(pattern.length) { 0 }

        for (i in pattern.indices) {
            var maxPrefixValue = 0
            val iterationSubstring = pattern.substring(0..i)

            for (j in iterationSubstring.indices.drop(1)) {
                val prefix = iterationSubstring.substring(0 until j)
                val suffix = iterationSubstring.substring(iterationSubstring.length - j)

                if (prefix == suffix) {
                    maxPrefixValue = prefix.length
                }
            }

            prefixFunction[i] = maxPrefixValue
        }

        return prefixFunction
    }

    private fun search(): KMPSubstringSearcher {
        if (!this::text.isInitialized || !this::pattern.isInitialized || !this::patternPrefixFunction.isInitialized) {
            throw Exception("KMP substring searcher's configuration is not completed")
        }

        val occurrences = mutableListOf<Int>()
        var i = 0

        while (i < text.length) {
            if (text.length - i < pattern.length) {
                break
            }

            var matchingCharsCount = 0

            for (j in pattern.indices) {
                val charFromText = text[j + i]
                val charFromPattern = pattern[j]

                if (charFromText != charFromPattern) {
                    break
                }

                matchingCharsCount++
            }

            if (matchingCharsCount == 0) {
                i++
                continue
            }

            if (matchingCharsCount == pattern.length) {
                occurrences.add(i)
                i++
            } else {
                i += matchingCharsCount - patternPrefixFunction[matchingCharsCount - 1]
            }
        }

        this._occurrences = occurrences.toTypedArray()

        return this
    }
}
