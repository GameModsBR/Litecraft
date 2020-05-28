@file:Suppress("EqualsOrHashCode")

package br.com.gamemods.litecraft.api.core.collections

import java.util.*
import java.util.Collections.*
import java.util.stream.Stream
import kotlin.collections.ArrayList

sealed class ImmutableCollection<E>: Collection<E> {
    private val string by lazy(::computeToString)
    private var hashed = false
    private var hash = 0

    final override fun toString() = string
    final override fun hashCode() = if (hashed) hash else calculateHash().also { hash = it }
    protected abstract fun calculateHash(): Int
    protected open fun computeToString() = toList().toString()
    
    open fun toImmutableList(): ImmutableList<E> = toImmutableArrayList()
    open fun toImmutableArrayList() = ImmutableArrayList(this)
    open fun toImmutableSet(): ImmutableSet<E> = toImmutableHashSet()
    open fun toImmutableHashSet() = ImmutableHashSet(this)
    open fun toImmutableArraySet() = ImmutableArraySet(this)
}

sealed class ImmutableList<E>(private val base: List<E>): ImmutableCollection<E>(), List<E> by base {
    override fun toImmutableList() = this
    
    override fun subList(fromIndex: Int, toIndex: Int): ImmutableList<E> {
        val sub = base.subList(fromIndex, toIndex)
        if (sub.isEmpty()) {
            return empty()
        }
        return ImmutableSubList(sub)
    }

    override fun equals(other: Any?) = base == other
    override fun calculateHash() = base.hashCode()
    override fun computeToString() = base.toString()

    override fun stream() = base.stream()
    override fun parallelStream() = base.parallelStream()
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
    override fun spliterator() = (base as java.util.List<E>).spliterator()

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <E> empty() = EmptyImmutableList as ImmutableList<E>
    }
}

private object EmptyImmutableList: ImmutableList<Nothing?>(emptyList()) {
    override fun subList(fromIndex: Int, toIndex: Int): ImmutableList<Nothing?> {
        if (fromIndex != 0) throw IndexOutOfBoundsException(fromIndex)
        if (toIndex != 0) throw IndexOutOfBoundsException(toIndex)
        return this
    }

    override fun equals(other: Any?) = EMPTY_LIST == other
    override fun calculateHash() = EMPTY_LIST.hashCode()
    override fun computeToString() = "[]"

    override fun stream(): Stream<Nothing?> = Stream.empty()
    override fun parallelStream(): Stream<Nothing?> = Stream.empty()
    override fun spliterator(): Spliterator<Nothing?> = Spliterators.emptySpliterator()
}

class ImmutableArrayList<E>(data: Array<E>): ImmutableList<E>(unmodifiableList(data.copyOf().asList())), RandomAccess {
    @Suppress("UNCHECKED_CAST")
    constructor(collection: Collection<E>): this(ArrayList(collection).toArray() as Array<E>)
    constructor(iterable: Iterable<E>): this(iterable.toList())

    override fun toImmutableArrayList() = this
}

private class ImmutableSubList<E>(subList: List<E>): ImmutableList<E>(subList), RandomAccess

sealed class ImmutableSet<E>: ImmutableCollection<E>(), Set<E> {
    override fun toImmutableSet() = this
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Set<*> || other.size != this.size) return false
        return containsAll(other)
    }

    override fun calculateHash(): Int {
        var hash = 0
        val iterator = iterator()
        while (iterator.hasNext()) {
            val current: E? = iterator.next()
            if (current != null) hash += current.hashCode()
        }
        return hash
    }
}

class ImmutableHashSet<E> private constructor(private val base: Set<E>, @Suppress("UNUSED_PARAMETER") unit: Unit): ImmutableSet<E>(), Set<E> by base {
    constructor(collection: Collection<E>): this(unmodifiableSet(collection.toSet()), Unit)
    constructor(data: Array<E>): this(data.asList())
    constructor(iterable: Iterable<E>): this(iterable.toList())
    
    override fun toImmutableHashSet() = this

    override fun calculateHash() = base.hashCode()
    override fun computeToString() = base.toString()
    
    override fun stream() = base.stream()
    override fun parallelStream() = base.parallelStream()
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
    override fun spliterator() = (base as java.util.Set<E>).spliterator()
}

class ImmutableArraySet<E>(data: Array<E>): ImmutableSet<E>(), Set<E>, List<E> {
    @Suppress("UNCHECKED_CAST")
    constructor(collection: Collection<E>): this(ArrayList(collection).toArray() as Array<E>)
    constructor(iterable: Iterable<E>): this(iterable.toList())
    
    private val array = unmodifiableList(data.toSet().toList())
    override val size = array.size

    override fun toImmutableArraySet() = this

    override fun computeToString() = array.toString()

    override fun contains(element: E) = array.contains(element)
    override fun containsAll(elements: Collection<E>) = array.containsAll(elements)
    override fun isEmpty() = array.isEmpty()
    override fun iterator() = array.iterator()
    override fun get(index: Int): E = array[index]
    override fun indexOf(element: E) = array.indexOf(element)
    override fun lastIndexOf(element: E) = array.lastIndexOf(element)
    override fun listIterator() = array.listIterator()
    override fun listIterator(index: Int) = array.listIterator(index)
    override fun spliterator() = array.spliterator()
    override fun stream() = array.stream()
    override fun parallelStream() = array.parallelStream()
    override fun subList(fromIndex: Int, toIndex: Int): ImmutableList<E> {
        val sub = array.subList(fromIndex, toIndex)
        if (sub.isEmpty()) {
            return ImmutableList.empty()
        }
        return ImmutableSubList(sub)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other is List<*>) {
            val iterator1: ListIterator<E> = listIterator()
            val iterator2 = other.listIterator()
            while (iterator1.hasNext() && iterator2.hasNext()) {
                if (iterator1.next() != iterator2.next()) {
                    return false
                }
            }
            return !(iterator1.hasNext() || iterator2.hasNext())
        }
        if (other is Set<*>) return super.equals(other)
        return false
    }
}
