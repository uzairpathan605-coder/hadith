package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.adapter.FawazAhmedSourceAdapter
import com.example.data.adapter.HadithSourceAdapter
import com.example.data.adapter.SunnahComSourceAdapter
import com.example.data.dao.HadithDao
import com.example.data.dao.SearchHistoryDao
import com.example.data.db.SeedData
import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.data.model.HadithEntity
import com.example.data.model.SearchHistoryEntity
import com.example.data.sync.DatabaseIntegrityReport
import com.example.data.sync.PackDownloadState
import com.example.data.sync.PackDownloader
import com.example.util.ArabicNormalizer
import com.example.util.ParsedReference
import com.example.util.ReferenceParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class DatabaseStats(
    val totalHadiths: Int = 0,
    val bukhariCount: Int = 0,
    val muslimCount: Int = 0,
    val arabicCount: Int = 0,
    val englishCount: Int = 0,
    val urduCount: Int = 0
)

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
    fun getDatabaseStats(): Flow<DatabaseStats>

    fun searchHadiths(
        query: String,
        collectionFilter: String? = null,
        gradeFilter: String? = null,
        languageFilter: String? = null
    ): Flow<List<HadithEntity>>

    suspend fun fetchDirectReferenceIfMissing(parsed: ParsedReference): HadithEntity?

    suspend fun recordSearchQuery(query: String)
    suspend fun clearSearchHistory()
    suspend fun setBookmark(hadithId: String, isBookmarked: Boolean)
    suspend fun markAsViewed(hadithId: String)
    suspend fun clearCache()
    suspend fun ensureSeeded()

    // Pack Downloader & Integrity
    val packDownloadState: StateFlow<PackDownloadState>
    fun getHadithCountForCollection(collectionName: String): Flow<Int>
    suspend fun downloadCollectionPack(collectionName: String): Result<Int>
    suspend fun downloadBook(collectionName: String, bookNumber: String): Result<Int>
    suspend fun downloadAllCollections(): Result<Int>
    suspend fun retryLastDownload(): Result<Int>
    suspend fun checkDatabaseIntegrity(): DatabaseIntegrityReport
    fun resetPackDownloadState()

    // Settings
    fun getArabicFontSize(): Float
    fun saveArabicFontSize(size: Float)
    fun isEnglishEnabled(): Boolean
    fun saveEnglishEnabled(enabled: Boolean)
    fun isUrduEnabled(): Boolean
    fun saveUrduEnabled(enabled: Boolean)
    fun hasSeenOnboarding(): Boolean
    fun setSeenOnboarding(seen: Boolean)
    fun getApiKey(): String
    fun saveApiKey(key: String)
    suspend fun testApiConnection(key: String): Result<String>

    // Admin Security
    fun isAdminPasscodeConfigured(): Boolean
    fun setAdminPasscode(passcode: String)
    fun verifyAdminPasscode(passcode: String): Boolean
}

class HadithRepositoryImpl(
    private val hadithDao: HadithDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val context: Context,
    private val primaryAdapter: HadithSourceAdapter = FawazAhmedSourceAdapter(),
    private val sunnahAdapter: HadithSourceAdapter = SunnahComSourceAdapter(),
    private val packDownloader: PackDownloader = PackDownloader(hadithDao, primaryAdapter)
) : HadithRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("siraj_al_huda_prefs", Context.MODE_PRIVATE)

    override val packDownloadState: StateFlow<PackDownloadState> = packDownloader.downloadState

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

    override fun getDatabaseStats(): Flow<DatabaseStats> {
        return combine(
            listOf(
                hadithDao.getTotalHadithCountFlow(),
                hadithDao.getHadithCountForCollection("bukhari"),
                hadithDao.getHadithCountForCollection("muslim"),
                hadithDao.getArabicHadithCountFlow(),
                hadithDao.getEnglishHadithCountFlow(),
                hadithDao.getUrduHadithCountFlow()
            )
        ) { values ->
            val total = values[0]
            val bukhari = values[1]
            val muslim = values[2]
            val arabic = values[3]
            val english = values[4]
            val urdu = values[5]
            DatabaseStats(
                totalHadiths = total,
                bukhariCount = bukhari,
                muslimCount = muslim,
                arabicCount = arabic,
                englishCount = english,
                urduCount = urdu
            )
        }.flowOn(Dispatchers.Default)
    }

    override fun searchHadiths(
        query: String,
        collectionFilter: String?,
        gradeFilter: String?,
        languageFilter: String?
    ): Flow<List<HadithEntity>> {
        val trimmedQuery = query.trim()
        val parsedRef = ReferenceParser.parse(trimmedQuery)
        val matchedCollectionKey = ReferenceParser.matchCollection(trimmedQuery)
        val numericDigits = trimmedQuery.filter { it.isDigit() }

        return hadithDao.getAllHadiths().map { allHadiths ->
            allHadiths.filter { hadith ->
                // 1. Collection filter
                if (!collectionFilter.isNullOrBlank() && !hadith.collectionName.equals(collectionFilter, ignoreCase = true)) {
                    return@filter false
                }

                // 2. Grade filter
                if (!gradeFilter.isNullOrBlank()) {
                    if (!hadith.grade.contains(gradeFilter, ignoreCase = true)) {
                        return@filter false
                    }
                }

                // 3. If empty query, matches filters
                if (trimmedQuery.isBlank()) return@filter true

                // 4. Exact Reference match (e.g. "Sahih Bukhari 1" or "Muslim 1900" or "Bukhari 52")
                if (parsedRef != null) {
                    val matchesCollection = parsedRef.collection == null ||
                            hadith.collectionName.equals(parsedRef.collection, ignoreCase = true)
                    val matchesExactNumber = hadith.hadithNumber == parsedRef.hadithNumber
                    val matchesPartialNumber = hadith.hadithNumber.startsWith(parsedRef.hadithNumber)
                    if (matchesCollection && (matchesExactNumber || matchesPartialNumber)) {
                        return@filter true
                    }
                }

                // 5. Query matching collection name directly (e.g. "Bukhari", "Muslim", "بخاري")
                if (matchedCollectionKey != null && hadith.collectionName.equals(matchedCollectionKey, ignoreCase = true)) {
                    return@filter true
                }

                // 6. Partial or exact Hadith number
                if (numericDigits.isNotEmpty() && hadith.hadithNumber.startsWith(numericDigits)) {
                    return@filter true
                }

                // 7. Textual matching across Arabic, English, Urdu, Narrators, and Chapters
                val matchesArabic = when (languageFilter) {
                    "en", "ur" -> false
                    else -> ArabicNormalizer.containsNormalized(hadith.arabicText, trimmedQuery) ||
                            ArabicNormalizer.containsNormalized(hadith.chapterTitleArabic, trimmedQuery)
                }

                val matchesEnglish = when (languageFilter) {
                    "ar", "ur" -> false
                    else -> hadith.englishText.contains(trimmedQuery, ignoreCase = true) ||
                            hadith.chapterTitleEnglish.contains(trimmedQuery, ignoreCase = true) ||
                            hadith.narrator.contains(trimmedQuery, ignoreCase = true) ||
                            hadith.collectionName.contains(trimmedQuery, ignoreCase = true)
                }

                val matchesUrdu = when (languageFilter) {
                    "ar", "en" -> false
                    else -> hadith.urduText.contains(trimmedQuery, ignoreCase = true) ||
                            hadith.chapterTitleUrdu.contains(trimmedQuery, ignoreCase = true)
                }

                matchesArabic || matchesEnglish || matchesUrdu
            }.sortedWith(
                compareByDescending<HadithEntity> { hadith ->
                    calculateSearchScore(hadith, trimmedQuery, parsedRef, matchedCollectionKey, numericDigits)
                }.thenBy {
                    it.hadithNumber.toIntOrNull() ?: 999999
                }
            )
        }.flowOn(Dispatchers.Default)
    }

    private fun calculateSearchScore(
        hadith: HadithEntity,
        query: String,
        parsedRef: ParsedReference?,
        matchedCollectionKey: String?,
        numericDigits: String
    ): Int {
        if (query.isBlank()) return 0

        // 1. Exact Collection + Exact Hadith Number match (Top Priority)
        if (parsedRef != null && parsedRef.collection != null &&
            hadith.collectionName.equals(parsedRef.collection, ignoreCase = true) &&
            hadith.hadithNumber == parsedRef.hadithNumber
        ) {
            return 1000
        }

        // 2. Exact Hadith Number match across collections
        if (parsedRef != null && parsedRef.collection == null && hadith.hadithNumber == parsedRef.hadithNumber) {
            return 900
        }
        if (hadith.hadithNumber == query) {
            return 880
        }

        // 3. Collection + Partial Hadith Number match (e.g. "Bukhari 5" matching #52)
        if (parsedRef != null && parsedRef.collection != null &&
            hadith.collectionName.equals(parsedRef.collection, ignoreCase = true) &&
            hadith.hadithNumber.startsWith(parsedRef.hadithNumber)
        ) {
            return 800
        }

        // 4. Collection name match (e.g. "Bukhari", "Muslim", "مسلم")
        if (matchedCollectionKey != null && hadith.collectionName.equals(matchedCollectionKey, ignoreCase = true)) {
            return 750
        }

        // 5. Partial Hadith Number (e.g. "190" matching 1900)
        if (numericDigits.isNotEmpty() && hadith.hadithNumber.startsWith(numericDigits)) {
            return 700
        }

        // 6. Arabic Matn match (diacritic-normalized)
        if (ArabicNormalizer.containsNormalized(hadith.arabicText, query)) {
            return 600
        }

        // 6. English Matn or Narrator match
        if (hadith.englishText.contains(query, ignoreCase = true)) {
            return 500
        }
        if (hadith.narrator.contains(query, ignoreCase = true)) {
            return 450
        }

        // 7. Urdu Matn match
        if (hadith.urduText.contains(query, ignoreCase = true)) {
            return 400
        }

        // 8. Chapter Title match
        if (hadith.chapterTitleEnglish.contains(query, ignoreCase = true) ||
            ArabicNormalizer.containsNormalized(hadith.chapterTitleArabic, query) ||
            hadith.chapterTitleUrdu.contains(query, ignoreCase = true)
        ) {
            return 300
        }

        return 100
    }

    override suspend fun fetchDirectReferenceIfMissing(parsed: ParsedReference): HadithEntity? = withContext(Dispatchers.IO) {
        val col = parsed.collection ?: return@withContext null
        val num = parsed.hadithNumber

        // Check Room first
        val existing = hadithDao.findHadithDirect(col, num)
        if (existing != null) {
            return@withContext existing
        }

        // Fetch via primary free adapter
        val primaryResult = primaryAdapter.fetchHadith(col, num)
        if (primaryResult.isSuccess) {
            val record = primaryResult.getOrNull()
            if (record != null) {
                hadithDao.insertHadith(record)
                return@withContext record
            }
        }

        null
    }

    override fun getHadithCountForCollection(collectionName: String): Flow<Int> {
        return hadithDao.getHadithCountForCollection(collectionName)
    }

    override suspend fun downloadCollectionPack(collectionName: String): Result<Int> {
        return packDownloader.downloadCollectionPack(collectionName)
    }

    override suspend fun downloadBook(collectionName: String, bookNumber: String): Result<Int> {
        return packDownloader.downloadBook(collectionName, bookNumber)
    }

    override suspend fun downloadAllCollections(): Result<Int> {
        val collections = listOf("bukhari", "muslim")
        return packDownloader.downloadAllCollections(collections)
    }

    override suspend fun retryLastDownload(): Result<Int> {
        return packDownloader.retryLastDownload()
    }

    override suspend fun checkDatabaseIntegrity(): DatabaseIntegrityReport {
        return packDownloader.checkIntegrity()
    }

    override fun resetPackDownloadState() {
        packDownloader.resetState()
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
        if (count < SeedData.hadiths.size) {
            hadithDao.insertCollections(SeedData.collections)
            hadithDao.insertBooks(SeedData.books)
            hadithDao.insertHadiths(SeedData.hadiths)
        }
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

    override fun hasSeenOnboarding(): Boolean = prefs.getBoolean("seen_onboarding", false)

    override fun setSeenOnboarding(seen: Boolean) {
        prefs.edit().putBoolean("seen_onboarding", seen).apply()
    }

    override fun getApiKey(): String = prefs.getString("sunnah_api_key", "") ?: ""

    override fun saveApiKey(key: String) {
        prefs.edit().putString("sunnah_api_key", key.trim()).apply()
    }

    override suspend fun testApiConnection(key: String): Result<String> = withContext(Dispatchers.IO) {
        if (key.isBlank()) return@withContext Result.failure(IllegalArgumentException("Please enter a valid API key"))
        val result = sunnahAdapter.fetchHadith("bukhari", "1", key)
        if (result.isSuccess) {
            Result.success("Authenticated with Sunnah.com API successfully!")
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("API validation failed"))
        }
    }

    private fun hashPasscode(passcode: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(passcode.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override fun isAdminPasscodeConfigured(): Boolean {
        return prefs.contains("admin_passcode_hash")
    }

    override fun setAdminPasscode(passcode: String) {
        val hash = hashPasscode(passcode)
        prefs.edit().putString("admin_passcode_hash", hash).apply()
    }

    override fun verifyAdminPasscode(passcode: String): Boolean {
        val stored = prefs.getString("admin_passcode_hash", null) ?: return false
        return stored == hashPasscode(passcode)
    }
}
