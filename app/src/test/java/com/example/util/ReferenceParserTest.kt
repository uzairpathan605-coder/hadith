package com.example.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ReferenceParserTest {

    @Test
    fun testParseSahihBukhariOne() {
        val result = ReferenceParser.parse("Sahih Bukhari 1")
        assertNotNull(result)
        assertEquals("bukhari", result?.collection)
        assertEquals("1", result?.hadithNumber)
        assertEquals("Sahih al-Bukhari #1", result?.canonicalName)
    }

    @Test
    fun testParseSahihAlBukhari() {
        val result = ReferenceParser.parse("Sahih al-Bukhari 13")
        assertNotNull(result)
        assertEquals("bukhari", result?.collection)
        assertEquals("13", result?.hadithNumber)
    }

    @Test
    fun testParseBukhariWithColonAndHash() {
        val resultColon = ReferenceParser.parse("bukhari:1")
        assertNotNull(resultColon)
        assertEquals("bukhari", resultColon?.collection)
        assertEquals("1", resultColon?.hadithNumber)

        val resultHash = ReferenceParser.parse("bukhari #8")
        assertNotNull(resultHash)
        assertEquals("bukhari", resultHash?.collection)
        assertEquals("8", resultHash?.hadithNumber)
    }

    @Test
    fun testParseMuslimWithColon() {
        val result = ReferenceParser.parse("muslim:8")
        assertNotNull(result)
        assertEquals("muslim", result?.collection)
        assertEquals("8", result?.hadithNumber)
    }

    @Test
    fun testParseArabicReference() {
        val resultBukhari = ReferenceParser.parse("صحيح البخاري 1")
        assertNotNull(resultBukhari)
        assertEquals("bukhari", resultBukhari?.collection)
        assertEquals("1", resultBukhari?.hadithNumber)

        val resultMuslim = ReferenceParser.parse("صحيح مسلم 100")
        assertNotNull(resultMuslim)
        assertEquals("muslim", resultMuslim?.collection)
        assertEquals("100", resultMuslim?.hadithNumber)
    }

    @Test
    fun testNonSupportedCollectionsDoNotMatch() {
        val nonSupportedQueries = listOf(
            "abu dawud 4941",
            "Jami at-Tirmidhi 2516",
            "Sunan an-Nasa'i 5005",
            "Sunan Ibn Majah 224",
            "Muwatta Malik 8",
            "Musnad Ahmad 23489",
            "40 Nawawi 1",
            "الأربعون النووية 7"
        )
        for (q in nonSupportedQueries) {
            val result = ReferenceParser.parse(q)
            assertNull("Collection for '$q' should be null as only Bukhari and Muslim are supported", result?.collection)
        }
    }

    @Test
    fun testParseNumberOnly() {
        val result = ReferenceParser.parse("13")
        assertNotNull(result)
        assertNull(result?.collection)
        assertEquals("13", result?.hadithNumber)
    }

    @Test
    fun testParseHadithPrefix() {
        val result = ReferenceParser.parse("hadith 224")
        assertNotNull(result)
        assertNull(result?.collection)
        assertEquals("224", result?.hadithNumber)
    }
}
