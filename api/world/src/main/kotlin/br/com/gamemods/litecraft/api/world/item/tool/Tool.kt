package br.com.gamemods.litecraft.api.world.item.tool

abstract class Tool {
    companion object {
        val HAND = HandTool()
        val PICKAXE = PickaxeTool()
        val SHOVEL = ShovelTool()
        val AXE = AxeTool()
        val SWORD = SwordTool()
        val SHEAR = ShearTool()
        val HOE = HoeTool()
    }
}
