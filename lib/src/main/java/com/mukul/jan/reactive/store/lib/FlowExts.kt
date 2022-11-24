package com.mukul.jan.reactive.store.lib

import kotlinx.coroutines.flow.*

/**
 * Example of Usage
 * val flow = flow {
 *    emit(Fruit(name = "apple", color = "red"))
 *    emit(Fruit(name = "apple", color = "red"))
 *    emit(Fruit(name = "banana", color = "yellow"))
 *    emit(Fruit(name = "pineapple", color = "yellow"))
 *    emit(Fruit(name = "fig", color = "brown"))
 * }
 *
 *  //Usage : 1
 * flow.map { it.color }.ifChanged()
 *  .collect { color ->
 *      //collect only when color is different from previous color
 *  }
 *
 *  //Usage : 2
 *  flow.ifChanged { it.color }
 *  .collect { fruit ->
 *      //collect only when color is different from previous color
 *  }
 *
 *  //Usage : 3
 *  flow.ifChanged()
 *  .collect { fruit ->
 *     //collect only when fruit is different from previous fruit
 *  }
 */

fun <T> Flow<T>.ifChanged(): Flow<T> {
    return ifChanged { it }
}

fun <T, R> Flow<T>.ifChanged(transform: (T) -> R): Flow<T> {
    var observedValueOnce = false
    var lastMappedValue: R? = null

    return filter { value ->
        val mapped = transform.invoke(value)
        if (!observedValueOnce || mapped != lastMappedValue) {
            lastMappedValue = mapped
            observedValueOnce = true
            true
        } else {
            false
        }
    }
}

