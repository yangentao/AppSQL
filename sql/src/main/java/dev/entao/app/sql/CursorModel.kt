@file:Suppress("unused")

package dev.entao.app.sql

import android.database.Cursor
import dev.entao.app.basic.Exclude
import dev.entao.app.basic.isPublic
import dev.entao.app.basic.setInstValue
import dev.entao.app.basic.userName
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties


fun <T : Any> Cursor.firstModel(block: () -> T): T? {
    return this.firstRow {
        val m = block()
        fillModel(m)
        m
    }
}

inline fun <reified T : Any> Cursor.firstModel(): T? {
    return firstModel { T::class.createInstance() }
}

inline fun <reified T : Any> Cursor.listModels(): List<T> {
    return listModels { T::class.createInstance() }
}


inline fun <reified T : Any> Cursor.listModels(block: () -> T): List<T> {
    val ps = T::class.propsOfModel
    val ls = ArrayList<T>(256)
    this.eachRow {
        val m = block()
        fillModel(m, ps)
        ls += m
    }
    return ls
}


fun Cursor.fillModel(model: Any, ps: List<KMutableProperty1<*, *>>) {
    val c = this
    val colCount = c.columnCount
    for (i in 0 until colCount) {
        val key = c.getColumnName(i)
        val p = ps.firstOrNull {
            it.userName == key
        } ?: continue
        val ptype = p.returnType
        val v: Any? = when (c.getType(i)) {
            Cursor.FIELD_TYPE_INTEGER -> {
                if (ptype.classifier == Long::class) {
                    c.getLong(i)
                } else {
                    c.getInt(i)
                }
            }
            Cursor.FIELD_TYPE_FLOAT -> {
                if (ptype.classifier == Double::class) {
                    c.getDouble(i)
                } else {
                    c.getFloat(i)
                }
            }
            Cursor.FIELD_TYPE_STRING -> c.getString(i)
            Cursor.FIELD_TYPE_BLOB -> c.getBlob(i)
            else -> null
        }
        if (v != null || p.returnType.isMarkedNullable) {
            p.setInstValue(model, v)
        }
    }
}

fun Cursor.fillModel(model: Any) {
    this.fillModel(model, model::class.propsOfModel)
}

val KClass<*>.propsOfModel: List<KMutableProperty1<*, *>>
    get() {
        return this.memberProperties.filter {
            it is KMutableProperty1<*, *> && !it.hasAnnotation<Exclude>() && it.isPublic
        }.map { it as KMutableProperty1<*, *> }
    }
