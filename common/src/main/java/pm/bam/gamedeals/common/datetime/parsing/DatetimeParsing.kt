package pm.bam.gamedeals.common.datetime.parsing

import java.time.Instant


interface DatetimeParsing {

    fun parseLocalDateTime(seconds: Long): Instant

}