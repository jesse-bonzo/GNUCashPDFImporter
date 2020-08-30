import java.io.File
import java.util.regex.Pattern

class AmexStatementParser {
    private val linePattern = Pattern.compile(
        "^(\\d\\d/\\d\\d/\\d\\d\\*?)\\s([()\\w\\s\\-.,*#&/!']+)\\s(-?\\\$[\\d,]+\\.\\d\\d)\$",
        Pattern.MULTILINE
    )

    fun parse(inputFile: File, outputFile: File) {
        val pdfText = extractText(inputFile)
        val matcher = linePattern.matcher(pdfText)
        val lines = mutableListOf<StatmentLine>()
        while (matcher.find()) {
            lines.add(StatmentLine.parse((1..matcher.groupCount()).map {
                matcher.group(it)?.trim()?.replace('\r', ' ')?.replace('\n', ' ') ?: ""
            }.toList()))
        }
        outputFile.printWriter().use { writer ->
            lines.filter { it.amount != "\$0.00" }.map { it.toCSV() }.forEach { writer.println(it) }
        }
    }

    data class StatmentLine(
        val transactionDate: String,
        val description: String,
        val amount: String
    ) {

        fun toCSV(): String {
            return "$transactionDate|$description|$amount"
        }

        companion object {
            fun parse(line: List<String>): StatmentLine {
                val transactionDate = line[0].replace('*', ' ')
                val description = line[1]
                val amount = line[2]
                return StatmentLine(transactionDate, description, amount)
            }
        }
    }
}
