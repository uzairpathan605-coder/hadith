package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.dao.HadithDao
import com.example.data.dao.SearchHistoryDao
import com.example.data.db.HadithDatabase
import com.example.data.db.SeedData
import com.example.data.model.HadithEntity
import com.example.data.model.SourceType
import com.example.data.repository.HadithRepository
import com.example.data.repository.HadithRepositoryImpl
import com.example.util.ArabicNormalizer
import com.example.util.ReferenceParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SmartSearchAndDownloadCUJTest {

    private lateinit var database: HadithDatabase
    private lateinit var hadithDao: HadithDao
    private lateinit var searchHistoryDao: SearchHistoryDao
    private lateinit var repository: HadithRepository

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HadithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        hadithDao = database.hadithDao()
        searchHistoryDao = database.searchHistoryDao()
        repository = HadithRepositoryImpl(hadithDao, searchHistoryDao, context)

        // Seed with canonical collections, books, and verified hadiths
        hadithDao.insertCollections(SeedData.collections)
        hadithDao.insertBooks(SeedData.books)
        hadithDao.insertHadiths(SeedData.hadiths)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testSeededRecordCompletenessAndAuthenticity() = runBlocking {
        val count = hadithDao.getHadithCount()
        assertTrue("Database should have seeded authentic hadiths", count >= 13)

        val all = hadithDao.getAllHadiths().first()
        all.forEach { hadith ->
            assertTrue("Hadith ${hadith.id} must have non-blank Arabic or English matn", hadith.hasArabic || hadith.hasEnglish)
            assertTrue("Hadith ${hadith.id} must have verified source attribution", hadith.sourceUrl.isNotBlank())
            assertEquals("Hadith ${hadith.id} must be canonical verified", SourceType.CANONICAL_VERIFIED, hadith.sourceType)
        }
    }

    @Test
    fun testExactReferenceSearchBukhariOne() = runBlocking {
        val results = repository.searchHadiths("Bukhari 1").first()
        assertFalse("Search for 'Bukhari 1' should return results", results.isEmpty())
        val top = results.first()
        assertEquals("bukhari_1", top.id)
        assertEquals("1", top.hadithNumber)
        assertEquals("bukhari", top.collectionName)
        assertTrue(top.arabicText.contains("إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ"))
    }

    @Test
    fun testExactReferenceSearchBukhariFiftyTwo() = runBlocking {
        val results = repository.searchHadiths("Bukhari 52").first()
        assertFalse("Search for 'Bukhari 52' should return results", results.isEmpty())
        val top = results.first()
        assertEquals("bukhari_52", top.id)
        assertEquals("52", top.hadithNumber)
        assertEquals("bukhari", top.collectionName)
        assertTrue(top.englishText.contains("doubtful matters", ignoreCase = true))
    }

    @Test
    fun testExactReferenceSearchSahihMuslimNineteenHundred() = runBlocking {
        val results = repository.searchHadiths("Sahih Muslim 1900").first()
        assertFalse("Search for 'Sahih Muslim 1900' should return results", results.isEmpty())
        val top = results.first()
        assertEquals("muslim_1900", top.id)
        assertEquals("1900", top.hadithNumber)
        assertEquals("muslim", top.collectionName)
        assertTrue(top.arabicText.contains("مَنْ دَلَّ عَلَى خَيْرٍ"))
        assertTrue(top.englishText.contains("Whoever guides someone to goodness", ignoreCase = true))
        assertTrue(top.urduText.contains("جس نے کسی نیکی کی رہنمائی کی"))
    }

    @Test
    fun testPartialCollectionNameSearch() = runBlocking {
        // Querying "bukhari" should rank Bukhari hadiths at the top
        val bukhariResults = repository.searchHadiths("bukhari").first()
        assertTrue("Query 'bukhari' should return results", bukhariResults.isNotEmpty())
        assertEquals("Top result for 'bukhari' must belong to bukhari collection", "bukhari", bukhariResults.first().collectionName)
        assertTrue(bukhariResults.any { it.collectionName == "bukhari" })

        // Querying with explicit collectionFilter
        val filteredBukhari = repository.searchHadiths("bukhari", collectionFilter = "bukhari").first()
        assertTrue(filteredBukhari.isNotEmpty())
        filteredBukhari.forEach {
            assertEquals("bukhari", it.collectionName)
        }

        // Querying "muslim" should rank Muslim hadiths at the top
        val muslimResults = repository.searchHadiths("muslim").first()
        assertTrue("Query 'muslim' should return results", muslimResults.isNotEmpty())
        assertEquals("Top result for 'muslim' must belong to muslim collection", "muslim", muslimResults.first().collectionName)
        assertTrue(muslimResults.any { it.collectionName == "muslim" })
    }

    @Test
    fun testPartialNumberSearch() = runBlocking {
        // Querying "190" should find 1900 and 1907
        val results = repository.searchHadiths("190").first()
        assertTrue("Query '190' should return partial matches", results.size >= 2)
        val numbers = results.map { it.hadithNumber }
        assertTrue(numbers.contains("1900"))
        assertTrue(numbers.contains("1907"))
    }

    @Test
    fun testArabicDiacriticInsensitiveSearch() = runBlocking {
        // Query without tashkeel
        val queryNoDiacritics = "انما الاعمال بالنيات"
        val results = repository.searchHadiths(queryNoDiacritics).first()
        assertTrue("Diacritic-insensitive search should return Bukhari 1", results.any { it.id == "bukhari_1" })
    }

    @Test
    fun testUrduFullTextSearch() = runBlocking {
        val results = repository.searchHadiths("نیکی").first()
        assertTrue("Urdu search for 'نیکی' should find Muslim 1900", results.any { it.id == "muslim_1900" })
    }

    @Test
    fun testEnglishFullTextSearch() = runBlocking {
        val results = repository.searchHadiths("intentions").first()
        assertTrue("English search for 'intentions' should find Bukhari 1 and Nawawi 1", results.any { it.id == "bukhari_1" })
    }

    @Test
    fun testSearchRankingExactRefRanksAboveOthers() = runBlocking {
        // "bukhari 1" should put bukhari_1 at index 0
        val results = repository.searchHadiths("bukhari 1").first()
        assertEquals("Top result must be exact match bukhari_1", "bukhari_1", results.first().id)

        // "muslim 1900" should put muslim_1900 at index 0
        val resultsMuslim = repository.searchHadiths("muslim 1900").first()
        assertEquals("Top result must be exact match muslim_1900", "muslim_1900", resultsMuslim.first().id)
    }

    @Test
    fun testOfflineDatabaseIntegrityReport() = runBlocking {
        val report = repository.checkDatabaseIntegrity()
        assertTrue("Database integrity should be valid", report.isValid)
        assertTrue("Total hadiths should be >= 13", report.totalHadiths >= 13)
        assertTrue("Collection breakdown should include bukhari and muslim", report.collectionBreakdown.containsKey("Bukhari"))
        assertTrue("Collection breakdown should include muslim", report.collectionBreakdown.containsKey("Muslim"))
    }

    @Test
    fun testSearchHistoryTracking() = runBlocking {
        repository.recordSearchQuery("Bukhari 52")
        repository.recordSearchQuery("Sahih Muslim 1900")

        val queries = repository.getRecentSearchQueries().first()
        assertTrue(queries.contains("Bukhari 52"))
        assertTrue(queries.contains("Sahih Muslim 1900"))

        repository.clearSearchHistory()
        val cleared = repository.getRecentSearchQueries().first()
        assertTrue("Search history should be cleared", cleared.isEmpty())
    }
}
