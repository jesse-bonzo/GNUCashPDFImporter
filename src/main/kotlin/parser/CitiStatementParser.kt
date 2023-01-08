package parser

import parser.input.ParserInput
import parser.output.ParserOutput
import java.util.regex.Pattern

class CitiStatementParser : StatementParser {
    private val linePattern =
        Pattern.compile(
            "^(\\d\\d/\\d\\d)?\\s?(\\d\\d/\\d\\d)\\s([()\\w\\s\\-.,*#&/!']*)\\s(-?\\\$[\\d,]*\\.\\d\\d)\$",
            Pattern.MULTILINE
        )

    override fun parse(input: ParserInput, output: ParserOutput) {
        val matcher = linePattern.matcher(input.getText())
        val lines = mutableListOf<StatmentLine>()
        while (matcher.find()) {
            lines.add(StatmentLine.parse((1..matcher.groupCount()).map {
                matcher.group(it)?.trim()?.replace('\r', ' ')?.replace('\n', ' ') ?: ""
            }.toList()))
        }
//        outputFile.printWriter().use { writer ->
//            lines.filter { it.amount != "\$0.00" }.map { it.toCSV() }.forEach { writer.println(it) }
//        }
    }

    data class StatmentLine(
        val transactionDate: String,
        val postDate: String,
        val description: String,
        val amount: String
    ) {

        fun toCSV(): String {
            return if (transactionDate.isBlank()) {
                "$postDate|$description|$amount"
            } else {
                "$transactionDate|$description|$amount"
            }
        }

        companion object {
            fun parse(line: List<String>): StatmentLine {
                val transactionDate = line[0]
                val postDate = line[1]
                val description = line[2]
                val amount = line[3]
                return StatmentLine(transactionDate, postDate, description, amount)
            }
        }
    }
}
