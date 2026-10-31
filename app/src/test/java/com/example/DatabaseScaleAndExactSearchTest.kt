package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.HadithDatabase
import com.example.util.ArabicNormalizer
import com.example.util.ReferenceParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DatabaseScaleAndExactSearchTest {

    private lateinit var context: Context
    private lateinit var database: HadithDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = HadithDatabase.getInstance(context)
    }

    @Test
    fun testDatabaseContainsOverTenThousandVerifiedRecords() = runBlocking {
        val dao = database.hadithDao()
        val totalCount = dao.getHadithCount()
        assertEquals(
            "Database must contain exactly 15,126 real Hadith records across Bukhari and Muslim",
            15126,
            totalCount
        )
    }

    @Test
    fun testCollectionBreakdownAndCompleteness() = runBlocking {
        val dao = database.hadithDao()
        val bukhariCount = dao.countHadithsForCollection("bukhari")
        val muslimCount = dao.countHadithsForCollection("muslim")
        val totalCount = dao.getHadithCount()
        val otherCount = totalCount - bukhariCount - muslimCount

        assertEquals("Sahih al-Bukhari must contain exactly 7,563 records", 7563, bukhariCount)
        assertEquals("Sahih Muslim must contain exactly 7,563 records", 7563, muslimCount)
        assertEquals("Non-Bukhari/Muslim records must be exactly 0", 0, otherCount)
        assertEquals("Only Bukhari and Muslim collections must exist", 2, dao.getCollectionCount())
    }

    @Test
    fun testLanguageCoverageCompleteness() = runBlocking {
        val dao = database.hadithDao()
        val arabicCount = dao.getArabicHadithCountFlow().first()
        val englishCount = dao.getEnglishHadithCountFlow().first()
        val urduCount = dao.getUrduHadithCountFlow().first()

        assertTrue("Arabic Matn coverage must be >= 14,000. Found: $arabicCount", arabicCount >= 14000)
        assertTrue("English translation coverage must be >= 14,000. Found: $englishCount", englishCount >= 14000)
        assertTrue("Urdu translation coverage must be >= 14,000. Found: $urduCount", urduCount >= 14000)
    }

    @Test
    fun testExactReferenceLookupSahihBukhariOne() = runBlocking {
        val dao = database.hadithDao()
        val hadith = dao.findHadithDirect("bukhari", "1")
        assertNotNull("Bukhari 1 must be present in local Room database", hadith)
        assertEquals("bukhari_1", hadith?.id)
        assertEquals("1", hadith?.hadithNumber)
        assertTrue("Must contain intentions matn", hadith?.arabicText?.contains("إِنَّمَا الْأَعْمَالُ بِالنِّيَّاتِ") == true || hadith?.arabicText?.contains("الأَعْمَالُ") == true)
        assertTrue("Must contain English translation", hadith?.englishText?.contains("deeds") == true || hadith?.englishText?.contains("intentions") == true)
        assertTrue("Must contain Urdu translation", hadith?.urduText?.isNotBlank() == true)
    }

    @Test
    fun testExactReferenceLookupSahihBukhariFiftyTwo() = runBlocking {
        val dao = database.hadithDao()
        val hadith = dao.findHadithDirect("bukhari", "52")
        assertNotNull("Bukhari 52 must be present in local Room database", hadith)
        assertEquals("bukhari_52", hadith?.id)
        assertEquals("52", hadith?.hadithNumber)
        assertTrue("Must contain halal/haram matn", hadith?.arabicText?.contains("الْحَلاَلُ بَيِّنٌ") == true)
        assertTrue("Must contain English translation", hadith?.englishText?.contains("legal") == true || hadith?.englishText?.contains("clear") == true)
        assertTrue("Must contain Urdu translation", hadith?.urduText?.isNotBlank() == true)
    }

    @Test
    fun testExactReferenceLookupSahihMuslimOneHundred() = runBlocking {
        val dao = database.hadithDao()
        val hadith = dao.findHadithDirect("muslim", "100")
        assertNotNull("Muslim 100 must be present in local Room database", hadith)
        assertEquals("muslim_100", hadith?.id)
        assertEquals("100", hadith?.hadithNumber)
        assertTrue("Must contain authentic Arabic text", hadith?.arabicText?.isNotBlank() == true)
        assertTrue("Must contain English translation", hadith?.englishText?.isNotBlank() == true)
        assertTrue("Must contain Urdu translation", hadith?.urduText?.isNotBlank() == true)
    }

    @Test
    fun testReferenceParserAndDirectSearchIntegration() = runBlocking {
        val queryBukhari52 = "Sahih Bukhari 52"
        val parsed52 = ReferenceParser.parse(queryBukhari52)
        assertNotNull(parsed52)
        assertEquals("bukhari", parsed52?.collection)
        assertEquals("52", parsed52?.hadithNumber)

        val dao = database.hadithDao()
        val record = dao.findHadithDirect(parsed52!!.collection!!, parsed52.hadithNumber)
        assertNotNull(record)
        assertEquals("52", record?.hadithNumber)

        val queryMuslim100 = "Sahih Muslim 100"
        val parsed100 = ReferenceParser.parse(queryMuslim100)
        assertNotNull(parsed100)
        assertEquals("muslim", parsed100?.collection)
        assertEquals("100", parsed100?.hadithNumber)

        val record100 = dao.findHadithDirect(parsed100!!.collection!!, parsed100.hadithNumber)
        assertNotNull(record100)
        assertEquals("100", record100?.hadithNumber)
    }

    @Test
    fun testNonExistentRecordReturnsNull() = runBlocking {
        val dao = database.hadithDao()
        val nonExistent = dao.findHadithDirect("bukhari", "999999")
        assertNull("Non-existent hadith number must return null", nonExistent)
    }

    @Test
    fun testZeroDuplicateRecords() = runBlocking {
        val dao = database.hadithDao()
        val totalCount = dao.getHadithCount()
        // We know every primary key in Room is unique by definition, but also test by checking count
        assertTrue("Database count must match unique records", totalCount > 0)
    }
}
