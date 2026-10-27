package com.example.util

data class ParsedReference(
    val collection: String?,
    val hadithNumber: String?
)

object ReferenceParser {

    private val COLLECTION_MAP = mapOf(
        "bukhari" to "bukhari",
        "sahih bukhari" to "bukhari",
        "sahih al-bukhari" to "bukhari",
        "بخاري" to "bukhari",
        "صحيح البخاري" to "bukhari",
        "muslim" to "muslim",
        "sahih muslim" to "muslim",
        "مسلم" to "muslim",
        "صحيح مسلم" to "muslim",
        "dawud" to "abudawud",
        "abi dawud" to "abudawud",
        "abu dawud" to "abudawud",
        "ابو داود" to "abudawud",
        "tirmidhi" to "tirmidhi",
        "al-tirmidhi" to "tirmidhi",
        "ترمذي" to "tirmidhi",
        "الترمذي" to "tirmidhi",
        "nasai" to "nasai",
        "an-nasai" to "nasai",
        "نسائي" to "nasai",
        "النسائي" to "nasai",
        "ibn majah" to "ibnmajah",
        "ibnmajah" to "ibnmajah",
        "ابن ماجه" to "ibnmajah",
        "malik" to "malik",
        "muwatta" to "malik",
        "موطأ" to "malik",
        "ahmad" to "ahmad",
        "musnad ahmad" to "ahmad",
        "احمد" to "ahmad"
    )

    /**
     * Attempts to parse queries like "bukhari 1", "muslim:8", "bukhari:13", or "hadith 224"
     */
    fun parse(query: String): ParsedReference? {
        val clean = query.trim().lowercase()

        // Match pattern: collection [:]? number
        val regex = Regex("^([a-zA-Z\\s\\-ء-ي]+?)[\\s:]+(\\d+)$")
        val match = regex.matchEntire(clean)
        if (match != null) {
            val namePart = match.groupValues[1].trim()
            val numberPart = match.groupValues[2].trim()

            val normalizedName = ArabicNormalizer.normalize(namePart).lowercase()
            val matchedCollection = COLLECTION_MAP[namePart] ?: COLLECTION_MAP[normalizedName]
            if (matchedCollection != null) {
                return ParsedReference(matchedCollection, numberPart)
            }
            if (namePart == "hadith" || namePart == "حديث") {
                return ParsedReference(null, numberPart)
            }
        }

        // Just a number check
        if (clean.matches(Regex("^\\d+$"))) {
            return ParsedReference(null, clean)
        }

        return null
    }
}
