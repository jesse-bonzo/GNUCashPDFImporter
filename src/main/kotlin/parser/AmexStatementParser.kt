package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AmexStatementParser : StatementParser {
    private val linePattern =
        Regex("""^(\d\d/\d\d/\d\d\*?)\s([()\w\s\-.,*#&/!']+)\s(-?\$[\d,]+\.\d\d)$""", RegexOption.MULTILINE)
    private val accountNumberPattern = Regex("""Account Ending (\d-\d{5})""")
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yy")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val text = input.getText()
        val accountNumber = accountNumberPattern.find(text)?.groupValues?.firstOrNull()
            ?: throw AmexParsingException("Unable to find account number")

        linePattern.findAll(text).map { matchResult ->
            val transactionDate = matchResult.groupValues[1].replace("*", "").trim()
            val description = matchResult.groupValues[2].trim()
            val amount = matchResult.groupValues[3].trim().replace("$", "")

            OutputTransaction(
                debitAccount = null,
                creditAccount = accountNumber,
                postDate = LocalDate.parse(transactionDate, dateFormat),
                description = description,
                amount = BigDecimal(amount)
            )
        }.forEach {
            output.write(it)
        }
    }
}

class AmexParsingException(message: String) : ParsingException(message)
