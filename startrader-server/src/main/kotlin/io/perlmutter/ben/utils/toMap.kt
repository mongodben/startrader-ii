package io.perlmutter.ben.utils

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun Any.toMap(): Map<String, Any?> {
    return this::class.memberProperties.associateBy({ it.name }, { (it as KProperty1<Any, Any?>).get(this) })
}
