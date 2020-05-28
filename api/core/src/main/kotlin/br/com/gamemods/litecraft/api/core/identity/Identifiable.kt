package br.com.gamemods.litecraft.api.core.identity

interface Identifiable<K: Key> {
    val key: K
}
