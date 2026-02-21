package com.ethio.shop.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyUtilsTest {

    @Test
    fun `formatETB formats double correctly`() {
        val result = CurrencyUtils.formatETB(1234.56)
        assertEquals("ETB 1,234.56", result)
    }

    @Test
    fun `formatETB formats integer correctly`() {
        val result = CurrencyUtils.formatETB(5000.0)
        assertEquals("ETB 5,000.00", result)
    }

    @Test
    fun `formatETB formats zero correctly`() {
        val result = CurrencyUtils.formatETB(0.0)
        assertEquals("ETB 0.00", result)
    }

    @Test
    fun `parseETB parses formatted string correctly`() {
        val result = CurrencyUtils.parseETB("ETB 1,234.56")
        assertEquals(1234.56, result, 0.01)
    }

    @Test
    fun `parseETB handles string without commas`() {
        val result = CurrencyUtils.parseETB("ETB 1234.56")
        assertEquals(1234.56, result, 0.01)
    }

    @Test
    fun `isValidETB validates positive amount`() {
        val result = CurrencyUtils.isValidETB(100.0)
        assertEquals(true, result)
    }

    @Test
    fun `isValidETB rejects negative amount`() {
        val result = CurrencyUtils.isValidETB(-100.0)
        assertEquals(false, result)
    }

    @Test
    fun `isValidETB rejects zero amount`() {
        val result = CurrencyUtils.isValidETB(0.0)
        assertEquals(false, result)
    }
}