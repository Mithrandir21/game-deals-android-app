package pm.bam.gamedeals.common.datetime.parsing

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class DatetimeParsingImpl @Inject constructor() : DatetimeParsing {

    private var datetimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private var dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun parseLocalDateTime(seconds: Long): Instant = Instant.ofEpochSecond(seconds)

    override fun parseDatetime(value: String): LocalDateTime = LocalDateTime.parse(value, datetimeFormatter)

    override fun parseDate(value: String): LocalDateTime = LocalDateTime.parse(value, dateFormatter)

    override fun datetimeToString(localDateTime: LocalDateTime): String = localDateTime.format(datetimeFormatter)

}