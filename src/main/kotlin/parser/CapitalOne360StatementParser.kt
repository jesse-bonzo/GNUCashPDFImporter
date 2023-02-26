package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CapitalOne360StatementParser : StatementParser {
    private val accountTitleRegex = Regex("""^([\w ]+) - (\d+)$""", RegexOption.MULTILINE)
    private val statementPeriodRegex = Regex("""^(\w\w\w \d\d?) - (\w\w\w \d\d?), (\d\d\d\d)""", RegexOption.MULTILINE)
    private val linePattern = Regex(
        """^(\w\w\w \d\d?) (.*) (Debit|Credit) (\+|-) \$([\d,]+\.\d\d) \$([\d,]+\.\d\d)$""",
        RegexOption.MULTILINE
    )
    private val dateFormat = DateTimeFormatter.ofPattern("MMM d yyyy")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val pdfText = input.getText()
        var accountTitle = accountTitleRegex.find(pdfText)
        while (accountTitle != null) {
            val (accountName, accountNumber) = accountTitle.destructured
            val nextAccountTitle = accountTitleRegex.find(pdfText, accountTitle.range.last)
            val accountStatement =
                pdfText.substring(accountTitle.range.last, nextAccountTitle?.range?.first ?: pdfText.length)
            processAccount(accountNumber, accountStatement).forEach(output::write)
            accountTitle = nextAccountTitle
        }
    }

    private fun processAccount(accountNumber: String, accountStatement: String): List<OutputTransaction> {
        val year = statementPeriodRegex.find(accountStatement)?.let { statementPeriod ->
            statementPeriod.groupValues[3]
        } ?: throw CapitalOneParsingException("Unable to determine year")

        return linePattern.findAll(accountStatement).map { found ->
            val (date, description, type, plusMinus, amount, balance) = found.destructured
            val transactionDate = LocalDate.parse("$date $year", dateFormat)
            val txAmount = plusMinus.trim() + amount.trim().replace(",", "")

            OutputTransaction(
                debitAccount = accountNumber,
                creditAccount = null,
                postDate = transactionDate,
                description = description,
                amount = BigDecimal(txAmount),
            )
        }.toList()
    }
}

class CapitalOneParsingException(message: String) : ParsingException(message)
