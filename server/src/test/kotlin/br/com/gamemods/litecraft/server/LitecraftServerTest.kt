package br.com.gamemods.litecraft.server

import org.junit.jupiter.api.Test

internal class LitecraftServerTest {

    @Test
    fun fruit() {
        val server = LitecraftServer()
        println(server.fruit("apple"))
    }
}
