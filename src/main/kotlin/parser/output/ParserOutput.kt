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
    val amount: BigDecimal = BigDecimal.ZERO,
    val accountType: AccountType
)

enum class AccountType {
    CHECKING_SAVING, STOCK, CREDIT_CARD
}