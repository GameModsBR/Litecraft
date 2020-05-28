package br.com.gamemods.litecraft.api.world.block.property

import br.com.gamemods.litecraft.api.core.collections.ImmutableMap
import br.com.gamemods.litecraft.api.world.item.tool.Tool

data class Mining(
    val speedMultipliers: ImmutableMap<Tool, Double>
): BlockProperty()
