package pm.bam.gamedeals.domain.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.domain.models.Giveaway
import pm.bam.gamedeals.domain.models.PUBLISHED_FIELD_NAME
import pm.bam.gamedeals.domain.models.USER_FIELD_NAME
import pm.bam.gamedeals.domain.models.WORTH_FIELD_NAME

@Dao
internal interface GiveawaysDao {

    /** Returns all the [Giveaway]s in the database. */
    @Query("SELECT * FROM Giveaway")
    fun observeAllGiveaways(): Flow<List<Giveaway>>

    /** Returns all the [Giveaway]s in the database. */
    @Query(
        "SELECT * FROM Giveaway " +
                "WHERE type IN (:typeValues) " +
                "ORDER BY " +
                "      CASE :sortBy WHEN '$WORTH_FIELD_NAME' THEN $WORTH_FIELD_NAME END DESC," +
                "      CASE :sortBy WHEN '$PUBLISHED_FIELD_NAME' THEN $PUBLISHED_FIELD_NAME END DESC," +
                "      CASE :sortBy WHEN '$USER_FIELD_NAME' THEN $USER_FIELD_NAME END DESC"
    )
    fun observeAllGiveawaysDescending(
        sortBy: String,
        typeValues: List<String>? = null
    ): Flow<List<Giveaway>>

    /** Returns all the [Giveaway]s in the database. */
    @Query(
        "SELECT * FROM Giveaway " +
                "WHERE type IN (:typeValues) " +
                "ORDER BY " +
                "      CASE :sortBy WHEN '$WORTH_FIELD_NAME' THEN $WORTH_FIELD_NAME END ASC," +
                "      CASE :sortBy WHEN '$PUBLISHED_FIELD_NAME' THEN $PUBLISHED_FIELD_NAME END ASC," +
                "      CASE :sortBy WHEN '$USER_FIELD_NAME' THEN $USER_FIELD_NAME END ASC"
    )
    fun observeAllGiveawaysAscending(
        sortBy: String,
        typeValues: List<String>? = null
    ): Flow<List<Giveaway>>

    /** Adds the [Giveaway] to the database. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGiveaways(vararg genericItem: Giveaway)

}