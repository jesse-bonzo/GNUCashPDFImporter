import java.io.File

class CapitalOne360StatementParser {
    private val savingsAccountTitle = Regex("Account: (.*) Annual Percentage Yield Earned:")
    private val cdTitle = Regex("^Account: (.*)$", RegexOption.MULTILINE)
    private val linePattern =
        Regex("""^(.*) (\d\d/\d\d/\d\d\d\d) (\$\(?[\d,]*\.\d\d\)?) (\$\(?[\d,]*\.\d\d\)?)$""", RegexOption.MULTILINE)

    fun parse(inputFile: File) {
        val pdfText = extractText(inputFile)
        var accountStartIndex = pdfText.indexOf("Account:")
        val accountStatements = mutableListOf<String>()
        while (accountStartIndex >= 0) {
            val nextAccountStartIndex = pdfText.indexOf("Account:", startIndex = accountStartIndex + 1)
            accountStatements += if (nextAccountStartIndex < 0) {
                pdfText.substring(accountStartIndex)
            } else {
                pdfText.substring(accountStartIndex, nextAccountStartIndex)
            }
            accountStartIndex = nextAccountStartIndex
        }

        accountStatements.forEach { accountStatement ->
            processAccount(accountStatement)
        }
    }

    private fun processAccount(accountStatement: String) {
        val title = findAccountTitle(accountStatement)
        File("output").mkdirs()
        val outputFile = File("output", "${title}.csv")
        outputFile.printWriter().use { writer ->
            var found = linePattern.find(accountStatement)
            while (found != null) {
                val description = found.groups[1]?.value
                val transactionDate = found.groups[2]?.value
                val amount = found.groups[3]?.value
                writer.println(
                    StatementLine(
                        transactionDate.orEmpty(),
                        description.orEmpty(),
                        amount.orEmpty()
                    ).toCSV()
                )
                found = found.next()
            }
        }
    }

    private fun findAccountTitle(accountStatement: String): String? {
        val found = savingsAccountTitle.find(accountStatement) ?: cdTitle.find(accountStatement)
        val title = found?.groups?.get(1)
        return title?.value
    }

    data class StatementLine(
        val transactionDate: String,
        val description: String,
        val amount: String
    ) {
        fun toCSV(): String {
            return "$transactionDate|$description|$amount"
        }
    }
}
