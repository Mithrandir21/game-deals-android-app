package pm.bam.gamedeals.feature.game.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValuePerHourTest {

    @Test
    fun `scales a prefix-symbol price to two decimals`() {
        // $59.99 over 30h (108000s) → $2.00/h
        assertEquals("$2.00", perHourDenominated("$59.99", 59.99, 108_000))
    }

    @Test
    fun `sub-unit per-hour keeps two decimals`() {
        // €40.00 over 100h (360000s) → €0.40/h
        assertEquals("€0.40", perHourDenominated("€40.00", 40.0, 360_000))
    }

    @Test
    fun `zero-decimal currency stays whole`() {
        // ¥6800 over 30h (108000s) → ¥227/h (no minor unit)
        assertEquals("¥227", perHourDenominated("¥6800", 6800.0, 108_000))
    }

    @Test
    fun `trailing currency code is preserved`() {
        // "40.00 PLN" over 100h → "0.40 PLN"
        assertEquals("0.40 PLN", perHourDenominated("40.00 PLN", 40.0, 360_000))
    }

    @Test
    fun `null when playtime missing or zero`() {
        assertNull(perHourDenominated("$59.99", 59.99, 0))
        assertNull(perHourDenominated("$59.99", 59.99, -1))
    }

    @Test
    fun `null for free games`() {
        assertNull(perHourDenominated("$0.00", 0.0, 108_000))
    }
}
