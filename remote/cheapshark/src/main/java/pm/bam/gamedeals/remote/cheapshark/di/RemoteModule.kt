package pm.bam.gamedeals.remote.cheapshark.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.cheapshark.api.DealsApi
import pm.bam.gamedeals.remote.cheapshark.api.GamesApi
import pm.bam.gamedeals.remote.cheapshark.api.ReleaseApi
import pm.bam.gamedeals.remote.cheapshark.api.StoresApi
import pm.bam.gamedeals.remote.cheapshark.datasources.deals.RemoteDealsDataSource
import pm.bam.gamedeals.remote.cheapshark.datasources.deals.RemoteDealsDataSourceImpl
import pm.bam.gamedeals.remote.cheapshark.datasources.games.RemoteGamesDataSource
import pm.bam.gamedeals.remote.cheapshark.datasources.games.RemoteGamesDataSourceImpl
import pm.bam.gamedeals.remote.cheapshark.datasources.releases.RemoteReleasesDataSource
import pm.bam.gamedeals.remote.cheapshark.datasources.releases.RemoteReleasesDataSourceImpl
import pm.bam.gamedeals.remote.cheapshark.datasources.stores.RemoteStoresDataSource
import pm.bam.gamedeals.remote.cheapshark.datasources.stores.RemoteStoresDataSourceImpl
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import javax.inject.Singleton

@Module(includes = [RemoteNetworkModule::class, InternalRemoteModule::class])
@InstallIn(SingletonComponent::class)
class RemoteModule


@Module
@InstallIn(SingletonComponent::class)
internal class InternalRemoteModule {

    @Provides
    @Singleton
    fun provideRemoteDealsDataSource(logger: Logger, dealsApi: DealsApi, remoteExceptionTransformer: RemoteExceptionTransformer): RemoteDealsDataSource =
        RemoteDealsDataSourceImpl(logger, dealsApi, remoteExceptionTransformer)

    @Provides
    @Singleton
    fun provideRemoteGamesDataSource(logger: Logger, gamesApi: GamesApi, remoteExceptionTransformer: RemoteExceptionTransformer): RemoteGamesDataSource =
        RemoteGamesDataSourceImpl(logger, gamesApi, remoteExceptionTransformer)

    @Provides
    @Singleton
    fun provideRemoteStoresDataSource(logger: Logger, storesApi: StoresApi, remoteExceptionTransformer: RemoteExceptionTransformer): RemoteStoresDataSource =
        RemoteStoresDataSourceImpl(logger, storesApi, remoteExceptionTransformer)

    @Provides
    @Singleton
    fun provideRemoteReleasesDataSource(
        logger: Logger,
        releaseApi: ReleaseApi,
        remoteExceptionTransformer: RemoteExceptionTransformer
    ): RemoteReleasesDataSource =
        RemoteReleasesDataSourceImpl(logger, releaseApi, remoteExceptionTransformer)
}
