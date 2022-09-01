@file:Suppress("unused")

package dev.entao.app.sql

import android.database.Cursor

interface CursorToModel {
    fun fromCursor(cursor: Cursor)
}

fun <R : Any> Cursor.firstRow(block: (Cursor) -> R): R? {
    return this.use {
        if (it.moveToNext()) {
            block(it)
        } else null
    }
}

fun <T : Any> Cursor.toList(block: (Cursor) -> T): ArrayList<T> {
    val ls = ArrayList<T>(this.count + 8)
    this.use {
        while (it.moveToNext()) {
            ls += block(it)
        }
    }
    return ls
}


inline fun Cursor.eachRow(block: (Cursor) -> Unit) {
    this.use {
        while (it.moveToNext()) {
            block(it)
        }
    }
}


val Cursor.resultExists: Boolean get() = this.use { it.moveToNext() }


val Cursor.resultInt: Int get() = firstRow { it.getInt(0) } ?: 0
val Cursor.resultLong: Long get() = firstRow { it.getLong(0) } ?: 0L
val Cursor.resultString: String? get() = firstRow { it.getString(0) }

val Cursor.currentRowData: RowData get() = RowData().also { it.fromCursor(this) }

fun Cursor.firstRowData(): RowData? {
    return firstRow { it.currentRowData }
}

fun Cursor.toRowDataList(): List<RowData> {
    return this.toList {
        it.currentRowData
    }
}



