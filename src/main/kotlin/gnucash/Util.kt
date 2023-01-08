package gnucash

import java.sql.Connection
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

val DATETIME_FORMAT: DateTimeFormatter = DateTimeFormatterBuilder()//
    .parseCaseInsensitive()//
    .append(DateTimeFormatter.ISO_LOCAL_DATE)//
    .appendLiteral(' ')//
    .append(DateTimeFormatter.ISO_LOCAL_TIME)//
    .toFormatter()

val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE

fun UUID.toGuid() = mostSignificantBits.toULong().toString(16) + leastSignificantBits.toULong().toString(16)

fun guid() = UUID.randomUUID().toGuid()

fun String.toUUID(): UUID {
    if (this.length == 32) {
        val mostSigBits = this.substring(0, 16).toULong(16)
        val leastSigBits = this.substring(16, 32).toULong(16)
        return UUID(mostSigBits.toLong(), leastSigBits.toLong())
    } else {
        throw IllegalArgumentException("Invalid guid: $this")
    }
}

@Suppress("LiftReturnOrAssignment")
fun Connection.inTransaction(block: (Connection) -> Unit) {
    autoCommit = false

    var commit = false
    try {
        block(this)
        commit = true
    } catch (throwable: Throwable) {
        commit = false
        throw throwable
    } finally {
        if (commit) {
            commit()
        } else {
            rollback()
        }
    }
}