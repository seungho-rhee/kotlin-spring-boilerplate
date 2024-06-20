package boilerplate.kotlin.spring.common.util

import java.time.Instant
import java.time.LocalDateTime
import java.util.*


/**
 * Timestamp(EpochMilli) converter
 */
@JvmInline
value class Timestamp(private val timestamp: Long) {
    constructor(localDateTime: LocalDateTime) : this(localDateTime.toEpochMilli())

    fun toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId())

    val epochMilli get() = timestamp
}

fun LocalDateTime.toEpochMilli(): Long = atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli()

fun LocalDateTime.toTimestamp(): Timestamp = Timestamp(localDateTime = this)