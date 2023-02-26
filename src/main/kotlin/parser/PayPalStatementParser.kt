package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object PayPalStatementParser : StatementParser {
    private val startOfLinePattern = Regex("""^\d\d/\d\d/\d\d\d\d""", RegexOption.MULTILINE)
    private val endOfLinePattern = Regex("""-?\d+\.\d\d\s-?\d+\.\d\d\s-?\d+\.\d\d$""", RegexOption.MULTILINE)
    private val linePattern = Regex(
        """^(\d\d/\d\d/\d\d\d\d)\s(.*)ID:\s(\w+)\s(\w+)\s(-?\d+\.\d\d)\s(-?\d+\.\d\d)\s(-?\d+\.\d\d)$""",
        RegexOption.MULTILINE
    )
    private val whiteSpace = Regex("""\s+""")
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private val emailPattern /* yea I know */ = Regex("""(.+@.+\.\w+)""")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val pdfText = input.getText()
        val accountEmail = emailPattern.find(pdfText)?.value
        startOfLinePattern.findAll(pdfText).forEach { startOfLineMatch ->
            endOfLinePattern.find(pdfText, startOfLineMatch.range.first)?.let { endOfLineMatch ->
                val line =
                    whiteSpace.replace(pdfText.substring(startOfLineMatch.range.first..endOfLineMatch.range.last), " ")
                linePattern.find(line)?.let {
                    val (postDate, description, id, currency, amount, fees, total) = it.destructured

                    val transaction = OutputTransaction(
                        debitAccount = accountEmail,
                        postDate = LocalDate.parse(postDate, dateFormat),
                        description = description,
                        amount = BigDecimal(total),
                        reference = id,
                    )

                    output.write(transaction)
                }
            }
        }
    }
}