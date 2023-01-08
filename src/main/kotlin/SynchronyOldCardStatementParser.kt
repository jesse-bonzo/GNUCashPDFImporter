import java.io.File
import java.util.regex.Pattern

class SynchronyOldCardStatementParser {
    private val linePattern = Pattern.compile("""(\d\d/\d\d)\s(\d\d/\d\d)\s(.*)\s(\(*\$[\d,]+\.\d\d\)*)""")

    fun parse(inputFile: File, outputFile: File) {
        val pdfText = extractText(inputFile)
        val matcher = linePattern.matcher(pdfText)
        val lines = mutableListOf<StatmentLine>()
        while (matcher.find()) {
            lines.add(StatmentLine.parse((1..matcher.groupCount()).map { matcher.group(it) }.toList()))
        }
        outputFile.printWriter().use { writer ->
            lines.filter { it.amount != "\$0.00" }.map { it.toCSV() }.forEach { writer.println(it) }
        }
    }

    data class StatmentLine(
        val transactionDate: String,
        val postDate: String,
        val referenceNumber: String,
        val description: String,
        val amount: String
    ) {

        fun toCSV(): String {
            return "$transactionDate|$referenceNumber|$description|$amount"
        }

        companion object {
            private val letters = Regex("[A-Z]+")

            fun parse(line: List<String>): StatmentLine {
                val transactionDate = line[0]
                val postDate = line[1]
                // if the first part is not just letters, the first part is a reference number
                val split = line[2].split(' ')
                val referenceNumber = if (split.isNotEmpty() && !split[0].matches(letters)) {
                    split[0]
                } else {
                    ""
                }
                val description = if (referenceNumber.isNotEmpty()) {
                    split.drop(1).joinToString(separator = " ")
                } else {
                    line[2]
                }
                val amount = line[3]
                return StatmentLine(transactionDate, postDate, referenceNumber, description, amount)
            }
        }
    }
}