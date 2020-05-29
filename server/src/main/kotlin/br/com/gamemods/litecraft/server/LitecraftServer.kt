package br.com.gamemods.litecraft.server

import br.com.gamemods.litecraft.api.world.block.Block

class LitecraftServer {
    var x: Block? = null
    fun fruit(banana: String): String {
        if (banana != "banana") {
            return "not a banana :("
        }
        return "i'm a gorilla xD"
    }
}
