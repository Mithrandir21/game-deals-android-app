package pm.bam.gamedeals.domain.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pm.bam.gamedeals.common.datetime.formatting.DateTimeFormatter
import pm.bam.gamedeals.common.datetime.parsing.DatetimeParsing
import pm.bam.gamedeals.common.serializer.Serializer
import pm.bam.gamedeals.domain.db.DomainDatabase
import pm.bam.gamedeals.domain.db.dao.DealsDao
import pm.bam.gamedeals.domain.db.dao.GamesDao
import pm.bam.gamedeals.domain.db.dao.GiveawaysDao
import pm.bam.gamedeals.domain.db.dao.ReleasesDao
import pm.bam.gamedeals.domain.db.dao.StoresDao
import pm.bam.gamedeals.domain.repositories.deals.DealsRepository
import pm.bam.gamedeals.domain.repositories.deals.DealsRepositoryImpl
import pm.bam.gamedeals.domain.repositories.games.GamesRepository
import pm.bam.gamedeals.domain.repositories.games.GamesRepositoryImpl
import pm.bam.gamedeals.domain.repositories.giveaway.GiveawayRepository
import pm.bam.gamedeals.domain.repositories.giveaway.GiveawayRepositoryImpl
import pm.bam.gamedeals.domain.repositories.releases.ReleasesRepository
import pm.bam.gamedeals.domain.repositories.releases.ReleasesRepositoryImpl
import pm.bam.gamedeals.domain.repositories.stores.StoresRepository
import pm.bam.gamedeals.domain.repositories.stores.StoresRepositoryImpl
import pm.bam.gamedeals.domain.transformations.CurrencyTransformation
import pm.bam.gamedeals.domain.transformations.CurrencyTransformationImpl
import pm.bam.gamedeals.domain.utils.GiveawayPlatformsConverter
import pm.bam.gamedeals.domain.utils.LocalDatetimeConverter
import pm.bam.gamedeals.domain.utils.StoreImagesConverter
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.cheapshark.datasources.deals.RemoteDealsDataSource
import pm.bam.gamedeals.remote.cheapshark.datasources.games.RemoteGamesDataSource
import pm.bam.gamedeals.remote.cheapshark.datasources.releases.RemoteReleasesDataSource
import pm.bam.gamedeals.remote.cheapshark.datasources.stores.RemoteStoresDataSource
import pm.bam.gamedeals.remote.gamerpower.datasources.giveaway.RemoteGiveawayDataSource
import javax.inject.Singleton

@Module(includes = [InternalDomainModule::class])
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    @Domain
    fun provideDomainSharedPreference(@ApplicationContext appContext: Context): SharedPreferences =
        appContext.getSharedPreferences("gamedeals_domain_storage", Context.MODE_PRIVATE)
}

@Module
@InstallIn(SingletonComponent::class)
internal class InternalDomainModule {

    @Provides
    @Singleton
    @CurrencyDenomination
    fun provideCurrencyDenomination(): String = "$"

    @Provides
    @Singleton
    fun provideCurrencyTransformation(@CurrencyDenomination currencySymbol: String): CurrencyTransformation = CurrencyTransformationImpl(currencySymbol)

    @Provides
    @Domain
    fun provideStoreImagesConverter(serializer: Serializer) = StoreImagesConverter(serializer)

    @Provides
    @Domain
    fun provideGiveawayPlatformsConverter() = GiveawayPlatformsConverter()

    @Provides
    @Domain
    fun provideLocalDatetimeConverter() = LocalDatetimeConverter()

//    @Provides
//    fun provideLocalDateSerializer(datetimeParsing: DatetimeParsing) = LocalDateSerializer(datetimeParsing)

    @Provides
    @Singleton
    fun provideDealsRepository(logger: Logger, dealsDao: DealsDao, db: DomainDatabase, remoteDealsDataSource: RemoteDealsDataSource, currencyTransformation: CurrencyTransformation, dateTimeFormatter: DateTimeFormatter): DealsRepository =
        DealsRepositoryImpl(logger, dealsDao, db, remoteDealsDataSource, currencyTransformation, dateTimeFormatter)

    @Provides
    @Singleton
    fun provideGamesRepository(gamesDao: GamesDao, remoteGamesDataSource: RemoteGamesDataSource, remoteDealsDataSource: RemoteDealsDataSource, currencyTransformation: CurrencyTransformation, dateTimeFormatter: DateTimeFormatter): GamesRepository =
        GamesRepositoryImpl(gamesDao, remoteGamesDataSource, remoteDealsDataSource, currencyTransformation, dateTimeFormatter)

    @Provides
    @Singleton
    fun provideStoresRepository(logger: Logger, storesDao: StoresDao, remoteStoresDataSource: RemoteStoresDataSource): StoresRepository =
        StoresRepositoryImpl(logger, storesDao, remoteStoresDataSource)

    @Provides
    @Singleton
    fun provideReleasesRepository(logger: Logger, releasesDao: ReleasesDao, remoteReleasesDataSource: RemoteReleasesDataSource): ReleasesRepository =
        ReleasesRepositoryImpl(logger, releasesDao, remoteReleasesDataSource)

    @Provides
    @Singleton
    fun provideGiveawayRepository(logger: Logger, giveawaysDao: GiveawaysDao, remoteGiveawayDataSource: RemoteGiveawayDataSource, datetimeParsing: DatetimeParsing): GiveawayRepository =
        GiveawayRepositoryImpl(logger, giveawaysDao, remoteGiveawayDataSource, datetimeParsing)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        @Domain storeImagesConverter: StoreImagesConverter,
        @Domain giveawayPlatformsConverter: GiveawayPlatformsConverter,
        @Domain localDatetimeConverter: LocalDatetimeConverter
    ): DomainDatabase =
        Room.databaseBuilder(context, DomainDatabase::class.java, "${DomainDatabase::class.java.simpleName}.db")
            .fallbackToDestructiveMigration()
            .addTypeConverter(storeImagesConverter)
            .addTypeConverter(giveawayPlatformsConverter)
            .addTypeConverter(localDatetimeConverter)
            .build()

    @Provides
    @Singleton
    fun provideDealsDao(db: DomainDatabase): DealsDao = db.getDealsDao()

    @Provides
    @Singleton
    fun provideGamesDao(db: DomainDatabase): GamesDao = db.getGamesDao()

    @Provides
    @Singleton
    fun provideStoresDao(db: DomainDatabase): StoresDao = db.getStoresDao()

    @Provides
    @Singleton
    fun provideReleasesDao(db: DomainDatabase): ReleasesDao = db.getReleasesDao()

    @Provides
    @Singleton
    fun provideGiveawaysDao(db: DomainDatabase): GiveawaysDao = db.getGiveawaysDao()
}

