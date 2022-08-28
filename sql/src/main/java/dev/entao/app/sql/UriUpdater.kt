@file:Suppress("unused", "MemberVisibilityCanBePrivate", "PrivatePropertyName")

package dev.entao.app.sql

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri

class UriUpdater(context: Context) {
    private val _ID = "_id"
    private val resolver: ContentResolver = context.contentResolver

    fun insert(uri: Uri, block: (ContentValues) -> Unit): Uri? {
        val v = ContentValues()
        block(v)
        return insert(uri, v)
    }


    fun insert(uri: Uri, values: ContentValues): Uri? {
        return resolver.insert(uri, values)
    }


    fun insert(uri: Uri, key: String, value: String): Uri? {
        val c = ContentValues()
        c.putAny(key, value)
        return insert(uri, c)
    }

    fun insert(uri: Uri, key: String, value: Long): Uri? {
        val c = ContentValues()
        c.putAny(key, value)
        return insert(uri, c)
    }

    fun insert(uri: Uri, key: String, value: String, key2: String, value2: String): Uri? {
        val c = ContentValues()
        c.putAny(key, value)
        c.putAny(key2, value2)
        return insert(uri, c)
    }

    fun insert(uri: Uri, key: String, value: String, key2: String, value2: Long): Uri? {
        val c = ContentValues()
        c.putAny(key, value)
        c.putAny(key2, value2)
        return insert(uri, c)
    }

    fun update(uri: Uri, values: ContentValues): Int {
        return update(uri, values, null)
    }

    fun update(uri: Uri, w: Where?, block: (ContentValues) -> Unit): Int {
        val v = ContentValues()
        block(v)
        return update(uri, v, w)
    }

    fun update(uri: Uri, values: ContentValues, w: Where?): Int {
        return resolver.update(uri, values, w?.toString(), w?.sqlArgs)
    }

    fun update(uri: Uri, values: Map<String, Any?>, w: Where?): Int {
        return update(uri, values.toContentValues, w)
    }


    fun update(uri: Uri, key: String, value: Any?): Int {
        val c = ContentValues()
        c.putAny(key, value)
        return update(uri, c, null)
    }


    fun update(uri: Uri, id: Long, key: String, value: Long): Int {
        val c = ContentValues()
        c.putAny(_ID, id)
        c.putAny(key, value)
        return update(uri, c, null)
    }

    fun update(uri: Uri, id: Long, key: String, value: String): Int {
        val c = ContentValues()
        c.putAny(_ID, id)
        c.putAny(key, value)
        return update(uri, c, null)
    }


    fun delete(uri: Uri): Int {
        return resolver.delete(uri, null, null)
    }

    fun delete(uri: Uri, w: Where): Int {
        return resolver.delete(uri, w.toString(), w.sqlArgs)
    }

    fun delete(uri: Uri, id: Long): Int {
        return delete(uri, _ID EQ id)
    }

    fun delete(uri: Uri, key: String, value: Long): Int {
        return delete(uri, key EQ value)
    }

    fun delete(uri: Uri, key: String, value: String): Int {
        return delete(uri, key EQ value)
    }

    fun delete(uri: Uri, keyValue: Pair<String, String>): Int {
        return delete(uri, keyValue.first EQ keyValue.second)
    }

}
