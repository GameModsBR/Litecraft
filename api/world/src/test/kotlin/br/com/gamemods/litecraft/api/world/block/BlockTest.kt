package br.com.gamemods.litecraft.api.world.block

import br.com.gamemods.litecraft.api.core.collections.ImmutableArraySet
import br.com.gamemods.litecraft.api.world.block.property.Mining
import org.junit.jupiter.api.Test

internal class BlockTest {
    @Test
    fun test() {
        var x: Int
        val block = Block(ImmutableArraySet(emptySet()))
        block.ifHas<Mining> {

        }
        block.ifDontHave<Mining> {
            x = 2
        }
    }
}
