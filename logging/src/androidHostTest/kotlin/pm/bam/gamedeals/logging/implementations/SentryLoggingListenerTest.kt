package pm.bam.gamedeals.logging.implementations

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import io.sentry.kotlin.multiplatform.Scope
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pm.bam.gamedeals.logging.LogLevel

/**
 * JVM-host coverage for how log levels map onto Sentry: low levels become breadcrumbs, ERROR/FATAL become
 * captured events (exception when a throwable is present, otherwise a message). The global `Sentry` object
 * is intercepted with mockk (mirrors AndroidNotificationSchedulerTest in :domain).
 */
class SentryLoggingListenerTest {

    private val listener = SentryLoggingListener()

    @Before
    fun setUp() {
        mockkObject(Sentry)
        every { Sentry.addBreadcrumb(any()) } returns Unit
        every { Sentry.captureException(any(), any()) } returns mockk()
        every { Sentry.captureMessage(any(), any()) } returns mockk()
        every { Sentry.isEnabled() } returns true
    }

    @After
    fun tearDown() = unmockkObject(Sentry)

    @Test
    fun warn_becomes_a_warning_breadcrumb_carrying_message_and_tag() {
        val crumb = slot<Breadcrumb>()

        listener.onLog(LogLevel.WARN, "low disk", tag = "Storage", throwable = null)

        verify(exactly = 1) { Sentry.addBreadcrumb(capture(crumb)) }
        assertEquals(SentryLevel.WARNING, crumb.captured.level)
        assertEquals("low disk", crumb.captured.message)
        assertEquals("Storage", crumb.captured.category)
        verify(exactly = 0) { Sentry.captureException(any(), any()) }
        verify(exactly = 0) { Sentry.captureMessage(any(), any()) }
    }

    @Test
    fun info_becomes_an_info_breadcrumb() {
        val crumb = slot<Breadcrumb>()

        listener.onLog(LogLevel.INFO, "started", tag = null, throwable = null)

        verify { Sentry.addBreadcrumb(capture(crumb)) }
        assertEquals(SentryLevel.INFO, crumb.captured.level)
    }

    @Test
    fun verbose_and_debug_both_map_to_debug_breadcrumbs() {
        val crumbs = mutableListOf<Breadcrumb>()

        listener.onLog(LogLevel.VERBOSE, "v", tag = null, throwable = null)
        listener.onLog(LogLevel.DEBUG, "d", tag = null, throwable = null)

        verify(exactly = 2) { Sentry.addBreadcrumb(capture(crumbs)) }
        assertEquals(listOf(SentryLevel.DEBUG, SentryLevel.DEBUG), crumbs.map { it.level })
    }

    @Test
    fun error_with_a_throwable_captures_the_exception_at_error_level() {
        val boom = IllegalStateException("boom")
        val scopeBlock = slot<(Scope) -> Unit>()

        listener.onLog(LogLevel.ERROR, "ignored-message", tag = null, throwable = boom)

        verify(exactly = 1) { Sentry.captureException(boom, capture(scopeBlock)) }
        verify(exactly = 0) { Sentry.captureMessage(any(), any()) }
        // The scope callback stamps the level — apply it to a scope and confirm.
        val scope = mockk<Scope>(relaxed = true)
        scopeBlock.captured(scope)
        verify { scope.level = SentryLevel.ERROR }
    }

    @Test
    fun error_without_a_throwable_captures_the_message_at_error_level() {
        val scopeBlock = slot<(Scope) -> Unit>()

        listener.onLog(LogLevel.ERROR, "no throwable", tag = null, throwable = null)

        verify(exactly = 1) { Sentry.captureMessage("no throwable", capture(scopeBlock)) }
        verify(exactly = 0) { Sentry.captureException(any(), any()) }
        val scope = mockk<Scope>(relaxed = true)
        scopeBlock.captured(scope)
        verify { scope.level = SentryLevel.ERROR }
    }

    @Test
    fun fatal_with_a_throwable_captures_the_exception_at_fatal_level() {
        val boom = IllegalStateException("fatal boom")
        val scopeBlock = slot<(Scope) -> Unit>()

        listener.onLog(LogLevel.FATAL, "ignored", tag = null, throwable = boom)

        verify(exactly = 1) { Sentry.captureException(boom, capture(scopeBlock)) }
        verify(exactly = 0) { Sentry.captureMessage(any(), any()) }
        val scope = mockk<Scope>(relaxed = true)
        scopeBlock.captured(scope)
        verify { scope.level = SentryLevel.FATAL }
    }

    @Test
    fun fatal_without_a_throwable_captures_the_message_at_fatal_level() {
        val scopeBlock = slot<(Scope) -> Unit>()

        listener.onLog(LogLevel.FATAL, "fatal note", tag = null, throwable = null)

        verify(exactly = 1) { Sentry.captureMessage("fatal note", capture(scopeBlock)) }
        val scope = mockk<Scope>(relaxed = true)
        scopeBlock.captured(scope)
        verify { scope.level = SentryLevel.FATAL }
    }

    @Test
    fun onFatalThrowable_captures_the_exception_at_fatal_level() {
        val crash = RuntimeException("crash")
        val scopeBlock = slot<(Scope) -> Unit>()

        listener.onFatalThrowable(tag = "Boot", throwable = crash)

        verify(exactly = 1) { Sentry.captureException(crash, capture(scopeBlock)) }
        val scope = mockk<Scope>(relaxed = true)
        scopeBlock.captured(scope)
        verify { scope.level = SentryLevel.FATAL }
    }

    @Test
    fun isEnabled_delegates_to_the_sdk() {
        every { Sentry.isEnabled() } returns false
        assertTrue(!listener.isEnabled())

        every { Sentry.isEnabled() } returns true
        assertTrue(listener.isEnabled())
    }
}
