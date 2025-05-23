package pm.bam.gamedeals.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pm.bam.gamedeals.domain.db.dao.DealsDao
import pm.bam.gamedeals.domain.db.dao.GamesDao
import pm.bam.gamedeals.domain.db.dao.GiveawaysDao
import pm.bam.gamedeals.domain.db.dao.PagingDao
import pm.bam.gamedeals.domain.db.dao.ReleasesDao
import pm.bam.gamedeals.domain.db.dao.StoresDao
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.DealPage
import pm.bam.gamedeals.domain.models.Game
import pm.bam.gamedeals.domain.models.Giveaway
import pm.bam.gamedeals.domain.models.Release
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.domain.utils.GiveawayPlatformsConverter
import pm.bam.gamedeals.domain.utils.LocalDatetimeConverter
import pm.bam.gamedeals.domain.utils.StoreImagesConverter

@Database(version = 3, entities = [Deal::class, DealPage::class, Game::class, Store::class, Release::class, Giveaway::class], exportSchema = false)
@TypeConverters(StoreImagesConverter::class, GiveawayPlatformsConverter::class, LocalDatetimeConverter::class)
internal abstract class DomainDatabase : RoomDatabase() {

    abstract fun getDealsDao(): DealsDao

    abstract fun getGamesDao(): GamesDao

    abstract fun getStoresDao(): StoresDao

    abstract fun getPagingDao(): PagingDao

    abstract fun getReleasesDao(): ReleasesDao

    abstract fun getGiveawaysDao(): GiveawaysDao

}