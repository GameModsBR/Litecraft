package br.com.gamemods.litecraft.api.world.block

import br.com.gamemods.litecraft.api.world.block.property.BlockProperty
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <reified T: BlockProperty> Block.get() = getProperty(T::class.java)

inline fun <reified T: BlockProperty> Block.with(function: (T) -> Unit) {
    contract { callsInPlace(function, InvocationKind.EXACTLY_ONCE) }
    get<T>()?.let(function)
}

inline fun <reified T: BlockProperty> Block.ifHas(crossinline function: (T) -> Unit) {
    contract { callsInPlace(function, InvocationKind.AT_MOST_ONCE) }
    get<T>()?.let(function)
}

inline fun <reified T: BlockProperty> Block.ifDontHave(function: () -> Unit) {
    contract { callsInPlace(function, InvocationKind.AT_MOST_ONCE) }
    if (!hasProperty(T::class.java)) {
        function()
    }
}
