package parser.output

import gnucash.entity.Account
import java.math.BigDecimal
import java.time.LocalDate

sealed interface ParserOutput : AutoCloseable {
    fun write(outputTransaction: OutputTransaction)
}

data class OutputTransaction(
    val fromAccount: Account,
    val toAccount: Account,
    val date: LocalDate = LocalDate.now(),
    val description: String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
)