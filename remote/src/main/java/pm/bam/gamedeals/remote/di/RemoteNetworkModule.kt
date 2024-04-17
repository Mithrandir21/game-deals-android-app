package pm.bam.gamedeals.remote.di

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
import pm.bam.gamedeals.remote.api.DealsApi
import pm.bam.gamedeals.remote.api.GamesApi
import pm.bam.gamedeals.remote.api.ReleaseApi
import pm.bam.gamedeals.remote.api.StoresApi
import pm.bam.gamedeals.remote.logic.RemoteBuildType
import pm.bam.gamedeals.remote.logic.RemoteBuildUtil
import pm.bam.gamedeals.remote.logic.RemoteBuildUtilImpl
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RemoteNetworkModule {

    @Provides
    @Singleton
    internal fun provideBuildUtil(): RemoteBuildUtil = RemoteBuildUtilImpl()

    @Provides
    @Singleton
    fun provideOkHttpClient(remoteBuildUtil: RemoteBuildUtil): OkHttpClient {
        val builder = OkHttpClient.Builder()

        when (remoteBuildUtil.buildType()) {
            RemoteBuildType.DEBUG -> builder.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            RemoteBuildType.RELEASE -> Unit
        }

        return builder
//            .addInterceptor(Interceptor { chain ->
//                val request = chain.request().newBuilder().addHeader("accept", "application/json").build()
//                chain.proceed(request)
//            })
            .connectTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor { chain: Interceptor.Chain ->
//                val response = chain.proceed(chain.request())
//
//                if (!response.isSuccessful) {
//                    throw response.code.toRemoteHttpException()
//                } else {
//                    response
//                }
//            }
            .build()
    }

    @Provides
    @Singleton
    @ExperimentalSerializationApi
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.cheapshark.com")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideDealsApi(retrofit: Retrofit): DealsApi = retrofit.create(DealsApi::class.java)

    @Provides
    @Singleton
    internal fun provideGamesApi(retrofit: Retrofit): GamesApi = retrofit.create(GamesApi::class.java)

    @Provides
    @Singleton
    internal fun provideStoresApi(retrofit: Retrofit): StoresApi = retrofit.create(StoresApi::class.java)

    @Provides
    @Singleton
    internal fun provideReleaseApi(retrofit: Retrofit): ReleaseApi = retrofit.create(ReleaseApi::class.java)
}