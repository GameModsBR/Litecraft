package br.com.gamemods.litecraft.api.core.collections

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class ImmutableCollectionTest {
    @Test
    fun toImmutableSet() {
        val list = mutableListOf(1,2,2,3,2,3)
        val immutableList = list.toImmutableArrayList()
        val immutableSet = immutableList.toImmutableSet()
        assertEquals(setOf(1,2,3), immutableSet)
        assertNotEquals(setOf(1,2,3,4), immutableSet)
    }
}
