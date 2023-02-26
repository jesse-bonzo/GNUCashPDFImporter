package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DiscoverStatementParser : StatementParser {
    private val startOfLinePattern = Regex("""^(\d\d/\d\d/\d\d)""", RegexOption.MULTILINE)
    private val whiteSpace = Regex("""\s+""")
    private val linePattern =
        Regex("""^(\d\d/\d\d/\d\d)\s(\d\d/\d\d/\d\d)\s(.*)\$\s(-?\d+\.\d\d)\s(.*)$""", RegexOption.MULTILINE)
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yy")
    private val accountNumberPattern = Regex("""Acct Ending (\d{4})""")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val text = input.getText()

        val accountNumber = accountNumberPattern.find(text)?.groupValues?.get(1)
            ?: throw DiscoverParsingException("Unable to find account number")

        startOfLinePattern.findAll(text).map {
            it.range.first
        }.map { start ->
            val dollarIndex = text.indexOf('$', start)
            val newLineIndex = text.indexOf('\n', dollarIndex)
            whiteSpace.replace(text.substring(start..newLineIndex), " ")
        }.forEach { line ->
            linePattern.find(line)?.let {
                val (transactionDate, postDate, description, amount, category) = it.destructured
                output.write(
                    OutputTransaction(
                        creditAccount = accountNumber,
                        postDate = LocalDate.parse(postDate, dateFormat),
                        description = description,
                        amount = BigDecimal(amount),
                    )
                )
            }
        }
    }
}

class DiscoverParsingException(message: String) : ParsingException(message)
