package pm.bam.gamedeals.remote.exceptions

import retrofit2.HttpException
import javax.inject.Inject

/**
 * Transformation functionality allowing for transformation between specific Remote module [Throwable]s and module-external [Throwable]s.
 */
internal class RemoteExceptionTransformerImpl @Inject constructor() : RemoteExceptionTransformer {

    override fun transformApiException(throwable: Throwable): Throwable =
        when (throwable) {
            // HttpExceptions are part of Retrofit and only accessible inside this module.
            is HttpException -> throwable.toRemoteHttpException()
            else -> throwable
        }
}