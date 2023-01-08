import com.opencsv.CSVWriter
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CapitalOne360StatementParser {
    private val accountTitleRegex = Regex("""^([\w ]+) - (\d+)$""", RegexOption.MULTILINE)
    private val statementPeriodRegex = Regex("""^(\w\w\w \d\d?) - (\w\w\w \d\d?), (\d\d\d\d)""", RegexOption.MULTILINE)
    private val linePattern =
        Regex(
            """^(\w\w\w \d\d?) (.*) (Debit|Credit) (\+|-) (\$[\d,]+\.\d\d) (\$[\d,]+\.\d\d)$""",
            RegexOption.MULTILINE
        )
    private val dateFormat = DateTimeFormatter.ofPattern("MMM d yyyy")

    fun parse(inputFile: File) {
        // create the output directory fresh
        val outputDir = File("output")
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        if (inputFile.isDirectory) {
            inputFile.listFiles()?.forEach { file ->
                processFile(file)
            }
        } else {
            processFile(inputFile)
        }
    }

    private fun processFile(inputFile: File) {
        val pdfText = extractText(inputFile)
        var accountTitle = accountTitleRegex.find(pdfText)
        while (accountTitle != null) {
            val accountName = accountTitle.groupValues[1]
            val accountNumber = accountTitle.groupValues[2]
            val nextAccountTitle = accountTitleRegex.find(pdfText, accountTitle.range.last)
            val accountStatement =
                pdfText.substring(accountTitle.range.last, nextAccountTitle?.range?.first ?: pdfText.length)
            processAccount(accountName, accountNumber, accountStatement)
            accountTitle = nextAccountTitle
        }
    }

    private fun processAccount(accountName: String, accountNumber: String, accountStatement: String) {
        val title = "${accountName}_$accountNumber"
        val outputFile = File("output", "${title}.csv")

        val year = statementPeriodRegex.find(accountStatement)?.let { statementPeriod ->
            statementPeriod.groupValues[3]
        } ?: LocalDate.now().year

        PrintWriter(FileOutputStream(outputFile, true).bufferedWriter()).use { writer ->
            CSVWriter(writer).use { csvWriter ->
                var found = linePattern.find(accountStatement)
                while (found != null) {
                    val transactionDate = LocalDate.parse("${found.groupValues[1]} $year", dateFormat).toString()
                    val description = found.groupValues[2]

                    val amount = when (val type = found.groupValues[3]) {
                        "Debit" -> "-${found.groupValues[5]}"
                        "Credit" -> found.groupValues[5]
                        else -> throw IllegalArgumentException("Unknown transaction type: $type")
                    }

                    csvWriter.writeNext(arrayOf(transactionDate, description, amount))
                    found = found.next()
                }
            }
        }
    }

    data class StatementLine(
        val transactionDate: String,
        val description: String,
        val amount: String
    ) {
        fun toCSV(): String {
            return "${transactionDate},${description},${amount}"
        }
    }
}
