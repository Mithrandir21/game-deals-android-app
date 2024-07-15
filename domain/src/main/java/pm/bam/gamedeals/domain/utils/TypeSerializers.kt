package pm.bam.gamedeals.domain.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
internal class LocalDateTimeSerializer @Inject constructor() : KSerializer<LocalDateTime> {

    override fun serialize(encoder: Encoder, value: LocalDateTime) = encoder.encodeLong(value.toEpochSecond(ZoneOffset.UTC))

    override fun deserialize(decoder: Decoder): LocalDateTime = LocalDateTime.ofEpochSecond(decoder.decodeLong(), 0, ZoneOffset.UTC)
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
internal class LocalDateSerializer @Inject constructor() : KSerializer<LocalDate> {

    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeLong(value.toEpochDay())

    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.ofEpochDay(decoder.decodeLong())
}