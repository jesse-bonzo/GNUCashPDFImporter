package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object BettermentStatementParser : StatementParser {
    private val accountNumberPattern = Regex("""^Account\s+#(\d+)$""", RegexOption.MULTILINE)
    private val cashReserveInterestPayment = Regex("""(\w\w\w \d+ \d\d\d\d) (Interest Payment) \$(\d+\.\d\d)""")
    private val dateFormat = DateTimeFormatter.ofPattern("MMM d yyyy")
    private val dividendPaymentPattern =
        Regex("""^(\w\w\w \d+ \d\d\d\d)\s([A-Z]{2,4}) (.*) \$(\d+\.\d\d)$""", RegexOption.MULTILINE)
    private val activityPattern =
        Regex(
            """([\w ]+)?(\w\w\w \d+ \d\d\d\d)\s([A-Z]{2,4}) \$(\d+\.\d\d) (-?\d+\.\d+) -?\$(\d+\.\d\d)?"""
        )

    override fun parse(input: ParserInput, output: ParserOutput) {
        val pdfText = input.getText()

        val accountNumberMatches = accountNumberPattern.findAll(pdfText).toList().reversed()
        accountNumberMatches.forEachIndexed { index, accountNumberMatch ->
            val (accountNumber) = accountNumberMatch.destructured
            val nextMatch = if (index + 1 in accountNumberMatches.indices) accountNumberMatches[index + 1] else null
            val accountStatement = pdfText.substring(nextMatch?.range?.last ?: 0, accountNumberMatch.range.first)

            cashReserveInterestPayment.findAll(accountStatement).forEach { matchResult ->
                val (date, description, amount) = matchResult.destructured
                output.write(
                    OutputTransaction(
                        debitAccount = accountNumber,
                        postDate = LocalDate.parse(date, dateFormat),
                        description = description,
                        amount = BigDecimal(amount),
                    )
                )
            }
        }

        // look for HOLDINGS
        var startOfAccountIndex = pdfText.indexOf("HOLDINGS")
        var accountNumber: String? = null
        while (startOfAccountIndex != -1) {
            val startOfNextAccountIndex = pdfText.indexOf("HOLDINGS", startIndex = startOfAccountIndex + 1)

            val accountStatement = if (startOfNextAccountIndex != -1) pdfText.substring(
                startOfAccountIndex,
                startOfNextAccountIndex
            ) else pdfText.substring(startOfAccountIndex)

            accountNumber = accountNumberPattern.find(accountStatement)?.groupValues?.get(1) ?: accountNumber

            if (accountNumber != null) {
                val dividendPaymentStart = accountStatement.indexOf("DIVIDEND PAYMENT DETAIL")
                val monthlyActivityStart =
                    accountStatement.indexOf("MONTHLY ACTIVITY DETAIL", startIndex = dividendPaymentStart)

                if (dividendPaymentStart != -1 && monthlyActivityStart != -1) {
                    dividendPaymentPattern.findAll(
                        accountStatement.substring(
                            dividendPaymentStart,
                            monthlyActivityStart
                        )
                    ).forEach { matchResult ->
                        val (date, symbol, description, amount) = matchResult.destructured

                        output.write(
                            OutputTransaction(
                                postDate = LocalDate.parse(date, dateFormat),
                                debitAccount = accountNumber,
                                amount = BigDecimal(amount),
                                description = description,
                            )
                        )
                    }
                }

                if (monthlyActivityStart != -1) {
                    activityPattern.findAll(accountStatement.substring(monthlyActivityStart)).forEach { matchResult ->
                        val (description, date, symbol, price, shares, amount) = matchResult.destructured

                        output.write(
                            OutputTransaction(
                                postDate = LocalDate.parse(date, dateFormat),
                                debitAccount = if (amount.toDouble() >= 0) accountNumber else null,
                                creditAccount = if (amount.toDouble() < 0) accountNumber else null,
                                amount = BigDecimal(amount),
                                quantity = BigDecimal(shares).abs(),
                                price = BigDecimal(price),
                                symbol = symbol.trim(),
                                description = description.trim(),
                            )
                        )
                    }
                }
            }

            startOfAccountIndex = startOfNextAccountIndex
        }
    }
}

class BettermentParsingException(message: String) : ParsingException(message)