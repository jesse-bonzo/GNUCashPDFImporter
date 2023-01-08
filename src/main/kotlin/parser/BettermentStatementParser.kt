package parser

import parser.input.ParserInput
import parser.output.ParserOutput
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BettermentStatementParser : StatementParser {

    private val dividendLinePattern = Regex("""(\w\w\w \d+ \d\d\d\d)\s(\w+)\s(.*)\s(-?\$[\d,]+\.\d\d)""")
    private val dividendStartPattern = Regex("Dividend Payment Detail")
    private val dividendEndPattern = Regex("""^Total\s\$[\d,]+\.\d\d$""", RegexOption.MULTILINE)
    private val linePattern = Regex(
        """^([\w ]+)? ?(\w\w\w \d\d? \d\d\d\d) (\w\w\w\w?) (\$[\d,]+\.\d\d) (-?[\d,]+\.\d\d\d) (-?\$[\d,]+\.\d\d) (-?[\d,]+\.\d\d\d) (-?\$[\d,]+\.\d\d)$""",
        RegexOption.MULTILINE
    )
    private val accountStartPattern = Regex("""^([\w\s]+) \(ACCT # (\d+)\)$""", RegexOption.MULTILINE)
    private val cashReserveLinePattern = Regex("""(\w\w\w \d+, \d+)\s(.*)\s(-?\$[\d,]+\.\d\d)""")
    private val cashReserveTransactionDateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy")
    private val dividendPaymentDateFormat = DateTimeFormatter.ofPattern("MMM d yyyy")
    private val cashActivityStartPattern = Regex("^CASH ACTIVITY", RegexOption.MULTILINE)
    private val cashActivityLinePattern = Regex(
        """^(\w\w\w \d\d? \d\d\d\d) ([\w ]+) (Deposit from Linked Bank Account) (-?\$[\d,]+\.\d\d) (-?\$[\d,]+\.\d\d)$""",
        RegexOption.MULTILINE
    )

    override fun parse(input: ParserInput, output: ParserOutput) {
        val pdfText = input.getText()
        var accountPatternMatch = accountStartPattern.find(pdfText)
        while (accountPatternMatch != null) {
            val accountType = accountPatternMatch.groupValues[1]
            val accountNumber = accountPatternMatch.groupValues[2]
            val next = accountPatternMatch.next()
            val accountStatement =
                pdfText.substring(accountPatternMatch.range.first, next?.range?.first ?: pdfText.length)
            processAccount(accountType, accountNumber, accountStatement)
            accountPatternMatch = next
        }
    }

    private fun processAccount(accountType: String, accountNumber: String, accountStatement: String) {
        if (accountType == "CASH RESERVE") {

            val lines = cashReserveLinePattern.findAll(accountStatement)
            for (line in lines) {
                val date = LocalDate.parse(line.groupValues[1], cashReserveTransactionDateFormat)
                val description = line.groupValues[2]
                val amount = line.groupValues[3]
                // TODO
            }

        } else {
            // if there are goals there will be multiple blocks of dividend payments
            var dividendStart = dividendStartPattern.find(accountStatement)
            while (dividendStart != null) {
                val start = dividendStart.range.last
                val end = dividendEndPattern.find(accountStatement, start)?.range?.start ?: accountStatement.length
                val dividendStatement = accountStatement.substring(if (start < 0) 0 else start, end)
                for (line in dividendLinePattern.findAll(dividendStatement)) {
                    val paymentDate = LocalDate.parse(line.groupValues[1], dividendPaymentDateFormat)
                    val fund = line.groupValues[2]
                    val description = line.groupValues[3]
                    val amount = line.groupValues[4]
                    // TODO
                }
                dividendStart = dividendStartPattern.find(accountStatement, end)
            }

            for (cashActivityStart in cashActivityStartPattern.findAll(accountStatement)) {
                for (cashActivityLine in cashActivityLinePattern.findAll(
                    accountStatement, cashActivityStart.range.last
                )) {
                    val transactionDate = LocalDate.parse(cashActivityLine.groupValues[1], dividendPaymentDateFormat)
                    val description = cashActivityLine.groupValues[3]
                    val deposit = cashActivityLine.groupValues[4]
                    // TODO
                }
            }

            val lines = linePattern.findAll(accountStatement)
            for (line in lines) {
                val transactionDate = LocalDate.parse(line.groupValues[2], dividendPaymentDateFormat).format(
                    DateTimeFormatter.ofPattern("MM/dd/yyyy")
                )
                val fund = line.groupValues[3].trim()
                // val price = line.groupValues[4].replace("$", "").replace(",", "").trim()
                val shares = line.groupValues[5].trim()
                val value = line.groupValues[6].replace("$", "").replace(",", "").trim()
                val description = if (shares.startsWith('-')) {
                    "Sell $fund {Backspace}"
                } else {
                    "Buy $fund {Backspace}{Tab}" // buy needs an extra tab...
                }
                val absShares = shares.replace("-", "")
                val absValue = value.replace("-", "")
                // we're counting on GnuCash to send us to the right place based on the description!
                // TODO
            }
        }
    }
}