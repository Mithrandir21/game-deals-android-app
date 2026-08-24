package pm.bam.gamedeals.logging

/**
 * Whether [throwable] (or anything in its cause chain) is a *connectivity* failure rather than a defect
 * — no network, DNS miss, dropped socket, upstream timeout.
 *
 * These reach the logger through the ordinary `error(logger, t)` path, which [implementations.SentryLoggingListener]
 * captures as an issue. That turned every offline user into Sentry traffic (KOTLIN-M/K/P/G/H: all
 * `handled`, all cases where the app degraded exactly as designed and the user simply had no signal),
 * and the volume scales with the install base rather than with anything wrong in the app. Downgrading
 * them to breadcrumbs keeps the diagnostic trail on genuine crashes without the standing noise.
 *
 * This deliberately does **not** suppress the log — only its Sentry severity. Logcat/console output is
 * unchanged, and a connectivity failure still appears as a breadcrumb on any issue that follows it.
 *
 * Ktor's timeout types are matched by simple name so `:logging` needn't depend on Ktor (it sits *below*
 * `:remote` in the module graph); platform IO hierarchies are matched properly via
 * [isPlatformNetworkFailure].
 */
fun isExpectedNetworkFailure(throwable: Throwable?): Boolean {
    var cause = throwable
    var depth = 0
    while (cause != null && depth < MAX_CAUSE_DEPTH) {
        if (cause.isPlatformNetworkFailure() || cause::class.simpleName in KTOR_NETWORK_FAILURE_NAMES) return true
        val next = cause.cause
        if (next === cause) return false // self-referential chain; bail rather than spin
        cause = next
        depth++
    }
    return false
}

/**
 * Whether this throwable is a connectivity failure in the platform's own IO hierarchy — `java.io.IOException`
 * and its subclasses (`UnknownHostException`, `SocketException`, `SSLException`, …) on Android.
 */
internal expect fun Throwable.isPlatformNetworkFailure(): Boolean

/**
 * Ktor's transport timeouts, matched by simple name. Ktor throws the same class names on every engine,
 * and this avoids a `:remote`-ward dependency from the logging layer.
 */
private val KTOR_NETWORK_FAILURE_NAMES = setOf(
    "HttpRequestTimeoutException",
    "ConnectTimeoutException",
    "SocketTimeoutException",
)

/** Guards against a pathologically long (or cyclic) cause chain. */
private const val MAX_CAUSE_DEPTH = 10
