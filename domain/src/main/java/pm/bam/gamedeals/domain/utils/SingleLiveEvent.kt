package pm.bam.gamedeals.domain.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// https://itnext.io/exercises-in-futility-one-time-events-in-android-ddbdd7b5bd1c

/**
 * Used as a wrapper for data that is exposed via a StateFlow that represents an event.
 *
 * [content] should be accessed via [getContentIfNotHandled] to ensure that the event is only handled once.
 */
data class SingleLiveEvent<out T>(
    private val content: T,

    // Provides a unique id so that StateFlows can differentiate between
    // different SingleLiveEvents even if they have the same content.
    private val id: String = UUID.randomUUID().toString()
) {
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
}


/**
 * An extension function for collecting [SingleLiveEvent]s from a [StateFlow], using [LaunchedEffect] and [LocalLifecycleOwner].
 */
@SuppressLint("ComposableNaming")
@Composable
fun <T> StateFlow<SingleLiveEvent<T>?>.collectSingleEvent(
    onEventUnhandledContent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectSingleEvent.collect { singleLiveEvent ->
                singleLiveEvent?.getContentIfNotHandled()?.let { data ->
                    // handle one-time event
                    onEventUnhandledContent(data)
                }
            }
        }
    }
}