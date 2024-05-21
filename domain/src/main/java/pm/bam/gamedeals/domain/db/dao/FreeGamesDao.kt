package pm.bam.gamedeals.domain.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.domain.models.FreeGame

@Dao
internal interface FreeGamesDao {

    /** Returns all the [FreeGame]s in the database. */
    @Query("SELECT * FROM FreeGame")
    fun observeAllFreeGames(): Flow<List<FreeGame>>

    /** Adds the [FreeGame] to the database. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFreeGames(vararg genericItem: FreeGame)

}