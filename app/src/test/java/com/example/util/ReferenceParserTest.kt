package com.example.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ReferenceParserTest {

    @Test
    fun testParseBukhariWithSpace() {
        val result = ReferenceParser.parse("bukhari 1")
        assertNotNull(result)
        assertEquals("bukhari", result?.collection)
        assertEquals("1", result?.hadithNumber)
    }

    @Test
    fun testParseMuslimWithColon() {
        val result = ReferenceParser.parse("muslim:8")
        assertNotNull(result)
        assertEquals("muslim", result?.collection)
        assertEquals("8", result?.hadithNumber)
    }

    @Test
    fun testParseAbuDawud() {
        val result = ReferenceParser.parse("abu dawud 4941")
        assertNotNull(result)
        assertEquals("abudawud", result?.collection)
        assertEquals("4941", result?.hadithNumber)
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
