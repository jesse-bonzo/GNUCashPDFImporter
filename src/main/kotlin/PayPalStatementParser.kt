import java.io.File

class PayPalStatementParser {

    private val dateRegex = Regex("""\d\d/\d\d/\d\d\d\d""")
    private val endOfBlockRegex = Regex("""USD -?\d+\.\d\d -?\d+\.\d\d -?\d+\.\d\d""")
    private val firstLineRegex = Regex("""(\d\d/\d\d/\d\d\d\d)(.*)""")
    private val secondaryLineRegex = Regex("""  (.*)\s(-?\d+\.\d\d) USD""")

    fun parse(inputFile: File, outputFile: File) {
        val pdfText = extractText(inputFile)
        println(pdfText)
        val dateMatcher = dateRegex.toPattern().matcher(pdfText)
        val dateIndexes = mutableListOf<Int>()
        while (dateMatcher.find()) {
            dateIndexes.add(dateMatcher.start())
        }
        dateIndexes.add(pdfText.length)

        outputFile.printWriter().use { output ->
            (0 until dateIndexes.size - 1).map { i ->
                val potentialBlock = pdfText.substring(dateIndexes[i], dateIndexes[i + 1])
                val endMatcher = endOfBlockRegex.toPattern().matcher(potentialBlock)
                if (endMatcher.find()) {
                    potentialBlock.substring(0, endMatcher.end())
                } else {
                    potentialBlock
                }
            }.forEach { block ->
                val firstLineResult = firstLineRegex.find(block)
                val firstLine = firstLineResult?.groupValues ?: emptyList()
                val date = firstLine.getOrNull(1) ?: ""
                val description =
                    (firstLine.getOrNull(2)?.trim() ?: "") + (firstLineResult?.range?.let { firstLineRange ->
                        secondaryLineRegex.find(block)?.range?.let { secondaryLineRange ->
                            " " + block.substring(firstLineRange.last + 1, secondaryLineRange.first).trim()
                        } ?: ""
                    } ?: "")

                secondaryLineRegex.findAll(block).map {
                    val secondaryLine = it.groupValues
                    val source = secondaryLine.getOrNull(1)?.trim() ?: ""
                    val amount = secondaryLine.getOrNull(2)?.trim() ?: ""
                    StatementLine(date, source, description, amount, "0.00", amount)
                }.forEach {
                    output.println(it.toCSV())
                }

                val lastLine = (endOfBlockRegex.find(block)?.value ?: "").split(" ")
                val amount = lastLine[1]
                val fees = lastLine[2]
                val total = lastLine[3]
                output.println(StatementLine(date, description, description, amount, fees, total).toCSV())
            }
        }
    }

    data class StatementLine(
        val date: String,
        val source: String,
        val description: String,
        val amount: String,
        val fees: String,
        val total: String
    ) {
        fun toCSV(): String {
            return "$date|$source|$description|$amount|$fees|$total"
        }
    }
}