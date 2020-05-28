package br.com.gamemods.litecraft.api.world.block.property

class RegisteredBlockProperty<Property: BlockProperty>(val property: Property) {
    val type = property::class.java
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegisteredBlockProperty<*>

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun toString(): String {
        return "RegisteredBlockProperty($property)"
    }
}
