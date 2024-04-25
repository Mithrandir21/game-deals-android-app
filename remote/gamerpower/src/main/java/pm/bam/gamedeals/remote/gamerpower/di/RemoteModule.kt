package pm.bam.gamedeals.remote.gamerpower.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.gamerpower.api.GamesApi
import pm.bam.gamedeals.remote.gamerpower.datasources.giveaway.RemoteGiveawayDataSource
import pm.bam.gamedeals.remote.gamerpower.datasources.giveaway.RemoteGiveawayDataSourceImpl
import javax.inject.Singleton

@Module(includes = [RemoteNetworkModule::class, InternalRemoteModule::class])
@InstallIn(SingletonComponent::class)
class RemoteModule


@Module
@InstallIn(SingletonComponent::class)
internal class InternalRemoteModule {

    @Provides
    @Singleton
    fun provideRemoteDealsDataSource(logger: Logger, gamesApi: GamesApi, remoteExceptionTransformer: RemoteExceptionTransformer): RemoteGiveawayDataSource =
        RemoteGiveawayDataSourceImpl(logger, gamesApi, remoteExceptionTransformer)
}
