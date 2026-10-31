package com.example

import com.example.data.db.SeedData
import com.example.util.ArabicNormalizer
import com.example.util.ReferenceParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HadithSearchTest {

    @Test
    fun testReferenceParserMatchesSahihBukhariOne() {
        val parsed = ReferenceParser.parse("Sahih Bukhari 1")
        assertNotNull(parsed)
        assertEquals("bukhari", parsed?.collection)
        assertEquals("1", parsed?.hadithNumber)

        val match = SeedData.hadiths.find {
            it.collectionName == parsed?.collection && it.hadithNumber == parsed?.hadithNumber
        }
        assertNotNull("Should find Bukhari 1 in seeded dataset", match)
        assertEquals("1", match?.hadithNumber)
        assertTrue(match?.arabicText?.contains("إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ") == true)
    }

    @Test
    fun testArabicFullTextSearchWithTashkeelInsensitive() {
        // Query without diacritics
        val query = "انما الاعمال بالنيات"
        val matches = SeedData.hadiths.filter {
            ArabicNormalizer.containsNormalized(it.arabicText, query)
        }
        assertTrue("Normalized search should find Hadith of Intentions", matches.isNotEmpty())
        assertEquals("bukhari_1", matches.first().id)
    }

    @Test
    fun testUrduFullTextSearch() {
        val query = "نیتوں"
        val matches = SeedData.hadiths.filter {
            it.urduText.contains(query, ignoreCase = true)
        }
        assertTrue("Should match Urdu translation with 'نیتوں'", matches.isNotEmpty())
    }

    @Test
    fun testEnglishFullTextSearch() {
        val query = "intentions"
        val matches = SeedData.hadiths.filter {
            it.englishText.contains(query, ignoreCase = true)
        }
        assertTrue("Should match English translation with 'intentions'", matches.isNotEmpty())
    }

    @Test
    fun testNarratorSearch() {
        val query = "Umar"
        val matches = SeedData.hadiths.filter {
            it.narrator.contains(query, ignoreCase = true)
        }
        assertTrue("Should match hadiths narrated by Umar", matches.isNotEmpty())
    }

    @Test
    fun testAuthenticityGradeFilter() {
        val sahihHadiths = SeedData.hadiths.filter { it.grade.contains("Sahih", ignoreCase = true) }
        assertTrue("Should have verified Sahih hadiths", sahihHadiths.isNotEmpty())
        sahihHadiths.forEach {
            assertTrue(it.grade.contains("Sahih", ignoreCase = true))
        }
    }

    @Test
    fun testExactReferenceLookupBukhariFiftyTwo() {
        val parsed = ReferenceParser.parse("Bukhari 52")
        assertNotNull(parsed)
        assertEquals("bukhari", parsed?.collection)
        assertEquals("52", parsed?.hadithNumber)

        val match = SeedData.hadiths.find {
            it.collectionName == parsed?.collection && it.hadithNumber == parsed?.hadithNumber
        }
        assertNotNull("Should find Bukhari 52 in seeded dataset", match)
        assertEquals("52", match?.hadithNumber)
        assertTrue(match?.arabicText?.contains("الْحَلاَلُ بَيِّنٌ وَالْحَرَامُ بَيِّنٌ") == true)
        assertTrue(match?.englishText?.contains("doubtful matters") == true)
    }

    @Test
    fun testExactReferenceLookupSahihMuslimNineteenHundred() {
        val parsed = ReferenceParser.parse("Sahih Muslim 1900")
        assertNotNull(parsed)
        assertEquals("muslim", parsed?.collection)
        assertEquals("1900", parsed?.hadithNumber)

        val match = SeedData.hadiths.find {
            it.collectionName == parsed?.collection && it.hadithNumber == parsed?.hadithNumber
        }
        assertNotNull("Should find Muslim 1900 in seeded dataset", match)
        assertEquals("1900", match?.hadithNumber)
        assertTrue(match?.arabicText?.contains("مَنْ دَلَّ عَلَى خَيْرٍ") == true)
        assertTrue(match?.englishText?.contains("Whoever guides someone to goodness") == true)
    }

    @Test
    fun testPartialCollectionNameMatching() {
        val bukhariMatch = ReferenceParser.matchCollection("bukhari")
        assertEquals("bukhari", bukhariMatch)

        val muslimMatch = ReferenceParser.matchCollection("muslim")
        assertEquals("muslim", muslimMatch)

        val arabicBukhari = ReferenceParser.matchCollection("بخاري")
        assertEquals("bukhari", arabicBukhari)

        val arabicMuslim = ReferenceParser.matchCollection("مسلم")
        assertEquals("muslim", arabicMuslim)

        val abuDawudMatch = ReferenceParser.matchCollection("abu dawud")
        org.junit.Assert.assertNull(abuDawudMatch)
    }

    @Test
    fun testPartialNumberMatching() {
        // Query "190" should partially match "1900" and "1907"
        val query = "190"
        val matches = SeedData.hadiths.filter { it.hadithNumber.startsWith(query) }
        assertTrue("Query '190' should match at least 1900 and 1907", matches.size >= 2)
        assertTrue(matches.any { it.hadithNumber == "1900" })
        assertTrue(matches.any { it.hadithNumber == "1907" })

        // Query "52" exact and partial
        val matches52 = SeedData.hadiths.filter { it.hadithNumber.startsWith("52") }
        assertTrue("Query '52' should match Bukhari 52", matches52.any { it.id == "bukhari_52" })
    }

    @Test
    fun testMultiLanguageSearchAndNoDuplicateSuggestions() {
        // Arabic search
        val arMatches = SeedData.hadiths.filter {
            ArabicNormalizer.containsNormalized(it.arabicText, "الخير") ||
            ArabicNormalizer.containsNormalized(it.arabicText, "خير")
        }
        assertTrue("Arabic search for 'خير' should return hadiths", arMatches.isNotEmpty())

        // English search
        val enMatches = SeedData.hadiths.filter {
            it.englishText.contains("goodness", ignoreCase = true)
        }
        assertTrue("English search for 'goodness' should return Muslim 1900", enMatches.any { it.id == "muslim_1900" })

        // Urdu search
        val urMatches = SeedData.hadiths.filter {
            it.urduText.contains("نیکی", ignoreCase = true)
        }
        assertTrue("Urdu search for 'نیکی' should return Muslim 1900", urMatches.any { it.id == "muslim_1900" })

        // Verify no duplicate IDs
        val distinctCount = SeedData.hadiths.distinctBy { it.id }.size
        assertEquals(SeedData.hadiths.size, distinctCount)
    }
}
