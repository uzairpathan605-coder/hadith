package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val name: String, // e.g. "bukhari", "muslim", "abudawud", "tirmidhi", "nasai", "ibnmajah", "malik", "ahmad"
    val titleEnglish: String,
    val titleArabic: String,
    val titleUrdu: String,
    val author: String,
    val shortIntro: String,
    val totalHadith: Int,
    val hasBooks: Boolean = true
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
        Index(value = ["lastViewedTimestamp"])
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
    val grade: String, // "Sahih", "Hasan", "Da'if"
    val gradedBy: String, // Scholar / source of grading
    val chapterTitleArabic: String = "",
    val chapterTitleEnglish: String = "",
    val chapterTitleUrdu: String = "",
    val sourceUrl: String,
    val isBookmarked: Boolean = false,
    val lastViewedTimestamp: Long? = null
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
