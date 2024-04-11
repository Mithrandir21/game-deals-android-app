package pm.bam.gamedeals.common.datetime.parsing

import java.time.Instant

fun interface DatetimeParsing {
    fun parseLocalDateTime(seconds: Long): Instant
}