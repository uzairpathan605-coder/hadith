package com.example.data.sync

import com.example.data.api.SunnahApiClient
import com.example.data.api.SunnahApiService
import com.example.data.model.HadithEntity
import com.example.util.ArabicNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class SunnahCrossCheckStatus {
    SOURCE_RECORD_FOUND,            // State A
    MATCHING_RECORD_FOUND,          // State B
    REQUIRES_REVIEW,                // State C
    UNAVAILABLE_OR_UNCONFIGURED     // State D
}

data class SunnahCrossCheckResult(
    val status: SunnahCrossCheckStatus,
    val title: String,
    val message: String,
    val sunnahUrl: String,
    val isApiConfigured: Boolean,
    val matchedArabic: Boolean = false,
    val matchedEnglish: Boolean = false
)

object SunnahCrossChecker {

    fun generateSunnahUrl(collection: String, hadithNumber: String): String {
        val col = when (collection.lowercase()) {
            "bukhari" -> "bukhari"
            "muslim" -> "muslim"
            else -> collection.lowercase()
        }
        return "https://sunnah.com/$col:$hadithNumber"
    }

    suspend fun checkHadith(
        hadith: HadithEntity,
        apiKey: String?,
        apiService: SunnahApiService = SunnahApiClient.service
    ): SunnahCrossCheckResult = withContext(Dispatchers.IO) {
        val sunnahUrl = generateSunnahUrl(hadith.collectionName, hadith.hadithNumber)

        if (apiKey.isNullOrBlank()) {
            return@withContext SunnahCrossCheckResult(
                status = SunnahCrossCheckStatus.UNAVAILABLE_OR_UNCONFIGURED,
                title = "State D: API Access Not Configured",
                message = "Sunnah.com cross-check unavailable — API access not configured.",
                sunnahUrl = sunnahUrl,
                isApiConfigured = false
            )
        }

        try {
            val response = apiService.getHadithByNumber(
                collectionName = hadith.collectionName.lowercase(),
                hadithNumber = hadith.hadithNumber,
                apiKey = apiKey
            )

            if (!response.isSuccessful || response.body() == null) {
                return@withContext SunnahCrossCheckResult(
                    status = SunnahCrossCheckStatus.UNAVAILABLE_OR_UNCONFIGURED,
                    title = "State D: Sunnah.com Record Unavailable",
                    message = "Sunnah.com record unavailable (HTTP ${response.code()}). The narration could not be retrieved from the Sunnah.com API endpoint.",
                    sunnahUrl = sunnahUrl,
                    isApiConfigured = true
                )
            }

            val remoteItem = response.body()!!
            val remoteAr = remoteItem.hadith.firstOrNull { it.lang == "ar" }?.body ?: ""
            val remoteEn = remoteItem.hadith.firstOrNull { it.lang == "en" }?.body ?: ""

            // Compare texts
            val hasArabicLocal = hadith.arabicText.isNotBlank()
            val hasEnglishLocal = hadith.englishText.isNotBlank()

            var matchedArabic = false
            var matchedEnglish = false

            if (hasArabicLocal && remoteAr.isNotBlank()) {
                val normLocal = ArabicNormalizer.normalize(hadith.arabicText)
                val normRemote = ArabicNormalizer.normalize(remoteAr)
                val remoteWords = normRemote.split("\\s+".toRegex()).filter { it.length > 2 }
                val matchedWordsCount = remoteWords.count { normLocal.contains(it) }
                val ratio = if (remoteWords.isNotEmpty()) matchedWordsCount.toDouble() / remoteWords.size else 0.0
                matchedArabic = normLocal.contains(normRemote) || normRemote.contains(normLocal) || ratio >= 0.4
            }

            if (hasEnglishLocal && remoteEn.isNotBlank()) {
                val cleanLocal = hadith.englishText.lowercase().replace("[^a-z0-9 ]".toRegex(), " ")
                val cleanRemote = remoteEn.lowercase().replace("[^a-z0-9 ]".toRegex(), " ")
                val remoteWords = cleanRemote.split("\\s+".toRegex()).filter { it.length > 3 }
                val matchedWordsCount = remoteWords.count { cleanLocal.contains(it) }
                val ratio = if (remoteWords.isNotEmpty()) matchedWordsCount.toDouble() / remoteWords.size else 0.0
                matchedEnglish = cleanLocal.contains(cleanRemote) || cleanRemote.contains(cleanLocal) || ratio >= 0.35
            }

            if (matchedArabic || matchedEnglish) {
                SunnahCrossCheckResult(
                    status = SunnahCrossCheckStatus.MATCHING_RECORD_FOUND,
                    title = "State B: Matching Record Found on Sunnah.com",
                    message = "Verified: Narration matches record in Sunnah.com repository. Text and collection reference aligned.",
                    sunnahUrl = sunnahUrl,
                    isApiConfigured = true,
                    matchedArabic = matchedArabic,
                    matchedEnglish = matchedEnglish
                )
            } else if (remoteAr.isNotBlank() || remoteEn.isNotBlank()) {
                SunnahCrossCheckResult(
                    status = SunnahCrossCheckStatus.REQUIRES_REVIEW,
                    title = "State C: Text or Reference Differs / Requires Review",
                    message = "Notice: Narration retrieved from Sunnah.com, but text variants or numbering differences were detected. Manual scholarly review recommended.",
                    sunnahUrl = sunnahUrl,
                    isApiConfigured = true,
                    matchedArabic = false,
                    matchedEnglish = false
                )
            } else {
                SunnahCrossCheckResult(
                    status = SunnahCrossCheckStatus.SOURCE_RECORD_FOUND,
                    title = "State A: Source Record Found",
                    message = "Source record preserved from primary open repository. Sunnah.com record entry is present but contains empty text bodies.",
                    sunnahUrl = sunnahUrl,
                    isApiConfigured = true
                )
            }
        } catch (e: Exception) {
            SunnahCrossCheckResult(
                status = SunnahCrossCheckStatus.UNAVAILABLE_OR_UNCONFIGURED,
                title = "State D: Sunnah.com Cross-Check Unavailable",
                message = "Sunnah.com cross-check unavailable: ${e.message ?: "Network error"}",
                sunnahUrl = sunnahUrl,
                isApiConfigured = true
            )
        }
    }
}
