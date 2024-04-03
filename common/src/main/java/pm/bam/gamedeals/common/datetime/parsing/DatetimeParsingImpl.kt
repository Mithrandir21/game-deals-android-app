package pm.bam.gamedeals.common.datetime.parsing

import java.time.Instant
import javax.inject.Inject

internal class DatetimeParsingImpl @Inject constructor() : DatetimeParsing {

    override fun parseLocalDateTime(seconds: Long): Instant = Instant.ofEpochSecond(seconds)

}