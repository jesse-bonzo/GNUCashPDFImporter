import com.webcohesion.ofx4j.domain.data.ResponseEnvelope
import com.webcohesion.ofx4j.domain.data.common.Status
import com.webcohesion.ofx4j.domain.data.investment.accounts.InvestmentAccountDetails
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponse
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponseMessageSet
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponseTransaction
import com.webcohesion.ofx4j.domain.data.investment.transactions.*
import com.webcohesion.ofx4j.domain.data.seclist.SecurityId
import com.webcohesion.ofx4j.io.AggregateMarshaller
import com.webcohesion.ofx4j.io.v2.OFXV2Writer
import java.io.File
import java.math.BigDecimal
import java.nio.file.Paths
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("IMPLICIT_CAST_TO_ANY")
class BettermentStatementParser {

    private val dividendLinePattern = Regex("""(\w\w\w \d+ \d\d\d\d)\s(\w+)\s(.*)\s(-?\$[\d,]+\.\d\d)""")
    private val linePattern =
        Regex("""(\w+)?\s(\w\w\w \d\d? \d\d\d\d)\s(\w*)\s(\$[\d,]+\.\d\d)\s(-?\d+\.\d+)\s(-?\$[\d,]+\.\d\d)""")

    fun parse(inputFile: File) {
        val pdfText = extractText(inputFile)
        var acctIndex = pdfText.indexOf("ACCT # ")
        while (acctIndex >= 0) {
            val nextAcctIndex = pdfText.indexOf("ACCT # ", acctIndex + 1)
            val accountStatement = if (nextAcctIndex < 0) {
                pdfText.substring(acctIndex)
            } else {
                pdfText.substring(acctIndex, nextAcctIndex)
            }
            processAccount(accountStatement)

            acctIndex = nextAcctIndex
        }
    }

    private fun processAccount(accountStatement: String) {
        val accountNumber = accountStatement.substring(0, accountStatement.indexOf(')')).replace("ACCT # ", "")

        Paths.get("output", "$accountNumber dividends.csv").toFile().let { outputFile ->
            outputFile.printWriter().use { writer ->
                val start = accountStatement.indexOf("Dividend Payment Detail")
                val end = accountStatement.indexOf("Quarterly Activity Detail")
                dividendLinePattern.findAll(
                    accountStatement.substring(
                        if (start < 0) 0 else start, if (end < 0) accountStatement.length else end
                    )
                ).forEach {
                    val paymentDate = LocalDate.parse(it.groupValues[1], DateTimeFormatter.ofPattern("MMM d yyyy"))
                    val fund = it.groupValues[2]
                    val description = it.groupValues[3]
                    val amount = it.groupValues[4]
                    writer.println("$paymentDate|$fund $description|$amount")
                }
            }
        }

        val transactions: List<BaseInvestmentTransaction> = linePattern.findAll(accountStatement).map {
            val description = it.groupValues[1]
            val transactionDate = LocalDate.parse(it.groupValues[2], DateTimeFormatter.ofPattern("MMM d yyyy"))
            val fund = it.groupValues[3].trim()
            val price = it.groupValues[4].replace("$", "").replace(",", "").trim()
            val shares = it.groupValues[5].trim()
            val value = it.groupValues[6].replace("$", "").replace(",", "").trim()

            val transaction: BaseInvestmentTransaction = if (value.contains('-')) {
                SellStockTransaction().apply {
                    sellInvestment = SellInvestmentTransaction().apply {
                        unitPrice = BigDecimal(price).toDouble()
                        units = BigDecimal(shares).toDouble()
                        total = BigDecimal(value).toDouble()
                        sellType = SellType.SELL.name
                        securityId = SecurityId().apply {
                            this.uniqueId = fund
                            this.uniqueIdType = "TICKER"
                        }
                        investmentTransaction = InvestmentTransaction().apply {
                            memo = description
                            tradeDate = Date(
                                transactionDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            )
                            transactionId = UUID.randomUUID().toString()
                        }
                    }
                }
            } else {
                BuyStockTransaction().apply {
                    buyInvestment = BuyInvestmentTransaction().apply {
                        unitPrice = BigDecimal(price).toDouble()
                        units = BigDecimal(shares).toDouble()
                        total = BigDecimal(value).toDouble()
                        buyType = BuyType.BUY.name
                        securityId = SecurityId().apply {
                            this.uniqueId = fund
                            this.uniqueIdType = "TICKER"
                        }
                        investmentTransaction = InvestmentTransaction().apply {
                            memo = description
                            tradeDate = Date(
                                transactionDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            )
                            transactionId = UUID.randomUUID().toString()
                        }
                    }
                }
            }
            return@map transaction
        }.toList()

        if (transactions.isEmpty()) {
            return
        }

        Paths.get("output").toFile().mkdirs()
        val outputFile = Paths.get("output", "$accountNumber.ofx").toFile()
        val marshaller = AggregateMarshaller()
        val writer = OFXV2Writer(outputFile.writer())
        writer.isWriteAttributesOnNewLine = true
        try {
            marshaller.marshal(ResponseEnvelope().apply {
                this.messageSets = sortedSetOf(InvestmentStatementResponseMessageSet().apply {
                    this.statementResponse = InvestmentStatementResponseTransaction().apply {
                        this.uid = UUID.randomUUID().toString()
                        this.status = Status().apply {
                            this.code = Status.KnownCode.SUCCESS
                            this.severity = Status.Severity.INFO
                        }
                        this.message = InvestmentStatementResponse().apply {
                            dateOfStatement = Date()
                            account = InvestmentAccountDetails().apply {
                                this.brokerId = "Betterment"
                                this.accountNumber = accountNumber
                            }
                            this.investmentTransactionList = InvestmentTransactionList().apply {
                                this.start = transactions.map { it.tradeDate }.min()
                                this.end = transactions.map { it.tradeDate }.max()
                                this.investmentTransactions = transactions
                            }
                        }
                    }
                })
            }, writer)
        } finally {
            writer.close()
        }
    }
}