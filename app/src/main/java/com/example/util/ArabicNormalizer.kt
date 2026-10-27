package com.example.util

object ArabicNormalizer {

    private val DIACRITICS_REGEX = Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]")
    private val TATWEEL_REGEX = Regex("\\u0640")

    /**
     * Normalizes an Arabic string by stripping diacritics, unifying Alef variations,
     * simplifying Teh Marbuta, and standardizing Yeh/Alef Maqsura.
     */
    fun normalize(input: String): String {
        if (input.isBlank()) return ""

        var text = input

        // 1. Remove tashkeel/diacritics and tatweel
        text = text.replace(DIACRITICS_REGEX, "")
        text = text.replace(TATWEEL_REGEX, "")

        // 2. Normalize Alef variants: أ, إ, آ, ٱ -> ا
        text = text.replace(Regex("[\\u0622\\u0623\\u0625\\u0671]"), "\u0627")

        // 3. Normalize Teh Marbuta: ة -> ه
        text = text.replace('\u0629', '\u0647')

        // 4. Normalize Alef Maqsura: ى -> ي
        text = text.replace('\u0649', '\u064A')

        // 5. Trim and collapse multiple spaces
        text = text.replace(Regex("\\s+"), " ").trim()

        return text
    }

    /**
     * Case-insensitive, diacritic-insensitive, normalized substring check.
     */
    fun containsNormalized(source: String, query: String): Boolean {
        if (query.isBlank()) return true
        val normalizedSource = normalize(source).lowercase()
        val normalizedQuery = normalize(query).lowercase()
        return normalizedSource.contains(normalizedQuery)
    }
}
