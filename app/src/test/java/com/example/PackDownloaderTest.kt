package com.example

import com.example.data.adapter.FawazAhmedSourceAdapter
import com.example.data.adapter.HadithApiComSourceAdapter
import com.example.data.adapter.SunnahComSourceAdapter
import com.example.data.sync.DatabaseIntegrityReport
import com.example.data.sync.PackDownloadState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PackDownloaderTest {

    @Test
    fun testAdapterMetadataAndLicensing() {
        val fawazAdapter = FawazAhmedSourceAdapter()
        assertEquals("fawaz_ahmed_cdn", fawazAdapter.sourceId)
        assertFalse("Primary adapter must not require API key", fawazAdapter.requiresApiKey)
        assertTrue(fawazAdapter.licenseNotice.contains("The Unlicense", ignoreCase = true))

        val sunnahAdapter = SunnahComSourceAdapter()
        assertTrue("Sunnah adapter requires API key", sunnahAdapter.requiresApiKey)

        val hadithApiAdapter = HadithApiComSourceAdapter()
        assertTrue("HadithAPI adapter requires API key", hadithApiAdapter.requiresApiKey)
    }

    @Test
    fun testDatabaseIntegrityReportModel() {
        val report = DatabaseIntegrityReport(
            isValid = true,
            totalHadiths = 23,
            totalCollections = 9,
            bookmarkedCount = 2,
            collectionBreakdown = mapOf("bukhari" to 7, "muslim" to 5),
            integrityMessage = "SQLite PRAGMA integrity_check: ok"
        )

        assertTrue(report.isValid)
        assertEquals(23, report.totalHadiths)
        assertEquals(9, report.totalCollections)
        assertEquals(7, report.collectionBreakdown["bukhari"])
    }

    @Test
    fun testPackDownloadStateTransitions() {
        val idleState: PackDownloadState = PackDownloadState.Idle
        val progressState: PackDownloadState = PackDownloadState.Progress(
            collection = "bukhari",
            downloaded = 10,
            total = 18,
            step = "Importing hadiths..."
        )
        val successState: PackDownloadState = PackDownloadState.Success(
            collection = "bukhari",
            importedCount = 18
        )

        assertTrue(idleState is PackDownloadState.Idle)
        assertTrue(progressState is PackDownloadState.Progress)
        assertEquals(10, (progressState as PackDownloadState.Progress).downloaded)
        assertTrue(successState is PackDownloadState.Success)
        assertEquals(18, (successState as PackDownloadState.Success).importedCount)
    }

    @Test
    fun testBookAndCollectionNumberMappings() {
        val mockDao = object : com.example.data.dao.HadithDao {
            override fun getAllCollections() = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.CollectionEntity>>()
            override fun getCollection(name: String) = kotlinx.coroutines.flow.emptyFlow<com.example.data.model.CollectionEntity?>()
            override fun getBooksForCollection(collectionName: String) = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.BookEntity>>()
            override fun getHadithsForBook(collectionName: String, bookNumber: String) = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.HadithEntity>>()
            override fun getHadithsForCollection(collectionName: String) = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.HadithEntity>>()
            override fun getHadithCountForCollection(collectionName: String) = kotlinx.coroutines.flow.emptyFlow<Int>()
            override suspend fun countHadithsForCollection(collectionName: String) = 0
            override fun getHadithById(id: String) = kotlinx.coroutines.flow.emptyFlow<com.example.data.model.HadithEntity?>()
            override fun getHadithByNumber(collectionName: String, hadithNumber: String) = kotlinx.coroutines.flow.emptyFlow<com.example.data.model.HadithEntity?>()
            override suspend fun findHadithDirect(collectionName: String, hadithNumber: String) = null
            override suspend fun findHadithByNumberOnly(hadithNumber: String) = null
            override fun getBookmarkedHadiths() = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.HadithEntity>>()
            override fun getRecentHistory(limit: Int) = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.HadithEntity>>()
            override fun getAllHadiths() = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.HadithEntity>>()
            override fun getRandomHadith() = kotlinx.coroutines.flow.emptyFlow<com.example.data.model.HadithEntity?>()
            override suspend fun insertCollections(collections: List<com.example.data.model.CollectionEntity>) {}
            override suspend fun insertBooks(books: List<com.example.data.model.BookEntity>) {}
            override suspend fun insertHadiths(hadiths: List<com.example.data.model.HadithEntity>) {}
            override suspend fun insertHadith(hadith: com.example.data.model.HadithEntity) {}
            override suspend fun setBookmark(hadithId: String, isBookmarked: Boolean) {}
            override suspend fun updateLastViewed(hadithId: String, timestamp: Long) {}
            override suspend fun getHadithCount() = 0
            override suspend fun getCollectionCount() = 0
            override fun getTotalHadithCountFlow() = kotlinx.coroutines.flow.emptyFlow<Int>()
            override fun getArabicHadithCountFlow() = kotlinx.coroutines.flow.emptyFlow<Int>()
            override fun getEnglishHadithCountFlow() = kotlinx.coroutines.flow.emptyFlow<Int>()
            override fun getUrduHadithCountFlow() = kotlinx.coroutines.flow.emptyFlow<Int>()
            override suspend fun clearUnsavedCache() {}
        }

        val downloader = com.example.data.sync.PackDownloader(mockDao)
        val bukhariNumbers = downloader.getCollectionHadithNumbers("bukhari")
        assertEquals(18, bukhariNumbers.size)
        assertEquals("1", bukhariNumbers.first())
        assertEquals("100", bukhariNumbers.last())

        val bukhariBook1 = downloader.getBookHadithNumbers("bukhari", "1")
        assertEquals(7, bukhariBook1.size)
        assertEquals("1", bukhariBook1.first())
        assertEquals("7", bukhariBook1.last())

        val muslimNumbers = downloader.getCollectionHadithNumbers("muslim")
        assertTrue("Muslim pack should contain 1900", muslimNumbers.contains("1900"))
        assertTrue("Muslim pack should contain 1907", muslimNumbers.contains("1907"))
    }
}
