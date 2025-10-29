package pm.bam.gamedeals.logging

import android.util.Log

enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL,
}

fun Int.toLogLevel(): LogLevel =
    when (this) {
        Log.VERBOSE -> LogLevel.VERBOSE
        Log.DEBUG -> LogLevel.DEBUG
        Log.INFO -> LogLevel.INFO
        Log.WARN -> LogLevel.WARN
        Log.ERROR -> LogLevel.ERROR
        else -> LogLevel.FATAL
    }
