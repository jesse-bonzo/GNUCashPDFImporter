package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

object VanguardStatementParser : StatementParser {
    private val transactionPattern = Regex(
        """^(\d\d/\d\d) (\d\d/\d\d) (-|[A-Z]{2,4}) (.*) (-|Buy|Sell|Dividend) (-|Cash) (-|-?\d+\.\d+) (-|\d+\.\d+) (-|\d+\.\d+) (-?[\d,]+\.\d\d)$""",
        RegexOption.MULTILINE
    )
    private val accountNumberPattern = Regex("""Account—(\d+)""")
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd")
    private val statementDatePattern = Regex("""^(\w+ \d+, \d\d\d\d), .* statement""")
    private val statementDateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val pdfText = input.getText()
        val accountNumber = accountNumberPattern.find(pdfText)?.groupValues?.get(1)
            ?: throw VanguardParsingException("Unable to find account number")

        val statementDate = statementDatePattern.find(pdfText)?.let { matchResult ->
            val (statementDate) = matchResult.destructured
            LocalDate.parse(statementDate, statementDateFormat)
        } ?: LocalDate.now()


        transactionPattern.findAll(pdfText).forEach { matchResult ->
            var (transactionDate, postDate, symbol, description, transactionType, accountType, quantity, price, fees, amount) = matchResult.destructured

            fun String.removeDashes() = this.replace("-", "").trim()

            symbol = symbol.removeDashes()
            description = description.removeDashes()
            amount = amount.replace(",", "").trim()
            transactionType = transactionType.removeDashes()
            accountType = accountType.removeDashes()
            quantity = quantity.removeDashes()
            price = price.removeDashes()
            fees = fees.removeDashes()

            val total = if (fees.isBlank()) {
                BigDecimal(amount)
            } else {
                BigDecimal(amount).add(BigDecimal(fees))
            }

            output.write(
                OutputTransaction(
                    debitAccount = if (transactionType == "Sell" || transactionType == "Dividend") accountNumber else null,
                    creditAccount = if (transactionType == "Buy") accountNumber else null,
                    postDate = dateFormat.parse(postDate).let { parsed ->
                        LocalDate.of(
                            statementDate.year, parsed[ChronoField.MONTH_OF_YEAR], parsed[ChronoField.DAY_OF_MONTH]
                        )
                    },
                    symbol = symbol.nullIfEmpty(),
                    description = description,
                    amount = total.abs(),
                    price = if (price.isBlank()) null else BigDecimal(price),
                    quantity = if (quantity.isBlank()) null else BigDecimal(quantity)
                )
            )
        }
    }
}

class VanguardParsingException(message: String) : ParsingException(message)

fun String?.nullIfEmpty() = if (this?.length == 0) null else this
