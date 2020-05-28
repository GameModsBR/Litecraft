package br.com.gamemods.litecraft.api.core.collections

import java.util.stream.Stream
import kotlin.streams.toList

fun <T> Array<T>.toImmutableList() : ImmutableList<T> = ImmutableArrayList(this)
fun <T> Iterable<T>.toImmutableList() : ImmutableList<T> = (this as? Collection<T>)?.toImmutableList() ?: ImmutableArrayList(this)
fun <T> Collection<T>.toImmutableList() : ImmutableList<T> = (this as? ImmutableCollection<T>)?.toImmutableList() ?: ImmutableArrayList(this)
fun <T> Sequence<T>.toImmutableList() : ImmutableList<T> = ImmutableArrayList(toList())
fun <T> Stream<T>.toImmutableList() : ImmutableList<T> = ImmutableArrayList(toList())

fun <T> Array<T>.toImmutableArrayList() = ImmutableArrayList(this)
fun <T> Iterable<T>.toImmutableArrayList() = (this as? Collection<T>)?.toImmutableArrayList() ?: ImmutableArrayList(this)
fun <T> Collection<T>.toImmutableArrayList() = (this as? ImmutableCollection<T>)?.toImmutableArrayList() ?: ImmutableArrayList(this)
fun <T> Sequence<T>.toImmutableArrayList() = ImmutableArrayList(toList())
fun <T> Stream<T>.toImmutableArrayList() = ImmutableArrayList(toList())

fun <T> Array<T>.toImmutableSet() : ImmutableSet<T> = ImmutableHashSet(this)
fun <T> Iterable<T>.toImmutableSet() : ImmutableSet<T> = (this as? Collection<T>)?.toImmutableSet() ?: ImmutableHashSet(this)
fun <T> Collection<T>.toImmutableSet() : ImmutableSet<T> = (this as? ImmutableCollection<T>)?.toImmutableSet() ?: ImmutableHashSet(this)
fun <T> Sequence<T>.toImmutableSet() : ImmutableSet<T> = ImmutableHashSet(toList())
fun <T> Stream<T>.toImmutableSet() : ImmutableSet<T> = ImmutableHashSet(toList())

fun <T> Array<T>.toImmutableHashSet() = ImmutableHashSet(this)
fun <T> Iterable<T>.toImmutableHashSet() = (this as? Collection<T>)?.toImmutableHashSet() ?: ImmutableHashSet(this)
fun <T> Collection<T>.toImmutableHashSet() = (this as? ImmutableCollection<T>)?.toImmutableHashSet() ?: ImmutableHashSet(this)
fun <T> Sequence<T>.toImmutableHashSet() = ImmutableHashSet(toList())
fun <T> Stream<T>.toImmutableHashSet() = ImmutableHashSet(toList())

fun <T> Array<T>.toImmutableArraySet() = ImmutableArraySet(this)
fun <T> Iterable<T>.toImmutableArraySet() = (this as? Collection<T>)?.toImmutableArraySet() ?: ImmutableArraySet(this)
fun <T> Collection<T>.toImmutableArraySet() = (this as? ImmutableCollection<T>)?.toImmutableArraySet() ?: ImmutableArraySet(this)
fun <T> Sequence<T>.toImmutableArraySet() = ImmutableArraySet(toList())
fun <T> Stream<T>.toImmutableArraySet() = ImmutableArraySet(toList())

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> = (this as? ImmutableMap<K, V>) ?: ImmutableHashMap(this)
fun <K, V> Map<K, V>.toImmutableHashMap() = (this as? ImmutableHashMap<K, V>)?.toImmutableHashMap() ?: ImmutableHashMap(this)
fun <K, V> Map<K, V>.toImmutableArrayMap() = (this as? ImmutableHashMap<K, V>)?.toImmutableArrayMap() ?:ImmutableArrayMap(this)

