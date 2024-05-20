package pm.bam.gamedeals.remote.freetogame.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pm.bam.gamedeals.remote.freetogame.api.FreeGamesApi
import pm.bam.gamedeals.remote.logic.RemoteBuildType
import pm.bam.gamedeals.remote.logic.RemoteBuildUtil
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RemoteNetworkModule {

    @Provides
    @Singleton
    @FreeToGame
    fun provideOkHttpClient(remoteBuildUtil: RemoteBuildUtil): OkHttpClient {
        val builder = OkHttpClient.Builder()

        when (remoteBuildUtil.buildType()) {
            RemoteBuildType.DEBUG -> builder.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            RemoteBuildType.RELEASE -> Unit
        }

        return builder
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @FreeToGame
    @ExperimentalSerializationApi
    fun provideRetrofit(@FreeToGame okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.freetogame.com")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideFreeGamesApi(@FreeToGame retrofit: Retrofit): FreeGamesApi = retrofit.create(FreeGamesApi::class.java)
}

/** A [Qualifier] used specifically for dependencies associated specifically with this FreeToGame module as opposed to any other Module. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class FreeToGame