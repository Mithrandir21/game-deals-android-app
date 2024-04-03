package pm.bam.gamedeals.logging

import android.util.Log
import java.util.logging.Logger

/** Logging tag. */
internal val Any.tag: String get() = javaClass.simpleName

/** Logs a verbose entry using default [Logger] implementation. */
internal fun Any.verbose(tag: String? = null, throwable: Throwable? = null, messageProvider: () -> String) {
    Log.v(tag ?: this.tag, messageProvider(), throwable)
}

/** Logs a debug entry using default [Logger] implementation. */
internal fun Any.debug(tag: String? = null, throwable: Throwable? = null, messageProvider: () -> String) {
    Log.d(tag ?: this.tag, messageProvider(), throwable)
}

/** Logs an info entry using default [Logger] implementation. */
internal fun Any.info(tag: String? = null, throwable: Throwable? = null, messageProvider: () -> String) {
    Log.i(tag ?: this.tag, messageProvider(), throwable)
}

/** Logs a warning entry using default [Logger] implementation. */
internal fun Any.warn(tag: String? = null, throwable: Throwable? = null, messageProvider: () -> String) {
    Log.w(tag ?: this.tag, messageProvider(), throwable)
}

/** Logs an error entry using default [Logger] implementation. */
internal fun Any.error(tag: String? = null, throwable: Throwable? = null, messageProvider: () -> String) {
    Log.e(tag ?: this.tag, messageProvider(), throwable)
}

/** Logs a fatal error entry using default [Logger] implementation. */
internal fun Any.fatal(tag: String? = null, throwable: Throwable? = null, messageProvider: () -> String) {
    Log.wtf(tag ?: this.tag, messageProvider(), throwable)
}

enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL,
}

fun Int.toLogLevel() : LogLevel =
    when(this) {
        Log.VERBOSE -> LogLevel.VERBOSE
        Log.DEBUG -> LogLevel.DEBUG
        Log.INFO -> LogLevel.INFO
        Log.WARN -> LogLevel.WARN
        Log.ERROR -> LogLevel.ERROR
        else -> LogLevel.FATAL
    }
