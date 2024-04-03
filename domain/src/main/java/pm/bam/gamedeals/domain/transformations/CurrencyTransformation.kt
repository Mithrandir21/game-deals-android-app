package pm.bam.gamedeals.domain.transformations

interface CurrencyTransformation {

    fun valueToDenominated(value: Double): String

}