package parser

import parser.input.ParserInput
import parser.output.AccountType
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
        val transactions = mutableListOf<OutputTransaction>()
        while (accountTitle != null) {
            accountTitle.groupValues[1]
            val accountNumber = accountTitle.groupValues[2]
            val nextAccountTitle = accountTitleRegex.find(pdfText, accountTitle.range.last)
            val accountStatement =
                pdfText.substring(accountTitle.range.last, nextAccountTitle?.range?.first ?: pdfText.length)
            transactions += processAccount(accountNumber, accountStatement)
            accountTitle = nextAccountTitle
        }

        transactions.forEach {
            output.write(it)
        }
    }

    private fun processAccount(accountNumber: String, accountStatement: String): List<OutputTransaction> {
        val year = statementPeriodRegex.find(accountStatement)?.let { statementPeriod ->
            statementPeriod.groupValues[3]
        } ?: LocalDate.now().year

        val lines = mutableListOf<OutputTransaction>()
        var found = linePattern.find(accountStatement)
        while (found != null) {
            val transactionDate = LocalDate.parse("${found.groupValues[1]} $year", dateFormat)
            val description = found.groupValues[2]

            val amount = when (val type = found.groupValues[3]) {
                "Debit" -> "-${found.groupValues[5].replace(",", "")}"
                "Credit" -> found.groupValues[5].replace(",", "")
                else -> throw IllegalArgumentException("Unknown transaction type: $type")
            }

            lines += OutputTransaction(
                debitAccount = accountNumber,
                creditAccount = null,
                postDate = transactionDate,
                description = description,
                amount = BigDecimal(amount),
                accountType = AccountType.CHECKING_SAVING
            )
            found = found.next()
        }
        return lines
    }
}
