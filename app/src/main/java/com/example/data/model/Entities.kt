package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object SourceType {
    const val CANONICAL_VERIFIED = "CANONICAL_VERIFIED"
    const val API_SYNCED = "API_SYNCED"
    const val SAMPLE_DEMO = "SAMPLE_DEMO"
}

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val name: String, // "bukhari", "muslim"
    val titleEnglish: String,
    val titleArabic: String,
    val titleUrdu: String,
    val author: String,
    val shortIntro: String,
    val totalHadith: Int,
    val hasBooks: Boolean = true,
    val availableLanguages: String = "ar,en,ur" // comma-separated
)

@Entity(
    tableName = "books",
    indices = [Index(value = ["collectionName", "bookNumber"])]
)
data class BookEntity(
    @PrimaryKey val id: String, // e.g. "bukhari_1"
    val collectionName: String,
    val bookNumber: String,
    val titleEnglish: String,
    val titleArabic: String,
    val titleUrdu: String,
    val numberOfHadith: Int
)

@Entity(
    tableName = "hadiths",
    indices = [
        Index(value = ["collectionName", "hadithNumber"]),
        Index(value = ["collectionName", "bookNumber"]),
        Index(value = ["isBookmarked"]),
        Index(value = ["lastViewedTimestamp"]),
        Index(value = ["sourceType"])
    ]
)
data class HadithEntity(
    @PrimaryKey val id: String, // e.g. "bukhari_1"
    val collectionName: String,
    val bookNumber: String,
    val hadithNumber: String,
    val inBookReference: String,
    val arabicText: String,
    val englishText: String,
    val urduText: String,
    val narrator: String,
    val grade: String, // "Sahih", "Hasan", "Da'if", "Not Specified"
    val gradedBy: String, // Scholar / source of grading
    val chapterTitleArabic: String = "",
    val chapterTitleEnglish: String = "",
    val chapterTitleUrdu: String = "",
    val sourceUrl: String,
    val isBookmarked: Boolean = false,
    val lastViewedTimestamp: Long? = null,
    val sourceType: String = SourceType.CANONICAL_VERIFIED,
    val isOfflineCached: Boolean = true
) {
    val hasArabic: Boolean get() = arabicText.isNotBlank()
    val hasEnglish: Boolean get() = englishText.isNotBlank()
    val hasUrdu: Boolean get() = urduText.isNotBlank()
}

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
