import com.webcohesion.ofx4j.domain.data.MessageSetType
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponseMessageSet
import com.webcohesion.ofx4j.domain.data.investment.transactions.*
import com.webcohesion.ofx4j.domain.data.seclist.SecurityListResponseMessageSet
import com.webcohesion.ofx4j.io.AggregateUnmarshaller
import java.io.File
import java.text.SimpleDateFormat
import kotlin.math.abs

object KPlanStatementParser {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy")

    fun parse(inputFile: File, autohotkeyCommandFile: File) {
        // the PDF generated by my kplan doesn't have any data in it, so use the ofx file

        val unmarshaller = AggregateUnmarshaller<ResponseEnvelope>(ResponseEnvelope::class.java)

        val data = inputFile.inputStream().use {
            unmarshaller.unmarshal(it)
        }
        val dividendsFile = autohotkeyCommandFile.toPath().resolveSibling("dividends.csv").toFile()

        autohotkeyCommandFile.printWriter().use { autohotkeyCommandWriter ->
            dividendsFile.printWriter().use { dividendsWriter ->
                val tickers =
                    (data.getMessageSet(MessageSetType.investment_security) as SecurityListResponseMessageSet).securityList.securityInfos.map { securityInfo ->
                        securityInfo.securityId.uniqueId to securityInfo.tickerSymbol
                    }.toMap()

                (data.getMessageSet(MessageSetType.investment) as InvestmentStatementResponseMessageSet).statementResponse.message.investmentTransactionList.investmentTransactions.forEach { it ->
                    if (it is TransactionWithSecurity) {
                        val settlementDate = dateFormat.format(it.settlementDate)
                        val securityId = it.securityId
                        val symbol = tickers[securityId.uniqueId]

                        val description =
                            if (it is BaseBuyInvestmentTransaction || it is ReinvestIncomeTransaction) {
                                "Buy $symbol"
                            } else if (it is BaseSellInvestmentTransaction) {
                                "Sell $symbol"
                            } else {
                                "Unknown"
                            }

                        val quantity = when (it) {
                            is BaseBuyInvestmentTransaction -> it.units
                            is BaseSellInvestmentTransaction -> it.units
                            is ReinvestIncomeTransaction -> it.units
                            else -> Double.NaN
                        }
                        val amount = when (it) {
                            is BaseBuyInvestmentTransaction -> it.total
                            is BaseSellInvestmentTransaction -> it.total
                            is ReinvestIncomeTransaction -> it.total
                            else -> Double.NaN
                        }

                        // Generate AutoHotKey commands to send to GnuCash...
                        val absShares = abs(quantity)
                        val absValue = abs(amount)

                        val symbolTab =
                            if (it is BaseBuyInvestmentTransaction || it is ReinvestIncomeTransaction) {
                                "{Tab}{Tab}"
                            } else if (it is BaseSellInvestmentTransaction) {
                                "{Tab}"
                            } else {
                                "Unknown"
                            }

                        /**
                         * 06/14/2021{Tab}{Tab}Buy JCPUX {Backspace}{Tab}William Hill 401K:JCPUX{Tab}{Tab}76.93{Enter}{Tab}{Down}{Tab}8.8629{Enter}
                         * 06/14/2021{Tab}{Tab}Sell JCPUX {Backspace}{Tab}William Hill 401K:JCPUX{Tab}76.93{Enter}{Tab}{Down}{Tab}8.8629{Enter}
                         */
                        autohotkeyCommandWriter.println("$settlementDate{Tab}{Tab}${description} {Backspace}{Tab}William Hill 401K:$symbol$symbolTab$absValue{Enter}{Tab}{Down}{Tab}$absShares{Enter}")

                        if (it is ReinvestIncomeTransaction && it.incomeTypeEnum == IncomeType.DIVIDEND) {
                            dividendsWriter.println("$settlementDate|$symbol ${it.memo}|${abs(amount)}")
                        }

                    } else {
                        println("Unknown: $it")
                    }
                }
            }
        }
    }
}