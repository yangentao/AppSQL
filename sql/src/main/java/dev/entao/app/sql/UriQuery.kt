@file:Suppress("unused", "MemberVisibilityCanBePrivate", "PropertyName")

package dev.entao.app.sql

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * Created by entaoyang@163.com on 16/5/13.
 */

class UriQuery(val context: Context, val uri: Uri) {
    val ID = "_id"

    private var columns = ArrayList<String>()

    private var orderStr: String = ""
    private var limitStr: String = ""
    private var whereStr: String = ""
    private var argList = ArrayList<Any>()

    constructor(context: Context, uri: Uri, id: Long) : this(context, ContentUris.withAppendedId(uri, id))

    fun colunms(vararg cols: String): UriQuery {
        columns.addAll(cols.toList())
        return this
    }

    fun where(w: Where?): UriQuery {
        if (w != null) {
            this.whereStr = w.toString()
            this.argList.addAll(w.args)
        }
        return this
    }

    fun where(s: String, args: List<Any> = emptyList()): UriQuery {
        this.whereStr = s
        this.argList.addAll(args)
        return this
    }


    fun whereId(_id: Long): UriQuery {
        return where("$ID = $_id")
    }

    fun limit(limit: Int): UriQuery {
        if (limit > 0) {
            limitStr = " LIMIT $limit "
        }
        return this
    }

    fun limit(limit: Int, offset: Int): UriQuery {
        if (limit > 0 && offset >= 0) {
            limitStr = " LIMIT $limit OFFSET $offset "
        }
        return this
    }

    fun orderBy(sortOrder: String): UriQuery {
        this.orderStr = sortOrder
        return this
    }

    fun orderBy(col: String, asc: Boolean): UriQuery {
        this.orderStr = col + if (asc) " ASC " else " DESC "
        return this
    }

    fun asc(col: String): UriQuery {
        return orderBy(col, true)
    }

    fun desc(col: String): UriQuery {
        return orderBy(col, false)
    }

    fun query(): Cursor? {
        val od = if (orderStr.isEmpty()) {
            "_id ASC $limitStr"
        } else {
            "$orderStr $limitStr"
        }
        try {
            return context.contentResolver.query(
                uri,
                columns.toTypedArray(),
                whereStr,
                argList.map { it.toString() }.toTypedArray(),
                od
            )
        } catch (ex: Exception) {
        }
        return null
    }

    companion object {
        fun select(context: Context, uri: Uri, vararg cols: String): UriQuery {
            return UriQuery(context, uri).colunms(*cols)
        }
    }

}