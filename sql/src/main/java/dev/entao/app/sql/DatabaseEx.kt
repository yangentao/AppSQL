@file:Suppress("unused")

package dev.entao.app.sql

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import dev.entao.app.basic.notEmpty
import dev.entao.app.basic.plusAssign


fun SQLiteDatabase.dumpTable(tableName: String) {
    val c = this.query("SELECT * FROM $tableName") ?: return
    val sb = StringBuilder(200)
    c.listDataClose.forEach {
        sb.setLength(0)
        it.map.forEach { e ->
            sb.append(e.value?.toString() ?: "null").append(", ")
        }
        Log.d("xlog", sb.toString())
    }
}

fun SQLiteDatabase.tables(): HashSet<String> {
    val all = HashSet<String>()
    val c = this.query("SELECT name FROM sqlite_master WHERE type='table'") ?: return all
    c.listDataClose.forEach {
        all += it.str("name") ?: ""
    }
    return all
}



fun SQLiteDatabase.indexs(): ArrayList<Pair<String, String>> {
    val all = ArrayList<Pair<String, String>>()
    val c = this.query("SELECT name, tbl_name FROM sqlite_master WHERE type='index'") ?: return all
    c.listDataClose.forEach {
        val a = it.str("name")!!
        val b = it.str("tbl_name")!!
        all += a to b
    }
    return all
}

fun SQLiteDatabase.indexInfo(indexName: String): HashSet<String> {
    val all = HashSet<String>()
    val c = this.query("PRAGMA index_info('$indexName')") ?: return all
    c.listDataClose.forEach {
        all += it.str("name")!!
    }
    return all
}

fun SQLiteDatabase.tableInfo(tableName: String): ArrayList<TableInfoItem> {
    val all = ArrayList<TableInfoItem>()
    val c = this.query("PRAGMA table_info('$tableName')", emptyList())
        ?: return all
    val ls = c.listDataClose
    ls.forEach {
        val item = TableInfoItem()
        item.cid = it.int("cid") ?: 0
        item.name = it.str("name") ?: ""
        item.type = it.str("type") ?: ""
        item.notNull = it.int("notnull")!! != 0
        item.defaultValue = it.str("dflt_value")
        item.pk = it.int("pk")!! != 0
        all.add(item)
    }
    return all
}

@SuppressLint("Recycle")
fun SQLiteDatabase.indexsOf(table: String): HashSet<String> {
    val all = HashSet<String>()
    val c = this.query("SELECT name FROM sqlite_master WHERE type='index' AND tbl_name='$table'") ?: return all
    c.listDataClose.forEach {
        val s = it.str("name")
        if (s != null) {
            all.add(s)
        }
    }
    return all
}

fun SQLiteDatabase.createIndex(table: String, vararg cols: String) {
    val s1 = cols.joinToString("_")
    val s2 = cols.joinToString(",")
    this.execSQL("CREATE INDEX IF NOT EXISTS ${table}_$s1 ON $table ( $s2 )")
}

fun SQLiteDatabase.addColumn(table: String, columnDef: String) {
    this.execSQL("ALTER TABLE $table ADD COLUMN $columnDef")
}


fun SQLiteDatabase.createTable(table: String, vararg columns: String) {
    this.createTable(table, columns.toList())
}

fun SQLiteDatabase.createTable(table: String, columns: List<String>) {
    val s = columns.joinToString(",")
    this.execSQL("CREATE TABLE IF NOT EXISTS $table ( $s )")
}

fun SQLiteDatabase.dropTable(table: String) {
    this.execSQL("DROP TABLE IF EXISTS $table")
}


fun SQLiteDatabase.countTable(table: String): Int {
    val c = this.query("SELECT count(*) FROM '$table'") ?: return 0
    return c.firstIntClose
}

fun SQLiteDatabase.existTable(tableName: String): Boolean {
    val sql = "SELECT * FROM sqlite_master WHERE type = 'table' AND name = '$tableName'"
    return this.query(sql)?.existRowClose ?: false
}

fun SQLiteDatabase.replaceX(table: String, vararg ps: Pair<String, String>) {
    val cv = ContentValues()
    ps.forEach {
        cv.put(it.first, it.second)
    }
    this.replace(table, null, cv)
}


fun SQLiteDatabase.exec(sql: String, args: List<Any>? = null): Boolean {
    try {
        this.execSQL(sql, args?.toTypedArray() ?: emptyArray())
        return true
    } catch (ex: Throwable) {
        ex.printStackTrace()
    }
    return false
}

fun SQLiteDatabase.query(sql: String, args: List<Any>? = null): Cursor? {
    return this.rawQuery(sql, args?.map { it.toString() }?.toTypedArray() ?: emptyArray<String>())
}

fun SQLiteDatabase.filter(table: String, w: Where?, orderBy: String?, limit: Int = 0, offset: Int = 0): Cursor? {
    val sb = StringBuilder(256)
    sb += "SELECT * FROM $table"
    w?.value?.notEmpty {
        sb += " WHERE $it"
    }
    orderBy?.notEmpty {
        sb += " ORDER BY $it"
    }
    if (limit > 0 && offset >= 0) {
        sb += " LIMIT $limit OFFSET $offset"
    }
    return this.query(sb.toString(), w?.args)
}



//"cid": 0,
//"name": "locale",
//"type": "TEXT",
//"notnull": 0,
//"dflt_value": null,
//"pk": 0
class TableInfoItem {
    var cid: Int = 0
    var name: String = ""
    var type: String = ""
    var notNull: Boolean = false
    var defaultValue: String? = null
    var pk: Boolean = false

    override fun toString(): String {
        return "$cid, $name, $type, $notNull, $defaultValue, $pk"
    }
}