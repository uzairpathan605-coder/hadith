package com.example

import com.example.data.adapter.FawazAhmedSourceAdapter
import com.example.data.api.FreeApiGrade
import com.example.data.api.FreeApiHadithItem
import com.example.data.api.FreeApiHadithResponse
import com.example.data.api.FreeApiReference
import com.example.data.api.FreeHadithApiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FawazAhmedApiVerificationTest {

    @Test
    fun testEditionMappingsForKutubAlSittahAndOthers() {
        val supportedCollections = listOf("bukhari", "muslim")

        for (col in supportedCollections) {
            val ar = FreeHadithApiClient.getArabicEdition(col)
            val en = FreeHadithApiClient.getEnglishEdition(col)
            val ur = FreeHadithApiClient.getUrduEdition(col)

            assertEquals("ara-$col", ar)
            assertEquals("eng-$col", en)
            assertEquals("urd-$col", ur)
        }

        // Other collections must return null for English and Urdu
        assertNull(FreeHadithApiClient.getEnglishEdition("abudawud"))
        assertNull(FreeHadithApiClient.getUrduEdition("abudawud"))
        assertNull(FreeHadithApiClient.getEnglishEdition("malik"))
        assertNull(FreeHadithApiClient.getUrduEdition("malik"))
        assertNull(FreeHadithApiClient.getEnglishEdition("nawawi"))
        assertNull(FreeHadithApiClient.getUrduEdition("nawawi"))
    }

    @Test
    fun testFawazAhmedAdapterRequirements() {
        val adapter = FawazAhmedSourceAdapter()
        assertEquals("fawaz_ahmed_cdn", adapter.sourceId)
        assertFalse("Must never require an API key", adapter.requiresApiKey)
        assertTrue(adapter.licenseNotice.contains("The Unlicense", ignoreCase = true))
        assertTrue(adapter.attributionNotice.contains("fawazahmed0/hadith-api", ignoreCase = true))
    }

    @Test
    fun testJsonPayloadParsing() {
        val jsonSample = """
            {
              "metadata": {
                "name": "Sahih al Bukhari",
                "section": {
                  "1": "Revelation"
                }
              },
              "hadiths": [
                {
                  "hadithnumber": 1.0,
                  "arabicnumber": 1.0,
                  "text": "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
                  "grades": [
                    {
                      "name": "Al-Albani",
                      "grade": "Sahih"
                    }
                  ],
                  "reference": {
                    "book": 1,
                    "hadith": 1
                  }
                }
              ]
            }
        """.trimIndent()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(FreeApiHadithResponse::class.java)
        val response = adapter.fromJson(jsonSample)

        assertNotNull(response)
        assertNotNull(response?.hadiths)
        assertEquals(1, response?.hadiths?.size)

        val item = response?.hadiths?.first()
        assertEquals(1.0, item?.hadithnumber)
        assertEquals("إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ", item?.text)
        assertEquals(1, item?.grades?.size)
        assertEquals("Sahih", item?.grades?.first()?.grade)
        assertEquals("Al-Albani", item?.grades?.first()?.name)
        assertEquals(1, item?.reference?.book)
        assertEquals(1, item?.reference?.hadith)
    }
}
