package br.com.gamemods.litecraft.api.world.block

import br.com.gamemods.litecraft.api.core.collections.ImmutableArraySet
import br.com.gamemods.litecraft.api.core.collections.toImmutableHashMap
import br.com.gamemods.litecraft.api.world.block.property.BlockProperty
import br.com.gamemods.litecraft.api.world.block.property.RegisteredBlockProperty

data class Block(
    val properties: ImmutableArraySet<RegisteredBlockProperty<*>>
) {
    private val propertiesMap = properties.associate { it.type to it.property }.toImmutableHashMap()
    
    @Suppress("UNCHECKED_CAST")
    fun <T: BlockProperty> getProperty(propertyClass: Class<T>): T? = propertiesMap[propertyClass] as? T
    fun hasProperty(propertyClass: Class<out BlockProperty>) = propertyClass in propertiesMap
}
