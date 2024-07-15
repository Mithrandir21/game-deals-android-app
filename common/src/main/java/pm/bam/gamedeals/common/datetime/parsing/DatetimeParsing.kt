package pm.bam.gamedeals.common.datetime.parsing

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

interface DatetimeParsing {

    fun parseLocalDateTime(seconds: Long): Instant

    fun parseDatetime(value: String): LocalDateTime

    fun parseDate(value: String): LocalDate

    fun datetimeToString(localDateTime: LocalDateTime): String

}