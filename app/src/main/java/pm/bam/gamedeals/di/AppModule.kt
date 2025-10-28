package pm.bam.gamedeals.di

import android.content.Context
import android.util.Log
import coil.ImageLoader
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.logging.toLogLevel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebase(): FirebaseAnalytics = Firebase.analytics

    @Coil
    @Provides
    @Singleton
    fun provideCoilInternalLogger(logger: Logger): coil.util.Logger =
        object : coil.util.Logger {
            override var level: Int = Log.DEBUG
            override fun log(tag: String, priority: Int, message: String?, throwable: Throwable?) {
                logger.log(priority.toLogLevel(), "CoilLogging", throwable = throwable) { message ?: "Coil Log Message" }
            }
        }


    @Provides
    @Singleton
    fun provideCoilImageLoader(
        @ApplicationContext appContext: Context,
        @Coil coilLogger: coil.util.Logger
    ): ImageLoader = ImageLoader.Builder(appContext)
        .crossfade(true)
        .dispatcher(Dispatchers.Default)
        .logger(coilLogger)
        .build()

}