package com.example.util

data class ParsedReference(
    val collection: String?,
    val hadithNumber: String,
    val canonicalName: String
)

object ReferenceParser {

    private val CANONICAL_NAMES = mapOf(
        "bukhari" to "Sahih al-Bukhari",
        "muslim" to "Sahih Muslim"
    )

    private val RAW_MAP = mapOf(
        // Bukhari
        "bukhari" to "bukhari",
        "sahih bukhari" to "bukhari",
        "sahih al bukhari" to "bukhari",
        "al bukhari" to "bukhari",
        "بخاري" to "bukhari",
        "البخاري" to "bukhari",
        "صحيح بخاري" to "bukhari",
        "صحيح البخاري" to "bukhari",

        // Muslim
        "muslim" to "muslim",
        "sahih muslim" to "muslim",
        "al muslim" to "muslim",
        "مسلم" to "muslim",
        "المسلم" to "muslim",
        "صحيح مسلم" to "muslim"
    )

    private fun sanitizeKey(key: String): String {
        return ArabicNormalizer.normalize(key)
            .lowercase()
            .replace("-", " ")
            .replace("'", "")
            .replace("’", "")
            .replace("`", "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private val SANITIZED_MAP = RAW_MAP.entries.associate { (k, v) ->
        sanitizeKey(k) to v
    }

    fun parse(rawQuery: String): ParsedReference? {
        if (rawQuery.isBlank()) return null

        var query = rawQuery.trim().lowercase()

        // Normalize punctuation
        query = query.replace(":", " ")
            .replace("#", " ")
            .replace("،", " ")
            .replace("-", " ")
            .replace("'", "")
            .replace("’", "")
            .replace("`", "")

        val normalizedQuery = ArabicNormalizer.normalize(query).lowercase().replace(Regex("\\s+"), " ").trim()

        // 1. Pure number query: "1", "13", "224"
        if (query.matches(Regex("^\\d+$"))) {
            return ParsedReference(null, query, "Hadith #$query")
        }

        // 2. "hadith 1" or "حديث 1"
        val hadithNumRegex = Regex("^(?:hadith|no|number|حديث|رقم)\\s*(\\d+)$")
        val hadithNumMatch = hadithNumRegex.matchEntire(normalizedQuery)
        if (hadithNumMatch != null) {
            val num = hadithNumMatch.groupValues[1]
            return ParsedReference(null, num, "Hadith #$num")
        }

        // 3. Collection first: "Sahih Bukhari 1", "40 Nawawi 1", "Bukhari hadith 1"
        val colFirstRegex = Regex("^(.+?)(?:\\s+hadith|\\s+no|\\s+حديث|\\s+رقم)?\\s+(\\d+)$")
        val colFirstMatch = colFirstRegex.matchEntire(normalizedQuery)
        if (colFirstMatch != null) {
            val name = colFirstMatch.groupValues[1].trim()
            val num = colFirstMatch.groupValues[2]
            val collectionKey = matchCollection(name)
            if (collectionKey != null) {
                return ParsedReference(
                    collection = collectionKey,
                    hadithNumber = num,
                    canonicalName = "${CANONICAL_NAMES[collectionKey]} #$num"
                )
            }
        }

        // 4. Number first: "1 bukhari", "8 muslim"
        val numFirstRegex = Regex("^(?:hadith\\s*)?(\\d+)\\s+(?:in\\s+|from\\s+)?([a-z\\sء-ي]+)$")
        val numFirstMatch = numFirstRegex.matchEntire(normalizedQuery)
        if (numFirstMatch != null) {
            val num = numFirstMatch.groupValues[1]
            val name = numFirstMatch.groupValues[2].trim()
            val collectionKey = matchCollection(name)
            if (collectionKey != null) {
                return ParsedReference(
                    collection = collectionKey,
                    hadithNumber = num,
                    canonicalName = "${CANONICAL_NAMES[collectionKey]} #$num"
                )
            }
        }

        return null
    }

    fun matchCollection(name: String): String? {
        val sanitized = sanitizeKey(name)
        SANITIZED_MAP[sanitized]?.let { return it }

        for ((key, col) in SANITIZED_MAP) {
            if (sanitized == key || sanitized.contains(key) || key.contains(sanitized)) {
                return col
            }
        }

        return null
    }

    fun getCanonicalName(collectionKey: String): String {
        return CANONICAL_NAMES[collectionKey] ?: collectionKey.replaceFirstChar { it.uppercase() }
    }
}
