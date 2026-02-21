package com.ethio.shop.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneUtilsTest {

    @Test
    fun `formatEthiopianPhone adds country code`() {
        val result = PhoneUtils.formatEthiopianPhone("911234567")
        assertEquals("+251911234567", result)
    }

    @Test
    fun `formatEthiopianPhone handles already formatted number`() {
        val result = PhoneUtils.formatEthiopianPhone("+251911234567")
        assertEquals("+251911234567", result)
    }

    @Test
    fun `formatEthiopianPhone handles 0 prefix`() {
        val result = PhoneUtils.formatEthiopianPhone("0911234567")
        assertEquals("+251911234567", result)
    }

    @Test
    fun `isValidEthiopianPhone validates correct format`() {
        val result = PhoneUtils.isValidEthiopianPhone("+251911234567")
        assertTrue(result)
    }

    @Test
    fun `isValidEthiopianPhone validates without country code`() {
        val result = PhoneUtils.isValidEthiopianPhone("0911234567")
        assertTrue(result)
    }

    @Test
    fun `isValidEthiopianPhone rejects invalid length`() {
        val result = PhoneUtils.isValidEthiopianPhone("09123456")
        assertFalse(result)
    }

    @Test
    fun `isValidEthiopianPhone rejects invalid prefix`() {
        val result = PhoneUtils.isValidEthiopianPhone("0811234567")
        assertFalse(result)
    }

    @Test
    fun `maskPhone masks middle digits`() {
        val result = PhoneUtils.maskPhone("+251911234567")
        assertEquals("+2519***4567", result)
    }

    @Test
    fun `maskPhone handles short number`() {
        val result = PhoneUtils.maskPhone("091234567")
        assertEquals("09***4567", result)
    }
}