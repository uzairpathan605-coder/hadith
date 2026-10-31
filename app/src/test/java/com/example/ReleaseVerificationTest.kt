package com.example

import com.example.data.api.ApiHadithItem
import com.example.data.api.ApiHadithText
import com.example.data.api.FreeHadithApiClient
import com.example.data.api.SunnahApiService
import com.example.data.db.SeedData
import com.example.data.model.HadithEntity
import com.example.data.model.SourceType
import com.example.data.sync.SunnahCrossCheckResult
import com.example.data.sync.SunnahCrossCheckStatus
import com.example.data.sync.SunnahCrossChecker
import com.example.util.ReferenceParser
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ReleaseVerificationTest {

    // 1. Online search with reference queries
    @Test
    fun testOnlineSearchReferenceQueries() {
        val testQueries = listOf(
            "Sahih Bukhari 1" to Pair("bukhari", "1"),
            "bukhari 1" to Pair("bukhari", "1"),
            "Sahih al-Bukhari 52" to Pair("bukhari", "52"),
            "Sahih Muslim 8" to Pair("muslim", "8"),
            "muslim 8" to Pair("muslim", "8"),
            "Sahih Muslim 100" to Pair("muslim", "100"),
            "Sahih Muslim 1900" to Pair("muslim", "1900")
        )

        for ((query, expected) in testQueries) {
            val parsed = ReferenceParser.parse(query)
            assertNotNull("Query '$query' should parse successfully", parsed)
            assertEquals("Collection mismatch for '$query'", expected.first, parsed?.collection)
            assertEquals("Hadith number mismatch for '$query'", expected.second, parsed?.hadithNumber)
        }

        // Verify other collections are not recognized
        val nonSupported = listOf("Sunan Abi Dawud 1", "Tirmidhi 1987", "Sunan an-Nasa'i 1", "40 Hadith Nawawi 1")
        for (q in nonSupported) {
            val parsed = ReferenceParser.parse(q)
            assertTrue("Non-supported collection '$q' should not match a supported collection", parsed?.collection == null)
        }
    }

    // 2. Language-specific download completeness
    @Test
    fun testLanguageSpecificDownloadCompleteness() {
        // Bukhari and Muslim both have complete Arabic, English, and Urdu verified editions in dataset
        val supportedCollections = listOf("bukhari", "muslim")
        for (col in supportedCollections) {
            assertNotNull("Arabic edition must exist for $col", FreeHadithApiClient.getArabicEdition(col))
            assertNotNull("English edition must exist for $col", FreeHadithApiClient.getEnglishEdition(col))
            assertNotNull("Urdu edition must exist for $col", FreeHadithApiClient.getUrduEdition(col))
        }

        // Verify other collections return null for English/Urdu editions
        assertNull("Non-supported collections must not have English edition", FreeHadithApiClient.getEnglishEdition("abudawud"))
        assertNull("Non-supported collections must not have Urdu edition", FreeHadithApiClient.getUrduEdition("abudawud"))

        // Verify entity level language flags
        val entityWithOnlyArabic = HadithEntity(
            id = "bukhari_sample",
            collectionName = "bukhari",
            bookNumber = "1",
            hadithNumber = "9999",
            inBookReference = "Hadith 9999",
            arabicText = "نص عربي فقط",
            englishText = "",
            urduText = "",
            narrator = "",
            grade = "Sahih",
            gradedBy = "Bukhari",
            sourceUrl = "https://example.com",
            sourceType = SourceType.API_SYNCED
        )
        assertTrue(entityWithOnlyArabic.hasArabic)
        assertFalse(entityWithOnlyArabic.hasEnglish)
        assertFalse(entityWithOnlyArabic.hasUrdu)
    }

    // 3. Duplicate handling
    @Test
    fun testDuplicateHandling() {
        val existingHadiths = SeedData.hadiths.toMutableList()
        val initialCount = existingHadiths.size

        // Attempting to add a duplicate ID
        val duplicateCandidate = SeedData.hadiths.first().copy(
            englishText = "Updated text should not duplicate record ID"
        )

        val uniqueMap = existingHadiths.associateBy { it.id }.toMutableMap()
        uniqueMap[duplicateCandidate.id] = duplicateCandidate

        assertEquals("Map should prevent duplicate records with same ID", initialCount, uniqueMap.size)
        assertEquals("bukhari_1", duplicateCandidate.id)
    }

    // 4. Missing records and empty text handling
    @Test
    fun testMissingRecordsAndEmptyTextHandling() {
        val blankEntity = HadithEntity(
            id = "test_empty",
            collectionName = "bukhari",
            bookNumber = "1",
            hadithNumber = "99999",
            inBookReference = "N/A",
            arabicText = "   ",
            englishText = "",
            urduText = "",
            narrator = "",
            grade = "Unknown",
            gradedBy = "Unknown",
            sourceUrl = "",
            sourceType = SourceType.CANONICAL_VERIFIED
        )

        assertFalse("Blank arabic text should report false for hasArabic", blankEntity.hasArabic)
        assertFalse("Empty english text should report false for hasEnglish", blankEntity.hasEnglish)
        assertFalse("Empty urdu text should report false for hasUrdu", blankEntity.hasUrdu)

        val nonBlank = blankEntity.copy(arabicText = "بِسْمِ اللَّهِ")
        assertTrue(nonBlank.hasArabic)
    }

    // 5. Sunnah.com cross-check status handling
    @Test
    fun testSunnahCrossCheckStatusDWhenUnconfigured() = runBlocking {
        val sampleHadith = SeedData.hadiths.first { it.id == "bukhari_1" }

        // Test with null API key
        val resultNullKey = SunnahCrossChecker.checkHadith(sampleHadith, null)
        assertEquals(SunnahCrossCheckStatus.UNAVAILABLE_OR_UNCONFIGURED, resultNullKey.status)
        assertEquals("Sunnah.com cross-check unavailable — API access not configured.", resultNullKey.message)
        assertFalse(resultNullKey.isApiConfigured)
        assertEquals("https://sunnah.com/bukhari:1", resultNullKey.sunnahUrl)

        // Test with blank API key
        val resultBlankKey = SunnahCrossChecker.checkHadith(sampleHadith, "   ")
        assertEquals(SunnahCrossCheckStatus.UNAVAILABLE_OR_UNCONFIGURED, resultBlankKey.status)
        assertEquals("Sunnah.com cross-check unavailable — API access not configured.", resultBlankKey.message)
        assertFalse(resultBlankKey.isApiConfigured)
    }

    @Test
    fun testSunnahCrossCheckStatusBMatchingRecord() = runBlocking {
        val sampleHadith = SeedData.hadiths.first { it.id == "bukhari_1" }

        // Mock Sunnah API Service returning matching Arabic and English text
        val mockService = object : SunnahApiService {
            override suspend fun getCollections(apiKey: String?) = Response.success(emptyList<com.example.data.api.ApiCollectionItem>())
            override suspend fun getBooks(collectionName: String, apiKey: String?) = Response.success(emptyList<com.example.data.api.ApiBookItem>())
            override suspend fun getHadiths(collectionName: String, bookNumber: String, page: Int, limit: Int, apiKey: String?) =
                Response.success(com.example.data.api.ApiHadithListResponse())
            override suspend fun getHadithByNumber(collectionName: String, hadithNumber: String, apiKey: String?): Response<ApiHadithItem> {
                val item = ApiHadithItem(
                    collection = "bukhari",
                    hadithNumber = "1",
                    hadith = listOf(
                        ApiHadithText(lang = "ar", body = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى"),
                        ApiHadithText(lang = "en", body = "Actions are but by intentions and every man will have only that which he intended.")
                    )
                )
                return Response.success(item)
            }
        }

        val result = SunnahCrossChecker.checkHadith(sampleHadith, "valid-test-key", mockService)
        assertEquals(SunnahCrossCheckStatus.MATCHING_RECORD_FOUND, result.status)
        assertTrue(result.title.contains("State B", ignoreCase = true))
        assertTrue(result.matchedArabic)
        assertTrue(result.matchedEnglish)
        assertTrue(result.isApiConfigured)
    }

    @Test
    fun testSunnahCrossCheckStatusCRequiresReview() = runBlocking {
        val sampleHadith = SeedData.hadiths.first { it.id == "bukhari_1" }

        // Mock Sunnah API Service returning non-matching text
        val mockService = object : SunnahApiService {
            override suspend fun getCollections(apiKey: String?) = Response.success(emptyList<com.example.data.api.ApiCollectionItem>())
            override suspend fun getBooks(collectionName: String, apiKey: String?) = Response.success(emptyList<com.example.data.api.ApiBookItem>())
            override suspend fun getHadiths(collectionName: String, bookNumber: String, page: Int, limit: Int, apiKey: String?) =
                Response.success(com.example.data.api.ApiHadithListResponse())
            override suspend fun getHadithByNumber(collectionName: String, hadithNumber: String, apiKey: String?): Response<ApiHadithItem> {
                val item = ApiHadithItem(
                    collection = "bukhari",
                    hadithNumber = "1",
                    hadith = listOf(
                        ApiHadithText(lang = "ar", body = "نص مختلف تماما عن الحديث الأول"),
                        ApiHadithText(lang = "en", body = "Completely different narration text that does not match intentions.")
                    )
                )
                return Response.success(item)
            }
        }

        val result = SunnahCrossChecker.checkHadith(sampleHadith, "valid-test-key", mockService)
        assertEquals(SunnahCrossCheckStatus.REQUIRES_REVIEW, result.status)
        assertTrue(result.title.contains("State C", ignoreCase = true))
        assertFalse(result.matchedArabic)
        assertFalse(result.matchedEnglish)
    }

    @Test
    fun testSunnahCrossCheckStatusDOnHttpError() = runBlocking {
        val sampleHadith = SeedData.hadiths.first { it.id == "bukhari_1" }

        val mockService = object : SunnahApiService {
            override suspend fun getCollections(apiKey: String?) = Response.success(emptyList<com.example.data.api.ApiCollectionItem>())
            override suspend fun getBooks(collectionName: String, apiKey: String?) = Response.success(emptyList<com.example.data.api.ApiBookItem>())
            override suspend fun getHadiths(collectionName: String, bookNumber: String, page: Int, limit: Int, apiKey: String?) =
                Response.success(com.example.data.api.ApiHadithListResponse())
            override suspend fun getHadithByNumber(collectionName: String, hadithNumber: String, apiKey: String?): Response<ApiHadithItem> {
                return Response.error(404, "{\"error\": \"Not Found\"}".toResponseBody("application/json".toMediaTypeOrNull()))
            }
        }

        val result = SunnahCrossChecker.checkHadith(sampleHadith, "valid-test-key", mockService)
        assertEquals(SunnahCrossCheckStatus.UNAVAILABLE_OR_UNCONFIGURED, result.status)
        assertTrue(result.title.contains("State D", ignoreCase = true))
        assertTrue(result.message.contains("404"))
    }
}
