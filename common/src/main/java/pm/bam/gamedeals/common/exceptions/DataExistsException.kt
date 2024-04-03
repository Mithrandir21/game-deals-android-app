package pm.bam.gamedeals.common.exceptions

class DataExistsException(private val dataKey: String?) : Exception() {
    override fun getLocalizedMessage(): String = msg()
    override fun toString(): String = msg()

    private fun msg() = when (dataKey) {
        null -> "Data already exists"
        else -> "Data already exists for key: $dataKey"
    }
}