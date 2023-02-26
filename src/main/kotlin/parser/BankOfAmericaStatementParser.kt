package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

object BankOfAmericaStatementParser : StatementParser {
    private val linePattern = Regex("""(\d\d/\d\d)\s(\d\d/\d\d)\s(.*)\s(\d+)\s(\d+)\s(-*\d+\.\d\d)""")
    private val accountNumberPattern = Regex("""Account Number: (\d{4} \d{4} \d{4} \d{4})""")
    private val dateRange = Regex("""(\w+ \d{2}) - (\w+ \d{2}, \d{4})""")
    private val dateRangeStartFormat = DateTimeFormatter.ofPattern("MMMM dd")
    private val dateRangeEndFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
    private val postDateFormat = DateTimeFormatter.ofPattern("MM/dd")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val text = input.getText()
        val accountNumber = accountNumberPattern.find(text)?.groupValues?.firstOrNull()
            ?: throw BankOfAmericaParsingException("Unable to find account number")

        val dateRangeMatch =
            dateRange.find(text) ?: throw BankOfAmericaParsingException("Unable to find dates for statement")
        val dateRangEnd = LocalDate.parse(dateRangeMatch.groupValues[2], dateRangeEndFormat)
        val temporal = dateRangeStartFormat.parse(dateRangeMatch.groupValues[1])
        val startMonth = Month.of(temporal[ChronoField.MONTH_OF_YEAR])
        val startDay = temporal[ChronoField.DAY_OF_MONTH]
        val startYear = if (dateRangEnd.month == Month.JANUARY && startMonth == Month.DECEMBER) {
            dateRangEnd.year - 1
        } else {
            dateRangEnd.year
        }
        val dateRangeStart = LocalDate.of(startYear, startMonth, startDay)

        linePattern.findAll(text).map { matchResult ->
            val (transactionDate, postDate, description, referenceNumber, amount) = matchResult.destructured

            val postDateTemporal = postDateFormat.parse(postDate)

            OutputTransaction(
                creditAccount = accountNumber,
                postDate = LocalDate.of(
                    if (postDateTemporal[ChronoField.MONTH_OF_YEAR] == dateRangeStart.monthValue) {
                        dateRangeStart.year
                    } else {
                        dateRangEnd.year
                    },
                    postDateTemporal[ChronoField.MONTH_OF_YEAR],
                    postDateTemporal[ChronoField.DAY_OF_MONTH]
                ),
                description = description,
                reference = referenceNumber,
                amount = BigDecimal(amount)
            )
        }.forEach(output::write)
    }
}

class BankOfAmericaParsingException(message: String) : ParsingException(message)