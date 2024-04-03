package pm.bam.gamedeals.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest


fun <T : Any> T.toFlow(): Flow<T> = flow { emit(this@toFlow) }

/** Catches and re-throws any [Throwable] that happen upstream after performing [action] on the caught [Throwable]. */
inline fun <T> Flow<T>.onError(crossinline action: suspend FlowCollector<T>.(cause: Throwable) -> Unit): Flow<T> =
    catch {
        action(it)
        emitAll(flow { throw it })
    }

/**
 * Returns a flow that delays the given [delayMillis] **before** this flow starts to be collected.
 */
fun <T> Flow<T>.delayOnStart(delayMillis: Long): Flow<T> = onStart { delay(delayMillis) }

/**
 * Returns a flow containing the results of applying the given [transform] function to each value of the original flow
 * only after given [delayMillis] has passed, either because the transformation took more or equal amount of time as the [delayMillis],
 * or because the suspend function was delayed for the remaining time.
 */
fun <T, R> Flow<T>.mapDelayAtLeast(delayMillis: Long, transformFunction: suspend (value: T) -> R): Flow<R> =
    transform { value ->
        val timeBeforeTransformation = System.currentTimeMillis()
        val transformedTransformation = transformFunction(value)
        val timeAfterTransformation = System.currentTimeMillis()

        val transformationDuration = timeAfterTransformation - timeBeforeTransformation

        val transformationShorterThanDelay = delayMillis > transformationDuration

        if (transformationShorterThanDelay) {
            val remainingDelay = delayMillis - transformationDuration
            delay(remainingDelay)
        }

        return@transform emit(transformedTransformation)
    }


/**
 * Returns a flow containing the results of applying the given [transformLatest] function to each value of the original flow
 * only after given [delayMillis] has passed, either because the transformation took more or equal amount of time as the [delayMillis],
 * or because the suspend function was delayed for the remaining time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
inline fun <T, R> Flow<T>.flatMapLatestDelayAtLeast(delayMillis: Long, crossinline transformFunction: suspend (value: T) -> R): Flow<R> =
    transformLatest { value ->

        val timeBeforeTransformation = System.currentTimeMillis()
        val transformedTransformation = transformFunction(value)
        val timeAfterTransformation = System.currentTimeMillis()

        val transformationDuration = timeAfterTransformation - timeBeforeTransformation

        val transformationShorterThanDelay = delayMillis > transformationDuration

        if (transformationShorterThanDelay) {
            val remainingDelay = delayMillis - transformationDuration
            delay(remainingDelay)
        }

        return@transformLatest emit(transformedTransformation)
    }


/**
 * Returns a flow containing the results of applying the given [transformLatest] function to each value of the original flow
 * only after given [delayMillis] has passed, either because the transformation took more or equal amount of time as the [delayMillis],
 * or because the suspend function was delayed for the remaining time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.latestDelayAtLeast(delayMillis: Long): Flow<T> =
    transformLatest { value ->

        val timeBeforeTransformation = System.currentTimeMillis()
        val timeAfterTransformation = System.currentTimeMillis()

        val transformationDuration = timeAfterTransformation - timeBeforeTransformation

        val transformationShorterThanDelay = delayMillis > transformationDuration

        if (transformationShorterThanDelay) {
            val remainingDelay = delayMillis - transformationDuration
            delay(remainingDelay)
        }

        return@transformLatest emit(value)
    }