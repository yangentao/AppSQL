@file:Suppress("unused")

package dev.entao.app.sql

import android.database.Cursor


class RowData(val map: MutableMap<String, Any?> = LinkedHashMap(16)) {

    fun isNull(key: String): Boolean {
        return map[key] == null
    }

    fun str(key: String): String? {
        return map[key]?.toString()
    }

    fun int(key: String): Int? {
        val v = map[key] ?: return null
        if (v is Number) {
            return v.toInt()
        }
        return v.toString().toIntOrNull()
    }

    fun long(key: String): Long? {
        val v = map[key] ?: return null
        if (v is Number) {
            return v.toLong()
        }
        return v.toString().toLongOrNull()
    }

    fun float(key: String): Float? {
        val v = map[key] ?: return null
        if (v is Number) {
            return v.toFloat()
        }
        return v.toString().toFloatOrNull()
    }

    fun double(key: String): Double? {
        val v = map[key] ?: return null
        if (v is Number) {
            return v.toDouble()
        }
        return v.toString().toDoubleOrNull()
    }

    fun blob(key: String): ByteArray? {
        return map[key] as? ByteArray
    }


    companion object {
        fun rowOf(c: Cursor): RowData {
            val map = LinkedHashMap<String, Any?>()
            val colCount = c.columnCount
            for (i in 0 until colCount) {
                val key = c.getColumnName(i)
                val v: Any? = when (c.getType(i)) {
                    Cursor.FIELD_TYPE_NULL -> null
                    Cursor.FIELD_TYPE_INTEGER -> c.getLong(i)
                    Cursor.FIELD_TYPE_FLOAT -> c.getDouble(i)
                    Cursor.FIELD_TYPE_STRING -> c.getString(i)
                    Cursor.FIELD_TYPE_BLOB -> c.getBlob(i)
                    else -> null
                }
                map[key] = v
            }
            return RowData(map)
        }
    }
}