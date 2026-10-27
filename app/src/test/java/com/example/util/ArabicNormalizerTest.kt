package com.example.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArabicNormalizerTest {

    @Test
    fun testNormalizeStripsTashkeel() {
        val input = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ"
        val normalized = ArabicNormalizer.normalize(input)
        // Stripped tashkeel and alef normalized
        assertTrue(normalized.contains("انما"))
        assertTrue(normalized.contains("الاعمال"))
        assertTrue(normalized.contains("بالنيات"))
    }

    @Test
    fun testAlefNormalization() {
        val input = "أحمد إبراهيم آدم"
        val normalized = ArabicNormalizer.normalize(input)
        assertEquals("احمد ابراهيم ادم", normalized)
    }

    @Test
    fun testTehMarbutaNormalization() {
        val input = "صلاة زكاة رحمة"
        val normalized = ArabicNormalizer.normalize(input)
        assertEquals("صلاه زكاه رحمه", normalized)
    }

    @Test
    fun testContainsNormalized() {
        val source = "حَدَّثَنَا عُمَرُ بْنُ الْخَطَّابِ رَضِيَ اللَّهُ عَنْهُ"
        assertTrue(ArabicNormalizer.containsNormalized(source, "عمر"))
        assertTrue(ArabicNormalizer.containsNormalized(source, "الخطاب"))
        assertTrue(ArabicNormalizer.containsNormalized(source, "عُمَر"))
        assertFalse(ArabicNormalizer.containsNormalized(source, "عثمان"))
    }
}
