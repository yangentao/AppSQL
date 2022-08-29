@file:Suppress("unused")

package dev.entao.app.sql

import android.database.Cursor


inline fun <reified T : Any> Cursor.toList(block: (Cursor) -> T): ArrayList<T> {
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

inline fun <R> Cursor.firstRow(block: (Cursor) -> R): R? {
    return this.use {
        if (it.moveToNext()) {
            block(it)
        } else null
    }
}


val Cursor.existRowClose: Boolean get() = this.use { it.moveToNext() }


val Cursor.firstIntClose: Int get() = firstRow { it.getInt(0) } ?: 0
val Cursor.firstLongClose: Long get() = firstRow { it.getLong(0) } ?: 0L
val Cursor.firstStringClose: String? get() = firstRow { it.getString(0) }
val Cursor.firstDataClose: RowData? get() = firstRow { it.currentData }

val Cursor.currentData: RowData get() = RowData.rowOf(this)

val Cursor.listDataClose: List<RowData>
    get() {
        val ls = ArrayList<RowData>()
        eachRow { ls += it.currentData }
        return ls
    }
