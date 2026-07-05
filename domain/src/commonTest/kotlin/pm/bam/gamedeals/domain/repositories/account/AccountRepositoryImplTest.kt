package pm.bam.gamedeals.domain.repositories.account

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pm.bam.gamedeals.domain.auth.AuthTokenStore
import pm.bam.gamedeals.domain.models.AuthState
import pm.bam.gamedeals.domain.models.ItadUser
import pm.bam.gamedeals.domain.source.ItadLoginSource
import pm.bam.gamedeals.logging.analytics.Analytics
import pm.bam.gamedeals.logging.analytics.AnalyticsEvents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The ITAD account session: the login outcome matrix, logout, and auth-state passthrough. */
class AccountRepositoryImplTest {

    private val tokenStore = FakeAuthTokenStore()
    private val loginSource = FakeItadLoginSource()
    private val analytics = RecordingAnalytics()

    private val repo = AccountRepositoryImpl(tokenStore, loginSource, analytics)

    @Test
    fun login_returns_the_user_and_reports_success() = runTest {
        loginSource.result = ItadUser("bam")

        assertEquals(ItadUser("bam"), repo.login())
        assertEquals(listOf(login("success")), analytics.captured)
    }

    @Test
    fun a_cancelled_browser_returns_null_and_reports_cancelled() = runTest {
        // The OAuth source returns null (not an exception) when the user backs out of the browser.
        loginSource.result = null

        assertNull(repo.login())
        assertEquals(listOf(login("cancelled")), analytics.captured)
    }

    @Test
    fun a_failing_login_reports_error_and_rethrows() = runTest {
        loginSource.error = RuntimeException("token exchange failed")

        assertFailsWith<RuntimeException> { repo.login() }
        assertEquals(listOf(login("error")), analytics.captured)
    }

    @Test
    fun a_cancelled_coroutine_propagates_without_reporting() = runTest {
        // CancellationException is cooperative cancellation, not a login failure — it must propagate
        // untouched and must NOT emit an analytics event (the dedicated catch rethrows before capture).
        loginSource.error = CancellationException("scope cancelled")

        assertFailsWith<CancellationException> { repo.login() }
        assertTrue(analytics.captured.isEmpty())
    }

    @Test
    fun logout_clears_the_token_and_reports_logout() = runTest {
        repo.logout()

        assertEquals(1, tokenStore.clearCount)
        assertEquals(listOf(AnalyticsEvents.ACCOUNT_LOGOUT to emptyMap<String, Any>()), analytics.captured)
    }

    @Test
    fun observeAuthState_forwards_the_token_store_state() = runTest {
        tokenStore.state = flowOf(AuthState.LoggedIn("bam"))

        assertEquals(AuthState.LoggedIn("bam"), repo.observeAuthState().first())
    }

    private fun login(result: String): Pair<String, Map<String, Any>> =
        AnalyticsEvents.ACCOUNT_LOGIN to mapOf("result" to result)
}

private class FakeItadLoginSource : ItadLoginSource {
    var result: ItadUser? = null
    var error: Throwable? = null
    override suspend fun login(): ItadUser? {
        error?.let { throw it }
        return result
    }
}

private class FakeAuthTokenStore(
    var state: Flow<AuthState> = flowOf(AuthState.LoggedOut),
) : AuthTokenStore {
    var clearCount = 0
        private set

    override fun observeAuthState(): Flow<AuthState> = state
    override suspend fun clear() { clearCount++ }

    override suspend fun getAccessToken(): String? = error("unused")
    override suspend fun getRefreshToken(): String? = error("unused")
    override suspend fun getUsername(): String? = error("unused")
    override suspend fun getExpiresAtEpochMs(): Long = error("unused")
    override suspend fun getScopeVersion(): Int = error("unused")
    override suspend fun saveTokens(accessToken: String, refreshToken: String, expiresAtEpochMs: Long, username: String, scopeVersion: Int) = error("unused")
}

private class RecordingAnalytics : Analytics {
    val captured = mutableListOf<Pair<String, Map<String, Any>>>()
    override fun screen(name: String, properties: Map<String, Any>) = Unit
    override fun capture(event: String, properties: Map<String, Any>) { captured += event to properties }
    override fun identify(distinctId: String) = Unit
    override fun reset() = Unit
    override fun setConsent(granted: Boolean) = Unit
}
