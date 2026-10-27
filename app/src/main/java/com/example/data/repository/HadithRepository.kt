package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.SunnahApiClient
import com.example.data.api.SunnahApiService
import com.example.data.dao.HadithDao
import com.example.data.dao.SearchHistoryDao
import com.example.data.db.SeedData
import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.data.model.HadithEntity
import com.example.data.model.SearchHistoryEntity
import com.example.util.ArabicNormalizer
import com.example.util.ReferenceParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface HadithRepository {
    fun getAllCollections(): Flow<List<CollectionEntity>>
    fun getCollection(name: String): Flow<CollectionEntity?>
    fun getBooksForCollection(collectionName: String): Flow<List<BookEntity>>
    fun getHadithsForBook(collectionName: String, bookNumber: String): Flow<List<HadithEntity>>
    fun getHadithsForCollection(collectionName: String): Flow<List<HadithEntity>>
    fun getHadithById(id: String): Flow<HadithEntity?>
    fun getHadithByNumber(collectionName: String, hadithNumber: String): Flow<HadithEntity?>
    fun getBookmarkedHadiths(): Flow<List<HadithEntity>>
    fun getRecentHistory(): Flow<List<HadithEntity>>
    fun getRandomFeaturedHadith(): Flow<HadithEntity?>
    fun getRecentSearchQueries(): Flow<List<String>>

    fun searchHadiths(
        query: String,
        collectionFilter: String? = null,
        gradeFilter: String? = null,
        languageFilter: String? = null
    ): Flow<List<HadithEntity>>

    suspend fun recordSearchQuery(query: String)
    suspend fun clearSearchHistory()
    suspend fun setBookmark(hadithId: String, isBookmarked: Boolean)
    suspend fun markAsViewed(hadithId: String)
    suspend fun clearCache()
    suspend fun ensureSeeded()

    // Remote API integration
    suspend fun testApiConnection(apiKey: String): Result<String>
    suspend fun fetchFromRemote(collectionName: String, bookNumber: String, apiKey: String): Result<Int>

    // Settings
    fun getApiKey(): String
    fun saveApiKey(key: String)
    fun getArabicFontSize(): Float
    fun saveArabicFontSize(size: Float)
    fun isEnglishEnabled(): Boolean
    fun saveEnglishEnabled(enabled: Boolean)
    fun isUrduEnabled(): Boolean
    fun saveUrduEnabled(enabled: Boolean)
}

class HadithRepositoryImpl(
    private val hadithDao: HadithDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val context: Context,
    private val apiService: SunnahApiService = SunnahApiClient.service
) : HadithRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hadith_library_prefs", Context.MODE_PRIVATE)

    override fun getAllCollections(): Flow<List<CollectionEntity>> = hadithDao.getAllCollections()

    override fun getCollection(name: String): Flow<CollectionEntity?> = hadithDao.getCollection(name)

    override fun getBooksForCollection(collectionName: String): Flow<List<BookEntity>> =
        hadithDao.getBooksForCollection(collectionName)

    override fun getHadithsForBook(collectionName: String, bookNumber: String): Flow<List<HadithEntity>> =
        hadithDao.getHadithsForBook(collectionName, bookNumber)

    override fun getHadithsForCollection(collectionName: String): Flow<List<HadithEntity>> =
        hadithDao.getHadithsForCollection(collectionName)

    override fun getHadithById(id: String): Flow<HadithEntity?> = hadithDao.getHadithById(id)

    override fun getHadithByNumber(collectionName: String, hadithNumber: String): Flow<HadithEntity?> =
        hadithDao.getHadithByNumber(collectionName, hadithNumber)

    override fun getBookmarkedHadiths(): Flow<List<HadithEntity>> = hadithDao.getBookmarkedHadiths()

    override fun getRecentHistory(): Flow<List<HadithEntity>> = hadithDao.getRecentHistory()

    override fun getRandomFeaturedHadith(): Flow<HadithEntity?> = hadithDao.getRandomHadith()

    override fun getRecentSearchQueries(): Flow<List<String>> = searchHistoryDao.getRecentQueries()

    override fun searchHadiths(
        query: String,
        collectionFilter: String?,
        gradeFilter: String?,
        languageFilter: String?
    ): Flow<List<HadithEntity>> {
        val parsedRef = ReferenceParser.parse(query)

        return hadithDao.getAllHadiths().map { allHadiths ->
            allHadiths.filter { hadith ->
                // 1. Collection filter
                if (!collectionFilter.isNullOrBlank() && hadith.collectionName != collectionFilter) {
                    return@filter false
                }

                // 2. Grade filter
                if (!gradeFilter.isNullOrBlank()) {
                    if (!hadith.grade.contains(gradeFilter, ignoreCase = true)) {
                        return@filter false
                    }
                }

                // 3. If empty query, matches filters
                if (query.isBlank()) return@filter true

                // 4. Reference match (e.g. "bukhari 1" or number match)
                if (parsedRef != null) {
                    val matchesCollection = parsedRef.collection == null ||
                            hadith.collectionName.equals(parsedRef.collection, ignoreCase = true)
                    val matchesNumber = parsedRef.hadithNumber == null ||
                            hadith.hadithNumber == parsedRef.hadithNumber
                    if (matchesCollection && matchesNumber) {
                        return@filter true
                    }
                }

                // 5. Textual matching
                val matchesArabic = when (languageFilter) {
                    "en", "ur" -> false
                    else -> ArabicNormalizer.containsNormalized(hadith.arabicText, query) ||
                            ArabicNormalizer.containsNormalized(hadith.chapterTitleArabic, query)
                }

                val matchesEnglish = when (languageFilter) {
                    "ar", "ur" -> false
                    else -> hadith.englishText.contains(query, ignoreCase = true) ||
                            hadith.chapterTitleEnglish.contains(query, ignoreCase = true) ||
                            hadith.narrator.contains(query, ignoreCase = true)
                }

                val matchesUrdu = when (languageFilter) {
                    "ar", "en" -> false
                    else -> hadith.urduText.contains(query, ignoreCase = true) ||
                            hadith.chapterTitleUrdu.contains(query, ignoreCase = true)
                }

                val matchesHadithNumber = hadith.hadithNumber.equals(query.trim(), ignoreCase = true)

                matchesArabic || matchesEnglish || matchesUrdu || matchesHadithNumber
            }
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun recordSearchQuery(query: String) = withContext(Dispatchers.IO) {
        if (query.isNotBlank()) {
            searchHistoryDao.insertQuery(SearchHistoryEntity(query.trim()))
        }
    }

    override suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        searchHistoryDao.clearAll()
    }

    override suspend fun setBookmark(hadithId: String, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        hadithDao.setBookmark(hadithId, isBookmarked)
    }

    override suspend fun markAsViewed(hadithId: String) = withContext(Dispatchers.IO) {
        hadithDao.updateLastViewed(hadithId, System.currentTimeMillis())
    }

    override suspend fun clearCache() = withContext(Dispatchers.IO) {
        hadithDao.clearUnsavedCache()
    }

    override suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val count = hadithDao.getHadithCount()
        if (count == 0) {
            hadithDao.insertCollections(SeedData.collections)
            hadithDao.insertBooks(SeedData.books)
            hadithDao.insertHadiths(SeedData.hadiths)
        }
    }

    override suspend fun testApiConnection(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCollections(apiKey.ifBlank { null })
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                Result.success("Connection successful! Verified ${list.size} collections.")
            } else {
                val code = response.code()
                val message = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("HTTP $code: $message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchFromRemote(
        collectionName: String,
        bookNumber: String,
        apiKey: String
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getHadiths(
                collectionName = collectionName,
                bookNumber = bookNumber,
                page = 1,
                limit = 50,
                apiKey = apiKey.ifBlank { null }
            )
            if (response.isSuccessful && response.body() != null) {
                val items = response.body()!!.data
                val entities = items.mapNotNull { item ->
                    val hadithNum = item.hadithNumber ?: return@mapNotNull null
                    val arText = item.hadith.firstOrNull { it.lang == "ar" }?.body ?: ""
                    val enText = item.hadith.firstOrNull { it.lang == "en" }?.body ?: ""
                    val arChapter = item.hadith.firstOrNull { it.lang == "ar" }?.chapterTitle ?: ""
                    val enChapter = item.hadith.firstOrNull { it.lang == "en" }?.chapterTitle ?: ""
                    val gradeItem = item.grades.firstOrNull()
                    val gradeText = gradeItem?.grade ?: "Verified"
                    val gradedBy = gradeItem?.gradedBy ?: "Sunnah.com"

                    HadithEntity(
                        id = "${collectionName}_$hadithNum",
                        collectionName = collectionName,
                        bookNumber = bookNumber,
                        hadithNumber = hadithNum,
                        inBookReference = "Book $bookNumber, Hadith $hadithNum",
                        arabicText = arText,
                        englishText = enText,
                        urduText = "", // Note: Official Sunnah.com API v1 provides Arabic and English
                        narrator = "",
                        grade = gradeText,
                        gradedBy = gradedBy,
                        chapterTitleArabic = arChapter,
                        chapterTitleEnglish = enChapter,
                        sourceUrl = "https://sunnah.com/$collectionName:$hadithNum",
                        isBookmarked = false
                    )
                }
                hadithDao.insertHadiths(entities)
                Result.success(entities.size)
            } else {
                Result.failure(Exception("Failed to fetch hadiths: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getApiKey(): String = prefs.getString("api_key", "") ?: ""

    override fun saveApiKey(key: String) {
        prefs.edit().putString("api_key", key).apply()
    }

    override fun getArabicFontSize(): Float = prefs.getFloat("arabic_font_size", 24f)

    override fun saveArabicFontSize(size: Float) {
        prefs.edit().putFloat("arabic_font_size", size).apply()
    }

    override fun isEnglishEnabled(): Boolean = prefs.getBoolean("lang_english_enabled", true)

    override fun saveEnglishEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("lang_english_enabled", enabled).apply()
    }

    override fun isUrduEnabled(): Boolean = prefs.getBoolean("lang_urdu_enabled", true)

    override fun saveUrduEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("lang_urdu_enabled", enabled).apply()
    }
}
