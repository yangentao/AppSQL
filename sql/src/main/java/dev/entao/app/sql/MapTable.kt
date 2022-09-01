@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.app.sql

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.database.sqlite.transaction
import dev.entao.app.basic.*
import kotlin.reflect.KProperty

/**
 * Created by entaoyang@163.com on 2017-03-24.
 */

@KeepMembers
class MapTable(val db: SQLiteDatabase, val table: String) {

    init {
        synchronized(nameSet) {
            if (table !in nameSet) {
                nameSet.add(table)
                db.createTable(table, "$KEY TEXT PRIMARY KEY", "$VAL TEXT")
                db.createIndex(table, "value")
            }
        }
    }

    operator fun <V> setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        this.put(property.userName, value)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <V> getValue(thisRef: Any?, property: KProperty<*>): V {
        val v = get(property.userName) ?: property.userDefaultValueText
        return if (v == null) {
            if (property.returnType.isMarkedNullable) {
                null as V
            } else {
                property.defaultValueByType as V
            }
        } else {
            property.valueByText(v) as V
        }
    }

    fun trans(block: (MapTable) -> Unit) {
        db.transaction {
            block(this@MapTable)
        }
    }

    fun toHashMap(): HashMap<String, String> {
        val map = HashMap<String, String>(512)
        toMap(map)
        return map
    }

    fun toMap(map: MutableMap<String, String>) {
        val c = db.query("select $KEY, $VAL from $table", emptyList()) ?: return
        c.eachRow {
            map[it.getString(0)] = it.getString(1)
        }
    }

    fun putAll(map: Map<String, String>) {
        this.trans {
            for ((k, v) in map) {
                db.replaceX(table, KEY to k, VAL to v)
            }
        }

    }

    fun findKey(value: String): String? {
        return db.query("SELECT $KEY from $table where $VAL = ? limit 1", listOf(value))?.resultString
    }

    fun has(key: String): Boolean {
        return db.query("select $VAL from $table where $KEY=? limit 1", listOf(key))?.resultExists ?: false
    }

    operator fun get(key: String): String? {
        return db.query("select $VAL from $table where $KEY=? limit 1", listOf(key))?.resultString
    }

    operator fun set(key: String, value: String?) {
        if (value == null) {
            remove(key)
        } else {
            db.replaceX(table, KEY to key, VAL to value)
        }
    }

    fun getString(key: String): String? {
        return get(key)
    }

    fun putString(key: String, value: String?) {
        set(key, value)
    }

    fun put(key: String, value: Any?) {
        return set(key, value?.toString())
    }

    fun getInt(key: String): Int? {
        return getString(key)?.toIntOrNull()
    }

    fun getLong(key: String): Long? {
        return getString(key)?.toLongOrNull()
    }

    fun getDouble(key: String): Double? {
        return getString(key)?.toDoubleOrNull()
    }

    fun getBool(key: String): Boolean? {
        return getString(key)?.toBoolean()
    }

//    fun getYsonObject(key: String): YsonObject? {
//        val s = this.getString(key) ?: return null
//        return YsonObject(s)
//    }
//
//    fun getYsonArray(key: String): YsonArray? {
//        val s = this.getString(key) ?: return null
//        return YsonArray(s)
//    }

    fun remove(key: String): Int {
        return db.delete(table, "$KEY = ?", arrayOf(key))
    }

    fun removeAll(): Int {
        return db.delete(table, null, null)
    }

    fun dumpAll() {
        val map = toHashMap()
        for ((k, v) in map) {
            Log.d("SQL", "$k = $v")
        }

    }

    companion object {
        const val KEY = "key"
        const val VAL = "value"
        private val nameSet = HashSet<String>()
    }
}

