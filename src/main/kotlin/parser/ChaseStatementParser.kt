package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ChaseStatementParser : StatementParser {
    private val linePattern = Regex("""(\d\d/\d\d)\s(.*)\s(-?\d*\.\d\d)""")
    private val datePattern = Regex("""(\d\d)/(\d\d)""")
    private val accountNumberPattern = Regex("""Account Number:\s*(\d{4} \d{4} \d{4} \d{4})""")
    private val dateRangePattern = Regex("""Opening/Closing Date\s*(\d\d/\d\d/\d\d) - (\d\d/\d\d/\d\d)""")
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yy")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val text = input.getText()
        val accountNumber = accountNumberPattern.find(text)?.groupValues?.get(1)
            ?: throw ChaseParsingException("Unable to find account number")
        val dateRange = dateRangePattern.find(text) ?: throw ChaseParsingException("Unable to find date")
        val openingDate = LocalDate.parse(dateRange.groupValues[1], dateFormat)
        val closingDate = LocalDate.parse(dateRange.groupValues[2], dateFormat)

        linePattern.findAll(text).map { matchResult ->
            val (transactionDate, description, amount) = matchResult.destructured
            OutputTransaction(
                creditAccount = accountNumber,
                debitAccount = null, // TODO: Maybe we can figure it out from the text?
                postDate = parseDate(transactionDate.trim(), openingDate, closingDate),
                description = description.trim(),
                amount = BigDecimal(amount.trim()),
            )
        }.forEach(output::write)
    }

    private fun parseDate(date: String, openingDate: LocalDate, closingDate: LocalDate) =
        datePattern.find(date)?.let { matchResult ->
            val month = matchResult.groupValues[1].removePrefix("0").toInt()
            val day = matchResult.groupValues[2].removePrefix("0").toInt()

            val year = when (month) {
                openingDate.monthValue -> openingDate.year
                closingDate.monthValue -> closingDate.year
                else -> LocalDate.now().year
            }

            LocalDate.of(year, month, day)
        } ?: throw ChaseParsingException("Invalid date: $date")
}

class ChaseParsingException(message: String) : ParsingException(message)