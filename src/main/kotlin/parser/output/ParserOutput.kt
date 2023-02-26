package parser.output

import java.math.BigDecimal
import java.time.LocalDate

sealed interface ParserOutput : AutoCloseable {
    fun write(outputTransaction: OutputTransaction)
}

data class OutputTransaction(
    val debitAccount: String? = null,
    val creditAccount: String? = null,
    val postDate: LocalDate = LocalDate.now(),
    val description: String = "",
    val reference: String? = null,
    val amount: BigDecimal = BigDecimal.ZERO,
    val price: BigDecimal? = null,
    val quantity: BigDecimal? = null,
    val symbol: String? = null
) {
    init {
        if (creditAccount == null && debitAccount == null) {
            throw IllegalArgumentException("No account was associated with this transaction: $this")
        }

        debitAccount?.let {
            if (it.length < 4) {
                throw IllegalArgumentException("Account length should be at least 4")
            }
        }

        creditAccount?.let {
            if (it.length < 4) {
                throw IllegalArgumentException("Account length should be at least 4")
            }
        }
    }
}