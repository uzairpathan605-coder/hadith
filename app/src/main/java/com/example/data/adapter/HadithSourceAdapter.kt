package com.example.data.adapter

import com.example.data.api.FreeHadithApiClient
import com.example.data.api.FreeHadithApiService
import com.example.data.api.SunnahApiClient
import com.example.data.api.SunnahApiService
import com.example.data.model.HadithEntity
import com.example.data.model.SourceType
import kotlinx.coroutines.delay
import java.io.IOException

interface HadithSourceAdapter {
    val sourceId: String
    val displayName: String
    val requiresApiKey: Boolean
    val licenseNotice: String
    val attributionNotice: String

    suspend fun fetchHadith(
        collection: String,
        hadithNumber: String,
        apiKey: String? = null
    ): Result<HadithEntity>
}

/**
 * Primary free, open-access adapter backed by Fawaz Ahmed Hadith Dataset (The Unlicense / Public Domain).
 * Free to access via jsDelivr CDN, zero API key required, zero commercial cost.
 */
class FawazAhmedSourceAdapter(
    private val apiService: FreeHadithApiService = FreeHadithApiClient.service
) : HadithSourceAdapter {

    override val sourceId: String = "fawaz_ahmed_cdn"
    override val displayName: String = "Fawaz Ahmed Open Hadith Dataset (Public Domain)"
    override val requiresApiKey: Boolean = false
    override val licenseNotice: String = "Dedicated to the Public Domain under The Unlicense."
    override val attributionNotice: String = "Source: github.com/fawazahmed0/hadith-api. Free, open-access prophetic traditions."

    override suspend fun fetchHadith(
        collection: String,
        hadithNumber: String,
        apiKey: String?
    ): Result<HadithEntity> {
        var lastException: Exception? = null
        val cleanNumber = hadithNumber.trim().toIntOrNull()?.toString() ?: hadithNumber.trim()

        // Try up to 2 attempts with brief exponential backoff
        for (attempt in 1..2) {
            try {
                val arEdition = FreeHadithApiClient.getArabicEdition(collection)
                val enEdition = FreeHadithApiClient.getEnglishEdition(collection)
                val urEdition = FreeHadithApiClient.getUrduEdition(collection)

                val arResp = try {
                    apiService.getHadith(arEdition, cleanNumber)
                } catch (e: Exception) {
                    null
                }

                val enResp = if (enEdition != null) {
                    try {
                        apiService.getHadith(enEdition, cleanNumber)
                    } catch (e: Exception) {
                        null
                    }
                } else null

                val urResp = if (urEdition != null) {
                    try {
                        apiService.getHadith(urEdition, cleanNumber)
                    } catch (e: Exception) {
                        null
                    }
                } else null

                val arItem = if (arResp != null && arResp.isSuccessful) arResp.body()?.hadiths?.firstOrNull() else null
                val enItem = if (enResp != null && enResp.isSuccessful) enResp.body()?.hadiths?.firstOrNull() else null
                val urItem = if (urResp != null && urResp.isSuccessful) urResp.body()?.hadiths?.firstOrNull() else null

                if (arItem == null && enItem == null) {
                    return Result.failure(IOException("Hadith #$cleanNumber not found in $collection"))
                }

                val gradeObj = enItem?.grades?.firstOrNull() ?: arItem?.grades?.firstOrNull()
                val isSahihayn = collection.lowercase() in listOf("bukhari", "muslim")
                val gradeName = gradeObj?.grade?.takeIf { it.isNotBlank() }
                    ?: if (isSahihayn) "Sahih" else "Not Specified"
                val gradedBy = gradeObj?.name?.takeIf { it.isNotBlank() }
                    ?: if (isSahihayn) "Scholarly Consensus (Sahihayn)" else "Source Dataset"
                val bookNum = enItem?.reference?.book?.toString() ?: arItem?.reference?.book?.toString() ?: "1"

                val entity = HadithEntity(
                    id = "${collection}_$cleanNumber",
                    collectionName = collection,
                    bookNumber = bookNum,
                    hadithNumber = cleanNumber,
                    inBookReference = "$collection #$cleanNumber",
                    arabicText = arItem?.text ?: "",
                    englishText = enItem?.text ?: "",
                    urduText = urItem?.text ?: "",
                    narrator = "",
                    grade = gradeName,
                    gradedBy = gradedBy,
                    sourceUrl = "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/$arEdition/$cleanNumber.json",
                    sourceType = SourceType.API_SYNCED,
                    isOfflineCached = true
                )
                return Result.success(entity)
            } catch (e: Exception) {
                lastException = e
                if (attempt < 2) delay(1000)
            }
        }
        return Result.failure(lastException ?: IOException("Failed to fetch hadith"))
    }
}

/**
 * Optional official Sunnah.com adapter.
 * Requires a developer API key issued by Sunnah.com.
 */
class SunnahComSourceAdapter(
    private val apiService: SunnahApiService = SunnahApiClient.service
) : HadithSourceAdapter {

    override val sourceId: String = "sunnah_com"
    override val displayName: String = "Sunnah.com Official API"
    override val requiresApiKey: Boolean = true
    override val licenseNotice: String = "Official developer API terms (sunnah.stoplight.io/docs/api)"
    override val attributionNotice: String = "Data provided by Sunnah.com with attribution."

    override suspend fun fetchHadith(
        collection: String,
        hadithNumber: String,
        apiKey: String?
    ): Result<HadithEntity> {
        if (apiKey.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Sunnah.com API key is not configured."))
        }

        return try {
            val response = apiService.getHadithByNumber(collection, hadithNumber, apiKey)
            if (response.isSuccessful && response.body() != null) {
                val item = response.body()!!
                val arText = item.hadith.firstOrNull { it.lang == "ar" }?.body ?: ""
                val enText = item.hadith.firstOrNull { it.lang == "en" }?.body ?: ""
                val gradeText = item.grades.firstOrNull()?.grade ?: "Verified"
                val gradedBy = item.grades.firstOrNull()?.gradedBy ?: "Sunnah.com"

                val entity = HadithEntity(
                    id = "${collection}_$hadithNumber",
                    collectionName = collection,
                    bookNumber = item.bookNumber ?: "1",
                    hadithNumber = hadithNumber,
                    inBookReference = "Hadith $hadithNumber",
                    arabicText = arText,
                    englishText = enText,
                    urduText = "",
                    narrator = "",
                    grade = gradeText,
                    gradedBy = gradedBy,
                    sourceUrl = "https://sunnah.com/$collection:$hadithNumber",
                    sourceType = SourceType.API_SYNCED,
                    isOfflineCached = true
                )
                Result.success(entity)
            } else {
                Result.failure(IOException("Sunnah.com API error: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Placeholder adapter for HadithAPI.com.
 * Requires individual developer registration and API key, free for standard non-commercial use.
 */
class HadithApiComSourceAdapter : HadithSourceAdapter {
    override val sourceId: String = "hadithapi_com"
    override val displayName: String = "HadithAPI.com (Free Registration Required)"
    override val requiresApiKey: Boolean = true
    override val licenseNotice: String = "Free public developer API with rate-limiting terms."
    override val attributionNotice: String = "Data courtesy of HadithAPI.com."

    override suspend fun fetchHadith(
        collection: String,
        hadithNumber: String,
        apiKey: String?
    ): Result<HadithEntity> {
        return Result.failure(
            UnsupportedOperationException("HadithAPI.com requires personal registration at hadithapi.com to obtain a free API key.")
        )
    }
}
