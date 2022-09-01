@file:Suppress("unused")

package dev.entao.app.sql

import android.database.Cursor


class RowData(val map: MutableMap<String, Any?> = LinkedHashMap(16)) : CursorToModel {

    override fun fromCursor(cursor: Cursor) {
        val colCount = cursor.columnCount
        for (i in 0 until colCount) {
            val key = cursor.getColumnName(i)
            val v: Any? = when (cursor.getType(i)) {
                Cursor.FIELD_TYPE_NULL -> null
                Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(i)
                Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(i)
                Cursor.FIELD_TYPE_STRING -> cursor.getString(i)
                Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(i)
                else -> null
            }
            map[key] = v
        }
    }

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



}