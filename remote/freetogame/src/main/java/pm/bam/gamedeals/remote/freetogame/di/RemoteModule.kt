package pm.bam.gamedeals.remote.freetogame.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.freetogame.api.FreeGamesApi
import pm.bam.gamedeals.remote.freetogame.datasources.RemoteFreeGamesDataSource
import pm.bam.gamedeals.remote.freetogame.datasources.RemoteFreeGamesDataSourceImpl
import javax.inject.Singleton

@Module(includes = [RemoteNetworkModule::class, InternalRemoteModule::class])
@InstallIn(SingletonComponent::class)
class RemoteModule


@Module
@InstallIn(SingletonComponent::class)
internal class InternalRemoteModule {

    @Provides
    @Singleton
    fun provideRemoteFreeGamesDataSource(
        logger: Logger,
        freeGamesApi: FreeGamesApi,
        remoteExceptionTransformer: RemoteExceptionTransformer
    ): RemoteFreeGamesDataSource =
        RemoteFreeGamesDataSourceImpl(logger, freeGamesApi, remoteExceptionTransformer)
}
