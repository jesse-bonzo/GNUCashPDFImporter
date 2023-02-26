package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

object CitiStatementParser : StatementParser {
    private val linePattern = Regex(
        """^(\d\d/\d\d)?\s?(\d\d/\d\d)\s([()\w\s\-.,*#&/!']*)\s(-?)\$([\d,]*\.\d\d)$""",
        RegexOption.MULTILINE
    )
    private val billingPeriodPattern = Regex("""(\d\d/\d\d/\d\d)-(\d\d/\d\d/\d\d)""")
    private val billingPeriodDateFormat = DateTimeFormatter.ofPattern("MM/dd/yy")
    private val txDateFormat = DateTimeFormatter.ofPattern("MM/dd")
    private val accountNumberPattern = Regex("""Account number ending in (\d{4})""")
    private val whitespace = Regex("\\s+")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val text = input.getText()

        val billingPeriod =
            billingPeriodPattern.find(text) ?: throw CitiParsingException("Unable to find billing period")
        val startDate = LocalDate.parse(billingPeriod.groupValues[1], billingPeriodDateFormat)
        val endDate = LocalDate.parse(billingPeriod.groupValues[2], billingPeriodDateFormat)

        val accountNumberMatch =
            accountNumberPattern.find(text) ?: throw CitiParsingException("Unable to find account number")
        val accountNumber = accountNumberMatch.groupValues[1]

        linePattern.findAll(text).map { matchResult ->
            val (transactionDate, postDate, description, plusMinus, amount) = matchResult.destructured

            val temporal = txDateFormat.parse(postDate)
            val postLocalDate = if (temporal[ChronoField.MONTH_OF_YEAR] == startDate.monthValue) {
                LocalDate.of(startDate.year, temporal[ChronoField.MONTH_OF_YEAR], temporal[ChronoField.DAY_OF_MONTH])
            } else {
                LocalDate.of(endDate.year, temporal[ChronoField.MONTH_OF_YEAR], temporal[ChronoField.DAY_OF_MONTH])
            }

            OutputTransaction(
                creditAccount = accountNumber,
                amount = BigDecimal(plusMinus.trim() + amount.replace(",", "").trim()),
                description = whitespace.replace(description, " "),
                postDate = postLocalDate,
            )
        }.forEach(output::write)
    }
}

class CitiParsingException(message: String) : ParsingException(message)