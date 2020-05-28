@file:Suppress("EqualsOrHashCode")

package br.com.gamemods.litecraft.api.core.collections

import java.util.Collections.unmodifiableMap

sealed class ImmutableMap<K, V>: Map<K, V> {
    abstract override val keys: ImmutableSet<K>
    abstract override val values: ImmutableCollection<V>
    abstract override val entries: ImmutableSet<Map.Entry<K, V>>
    
    private val string by lazy(::computeToString)
    private var hashed = false
    private var hash = 0
    final override fun toString() = string
    final override fun hashCode() = if (hashed) hash else calculateHash().also { hash = it }
    protected abstract fun calculateHash(): Int
    protected open fun computeToString() = toMap().toString()

    fun toImmutableMap() = this
    open fun toImmutableHashMap() = ImmutableHashMap(this)
    open fun toImmutableArrayMap() = ImmutableArrayMap(this)
}

class ImmutableHashMap<K, V> private constructor(private val base: Map<K, V>, @Suppress("UNUSED_PARAMETER") unit: Unit): ImmutableMap<K, V>(), Map<K, V> by base {
    constructor(map: Map<K, V>): this(unmodifiableMap(map.toMap()), Unit)
    
    override val keys by lazy { base.keys.toImmutableSet() }
    override val values by lazy { base.values.toImmutableList() }
    override val entries by lazy { base.entries.toImmutableSet() }

    override fun equals(other: Any?) = base == other
    override fun calculateHash() = base.hashCode()
    override fun computeToString() = base.toString()

    override fun toImmutableHashMap() = this
}

class ImmutableArrayMap<K, V>(map: Map<K, V>): ImmutableMap<K, V>() {
    override val keys = map.keys.toImmutableArraySet()
    override val values = map.values.toImmutableArrayList()
    override val entries = map.entries.toImmutableArraySet()
    
    override fun calculateHash() = entries.sumBy(Any::hashCode)

    override val size get() = keys.size

    override fun containsKey(key: K) = key in keys
    override fun containsValue(value: V) = value in values
    override fun get(key: K) = keys.indexOf(key).takeIf { it != -1 }?.let { values[it] }
    override fun isEmpty() = size == 0

    override fun toImmutableArrayMap() = this
}
