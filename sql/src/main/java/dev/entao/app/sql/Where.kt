package dev.entao.app.sql


import dev.entao.app.basic.userName
import kotlin.reflect.KProperty

/**
 * Created by entaoyang@163.com on 2018-07-19.
 */


class Where(val value: String) {
    val args = ArrayList<Any>()

    val sqlArgs: Array<String>
        get() {
            return args.map(Any::toString).toTypedArray()
        }

    fun addArg(s: Any): Where {
        args.add(s)
        return this
    }

    fun addArgs(s: Collection<Any>): Where {
        args.addAll(s)
        return this
    }

    override fun toString(): String {
        return value
    }
}


fun IsNull(col: String): Where {
    return Where("$col IS NULL")
}

fun IsNotNull(col: String): Where {
    return Where("$col IS NOT NULL")
}

fun IsNull(p: KProperty<*>): Where {
    val col = p.userName
    return IsNull(col)
}

fun IsNotNull(p: KProperty<*>): Where {
    val col = p.userName
    return IsNotNull(col)
}

fun EQS(a: Pair<KProperty<*>, Any?>, vararg ps: Pair<KProperty<*>, Any?>): Where {
    var w: Where? = a.first.EQ(a.second)
    for (p in ps) {
        w = w AND p.first.EQ(p.second)
    }
    return w!!
}

infix fun KProperty<*>.EQ(value: Any?): Where {
    return this.userName.EQ(value)
}

infix fun String.EQ(value: Any?): Where {
    return when (value) {
        null -> IsNull(this)
        is Number -> Where("$this = $value")
        else -> Where("$this = ?").addArg(value)
    }
}

infix fun KProperty<*>.NE(value: Any?): Where {
    return this.userName.NE(value)
}

infix fun String.NE(value: Any?): Where {
    val s = this
    return when (value) {
        null -> IsNotNull(s)
        is Number -> Where("$s <> $value")
        else -> Where("$s <> ?").addArg(value)
    }
}


infix fun KProperty<*>.GE(value: Any): Where {
    return this.userName.GE(value)
}

infix fun String.GE(value: Any): Where {
    val s = this
    return Where("$s >= ?").addArg(value)
}


infix fun KProperty<*>.GT(value: Any): Where {
    return this.userName.GT(value)
}

infix fun String.GT(value: Any): Where {
    val s = this
    return Where("$s > ?").addArg(value)
}


infix fun KProperty<*>.LE(value: Any): Where {
    return this.userName.LE(value)
}

infix fun String.LE(value: Any): Where {
    val s = this
    return Where("$s <= ?").addArg(value)
}


infix fun KProperty<*>.LT(value: Any): Where {
    return this.userName.LT(value)
}

infix fun String.LT(value: Any): Where {
    val s = this
    return Where("$s < ?").addArg(value)
}


infix fun KProperty<*>.LIKE(value: String): Where {
    return this.userName.LIKE(value)
}

infix fun String.LIKE(value: String): Where {
    val s = this
    return Where("$s LIKE ?").addArg(value)
}

infix fun KProperty<*>.IN(values: Collection<Any>): Where {
    return this.userName.IN(values)
}

infix fun String.IN(values: Collection<Any>): Where {
    val s = this
    val a = values.joinToString(",") { "?" }
    return Where("$s IN ($a)").addArgs(values)
}

infix fun Where?.AND(other: Where?): Where? {
    if (this == null) {
        return other
    }
    if (other == null) {
        return this
    }
    if (this.value.isBlank()) {
        return other
    }
    val w = Where("($this) AND ($other)")
    w.args.addAll(this.args)
    w.args.addAll(other.args)
    return w
}

infix fun Where?.OR(other: Where?): Where? {
    if (this == null) {
        return other
    }
    if (other == null) {
        return this
    }
    if (this.value.isBlank()) {
        return other
    }
    val w = Where("($this) OR ($other)")
    w.args.addAll(this.args)
    w.args.addAll(other.args)
    return w
}