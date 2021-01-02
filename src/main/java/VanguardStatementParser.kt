import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object VanguardStatementParser {
    private val startLinePattern = Regex("""^\d\d/\d\d/\d\d\d\d""", RegexOption.MULTILINE)
    private val endLinePattern = Regex("""-?\$[\d,]+\.\d\d$|—$""", RegexOption.MULTILINE)

    private val stockTransactionLinePattern =
        Regex("""(\d\d/\d\d/\d\d\d\d) (\d\d/\d\d/\d\d\d\d) (\w\w\w\w?) (.*) (Transfer.*|Sweep.*|Reinvestment.*|Dividend.*|Capital gain.*|Buy|Sell|Stock split) Cash (\d+\.\d\d\d\d) (-?\$[\d,]+\.\d\d) (—|Free) (-?\$[\d,]+\.\d\d)""")
    private val dividendLinePattern =
        Regex("""(\d\d/\d\d/\d\d\d\d) (\d\d/\d\d/\d\d\d\d) (\w\w\w\w?) (.*) Dividend Cash — — — (-?\$[\d,]+\.\d\d)""")
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    fun parse(inputFile: File, autohotkeyCommandFile: File) {
        val pdfText = extractText(inputFile)
        val dividendsFile = autohotkeyCommandFile.toPath().resolveSibling("dividends.csv").toFile()
        autohotkeyCommandFile.printWriter().use { autohotkeyCommandWriter ->
            dividendsFile.printWriter().use { dividendsWriter ->
                var startMatch = startLinePattern.find(pdfText)
                while (startMatch != null) {
                    val endMatch = endLinePattern.find(pdfText, startMatch.range.last)

                    val line =
                        pdfText.substring(startMatch.range.first, endMatch?.range?.last?.plus(1) ?: pdfText.length)
                            .replace('\n', ' ').replace('\r', ' ').trim()

                    val stockTransactionLineMatch = stockTransactionLinePattern.matchEntire(line)
                    if (stockTransactionLineMatch != null) {
                        val settlementDate =
                            LocalDate.parse(stockTransactionLineMatch.groupValues[1], dateFormat)
                                .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                        val symbol = stockTransactionLineMatch.groupValues[3]
                        val transactionType = stockTransactionLineMatch.groupValues[5]
                        val quantity = stockTransactionLineMatch.groupValues[6]
                        val amount = stockTransactionLineMatch.groupValues[9]

                        val description =
                            if (transactionType.startsWith("Buy") || transactionType.startsWith("Reinvestment")) {
                                "Buy $symbol {Backspace}{Tab}"
                            } else {
                                "Sell $symbol {Backspace}"
                            }
                        // Generate AutoHotKey commands to send to GnuCash...
                        val absShares = quantity.replace("-", "")
                        val absValue = amount.replace("-", "")

                        // we're counting on GnuCash to send us to the right place based on the description!
                        autohotkeyCommandWriter.println("$settlementDate{Tab}{Tab}${description}Vanguard IRA:$symbol{Tab}{Tab}$absValue{Enter}{Tab}{Down}{Tab}$absShares{Enter}")
                    } else {
                        val dividendLineMatch = dividendLinePattern.matchEntire(line)
                        if (dividendLineMatch != null) {
                            val settlementDate = LocalDate.parse(dividendLineMatch.groupValues[1], dateFormat)
                            val symbol = dividendLineMatch.groupValues[3]
                            val description = dividendLineMatch.groupValues[4]
                            val amount = dividendLineMatch.groupValues[5]
                            dividendsWriter.println("$settlementDate|$symbol|$description|$amount")
                        } else {
                            println("Manually handle this: $line")
                        }
                    }

                    startMatch = startLinePattern.find(pdfText, startMatch.range.last)
                }
            }
        }
    }
}