package pm.bam.gamedeals.domain.transformations

fun interface CurrencyTransformation {
    fun valueToDenominated(value: Double): String
}