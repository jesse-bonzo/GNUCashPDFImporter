package parser

import parser.input.ParserInput
import parser.output.ParserOutput
import java.util.regex.Pattern

class AmexStatementParser : StatementParser {
    private val linePattern = Pattern.compile(
        "^(\\d\\d/\\d\\d/\\d\\d\\*?)\\s([()\\w\\s\\-.,*#&/!']+)\\s(-?\\\$[\\d,]+\\.\\d\\d)\$",
        Pattern.MULTILINE
    )

    override fun parse(input: ParserInput, output: ParserOutput) {
        val matcher = linePattern.matcher(input.getText())
        val lines = mutableListOf<StatementLine>()
        while (matcher.find()) {
            lines.add(StatementLine.parse((1..matcher.groupCount()).map {
                matcher.group(it)?.trim()?.replace('\r', ' ')?.replace('\n', ' ') ?: ""
            }.toList()))
        }
//        outputFile.printWriter().use { writer ->
//            lines.filter { it.amount != "\$0.00" }.map { it.toCSV() }.forEach { writer.println(it) }
//        }
    }

    data class StatementLine(
        val transactionDate: String,
        val description: String,
        val amount: String
    ) {

        fun toCSV(): String {
            return "$transactionDate|$description|$amount"
        }

        companion object {
            fun parse(line: List<String>): StatementLine {
                val transactionDate = line[0].replace('*', ' ')
                val description = line[1]
                val amount = line[2]
                return StatementLine(transactionDate, description, amount)
            }
        }
    }
}
