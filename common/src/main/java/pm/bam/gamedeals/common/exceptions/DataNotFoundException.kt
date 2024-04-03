package pm.bam.gamedeals.common.exceptions

class DataNotFoundException(private val dataKey: String?) : Exception() {
    override fun getLocalizedMessage(): String = msg()
    override fun toString(): String = msg()

    private fun msg() = when (dataKey) {
        null -> "Data not found"
        else -> "Data not found for key: $dataKey"
    }
}