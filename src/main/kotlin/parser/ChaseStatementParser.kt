package parser

import parser.input.ParserInput
import parser.output.AccountType
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

object ChaseStatementParser : StatementParser {
    private val linePattern =
        Pattern.compile("""(\d\d/\d\d)\s(.*)\s(-?\d*\.\d\d)""")
    private val datePattern = Regex("""(\d\d)/(\d\d)""")
    private val accountNumberPattern = Regex("""Account Number:\s*(\d{4} \d{4} \d{4} \d{4})""")
    private val dateRangePattern = Regex("""Opening/Closing Date\s*(\d\d/\d\d/\d\d) - (\d\d/\d\d/\d\d)""")
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yy")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val text = input.getText()
        val accountNumber = accountNumberPattern.find(text)?.groups?.get(1)?.value
        val dateRange = dateRangePattern.find(text)
        val openingDate = dateRange?.groups?.get(1)?.value?.let { LocalDate.parse(it, dateFormat) } ?: LocalDate.MIN
        val closingDate = dateRange?.groups?.get(2)?.value?.let { LocalDate.parse(it, dateFormat) } ?: LocalDate.MIN

        val matcher = linePattern.matcher(text)
        val transactions = mutableListOf<OutputTransaction>()
        while (matcher.find()) {
            val transactionDate = matcher.group(1)
            val description = matcher.group(2)
            val amount = matcher.group(3)

            if (amount != "0.00") {
                transactions += OutputTransaction(
                    creditAccount = accountNumber,
                    debitAccount = null, // TODO: Maybe we can figure it out from the text?
                    postDate = parseDate(transactionDate.trim(), openingDate, closingDate),
                    description = description.trim(),
                    amount = BigDecimal(amount.trim()),
                    accountType = AccountType.CREDIT_CARD
                )
            }
        }

        transactions.forEach { outputTransaction ->
            output.write(outputTransaction)
        }
    }

    private fun parseDate(date: String, openingDate: LocalDate, closingDate: LocalDate) =
        datePattern.find(date)?.let { matchResult ->
            val month = matchResult.groups[1]?.value?.removePrefix("0")?.let(Integer::parseInt)
                ?: throw IllegalArgumentException("Invalid date: $date")
            val day = matchResult.groups[2]?.value?.removePrefix("0")?.let(Integer::parseInt)
                ?: throw IllegalArgumentException("Invalid date: $date")

            val year = when (month) {
                openingDate.monthValue -> openingDate.year
                closingDate.monthValue -> closingDate.year
                else -> LocalDate.now().year
            }

            LocalDate.of(year, month, day)
        } ?: throw IllegalArgumentException("Invalid date: $date")
}