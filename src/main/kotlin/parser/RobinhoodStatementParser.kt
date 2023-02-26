package parser

import parser.input.ParserInput
import parser.output.OutputTransaction
import parser.output.ParserOutput
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object RobinhoodStatementParser : StatementParser {
    private val stockLinePattern = Regex(
        """, CUSIP: (\d+) ([A-Z]{1,4}) \w+ (Buy|Sell) (\d\d/\d\d/\d\d\d\d) (\d+) \$(\d+\.\d\d) \$(\d+\.\d\d)$""",
        RegexOption.MULTILINE
    )
    private val achLinePattern =
        Regex("""(ACH \w+) \w+ ACH (\d\d/\d\d/\d\d\d\d) \$(\d+\.\d\d)$""", RegexOption.MULTILINE)
    private val interestPattern =
        Regex("""(Interest Payment \w+) INT (\d\d/\d\d/\d\d\d\d) \$(\d+\.\d\d)$""", RegexOption.MULTILINE)
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private val accountNumberPattern = Regex("""Account #:(\d+)""")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val text = input.getText()
        val accountNumber = accountNumberPattern.find(text)?.groupValues?.get(1)
            ?: throw RobinhoodParsingException("Unable to find account number")
        val accountActivityIndex = text.indexOf("Account Activity")
        val importantInformationIndex = text.indexOf("Important Information", accountActivityIndex)

        val accountActivity = text.substring(accountActivityIndex, importantInformationIndex)

        stockLinePattern.findAll(accountActivity).forEach { match ->
            val (cusip, symbol, transaction, date, quantity, price, amount) = match.destructured

            var debitAccount: String? = null
            var creditAccount: String? = null
            if (transaction == "Buy") {
                debitAccount = cusip
            } else if (transaction == "Sell") {
                creditAccount = cusip
            }

            output.write(
                OutputTransaction(
                    debitAccount = debitAccount,
                    creditAccount = creditAccount,
                    postDate = LocalDate.parse(date, dateFormat),
                    amount = BigDecimal(amount),
                    price = BigDecimal(price),
                    quantity = BigDecimal(quantity),
                    description = symbol
                )
            )
        }

        achLinePattern.findAll(accountActivity).forEach { match ->
            val (description, date, amount) = match.destructured

            var debitAccount: String? = null
            var creditAccount: String? = null
            if (description.contains("Withdrawal")) {
                debitAccount = accountNumber
            } else {
                creditAccount = accountNumber
            }

            output.write(
                OutputTransaction(
                    debitAccount = debitAccount,
                    creditAccount = creditAccount,
                    postDate = LocalDate.parse(date, dateFormat),
                    amount = BigDecimal(amount),
                    description = description
                )
            )
        }

        interestPattern.findAll(accountActivity).forEach { match ->
            val (description, date, amount) = match.destructured

            output.write(
                OutputTransaction(
                    creditAccount = accountNumber,
                    postDate = LocalDate.parse(date, dateFormat),
                    amount = BigDecimal(amount),
                    description = description
                )
            )
        }
    }
}

class RobinhoodParsingException(message: String) : ParsingException(message)
