package pm.bam.gamedeals.common.storage

import android.content.SharedPreferences
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import pm.bam.gamedeals.common.di.Settings
import pm.bam.gamedeals.common.exceptions.DataExistsException
import pm.bam.gamedeals.common.exceptions.DataNotFoundException
import pm.bam.gamedeals.common.serializer.Serializer
import javax.inject.Inject

internal class SettingStorage @Inject constructor(
    private val serializer: Serializer,
    @Settings val sharedPreferences: SharedPreferences,
) : Storage {

    override fun <T : Any> get(storageKey: String, deserializationStrategy: DeserializationStrategy<T>, defaultValue: T?): T =
        getNullable(storageKey, deserializationStrategy, defaultValue) ?: throw DataNotFoundException(storageKey)

    override fun <T : Any> getNullable(storageKey: String, deserializationStrategy: DeserializationStrategy<T>, defaultValue: T?): T? =
        sharedPreferences.getString(storageKey, null)
            ?.let { serializedData -> serializer.deserialize(serializedData, deserializationStrategy) }
            .let { data -> data ?: defaultValue }

    override fun <T : Any> save(storageKey: String, data: T, serializationStrategy: SerializationStrategy<T>, overwrite: Boolean): Boolean {
        if (!overwrite && containsKey(storageKey)) {
            throw DataExistsException(storageKey)
        }

        return sharedPreferences.edit().putString(storageKey, serializer.serialize(data, serializationStrategy)).commit()
    }

    override fun containsKey(storageKey: String): Boolean = sharedPreferences.contains(storageKey)

    override fun remove(storageKey: String): Boolean = sharedPreferences.edit().remove(storageKey).commit()
}