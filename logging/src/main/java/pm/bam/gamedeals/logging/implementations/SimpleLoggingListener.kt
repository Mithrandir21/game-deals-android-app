package pm.bam.gamedeals.logging.implementations

import pm.bam.gamedeals.logging.LogLevel
import pm.bam.gamedeals.logging.LoggingInterface
import pm.bam.gamedeals.logging.debug
import pm.bam.gamedeals.logging.error
import pm.bam.gamedeals.logging.fatal
import pm.bam.gamedeals.logging.info
import pm.bam.gamedeals.logging.verbose
import pm.bam.gamedeals.logging.warn
import javax.inject.Inject

internal class SimpleLoggingListener @Inject constructor() : LoggingInterface {

    /** Returns a boolean indicating whether this [LoggingInterface] is enabled or not. */
    override fun isEnabled(): Boolean = true

    /** Tag for the interface used internally for reporting or identifying. */
    override fun getLoggerTag(): String = SimpleLoggingListener::class.java.simpleName

    /**
     * Method called when some information needs logging.
     *
     * @param level The level of logging that applies to the provided message and possible [Throwable].
     * @param message The logged message.
     * @param tag The tag to use in logging.
     * @param throwable A specific [Throwable] associated with this log event. This could be a fatal exception to some journey, or simply informative.
     */
    override fun onLog(level: LogLevel, message: String, tag: String?, throwable: Throwable?) =
        when (level) {
            LogLevel.VERBOSE -> verbose(tag, throwable) { message }
            LogLevel.DEBUG -> debug(tag, throwable) { message }
            LogLevel.INFO -> info(tag, throwable) { message }
            LogLevel.WARN -> warn(tag, throwable) { message }
            LogLevel.ERROR -> error(tag, throwable) { message }
            LogLevel.FATAL -> fatal(tag, throwable) { message }
        }

    /**
     * A fatal [Throwable] has been thrown by the App. This happens when something catastrophic happens, something that cannot be recovered from.
     *
     * @param tag The tag to use in logging.
     * @param throwable The [Throwable] that has been thrown by the fatal event.
     */
    override fun onFatalThrowable(tag: String?, throwable: Throwable) = fatal(tag, throwable) { "Fatal crash" }
}