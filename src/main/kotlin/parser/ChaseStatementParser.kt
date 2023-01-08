package parser

import parser.input.ParserInput
import parser.output.ParserOutput
import java.util.regex.Pattern

class ChaseStatementParser : StatementParser {
    private val linePattern =
        Pattern.compile("""(\d\d/\d\d)\s(.*)\s(-?\d*\.\d\d)""")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val matcher = linePattern.matcher(input.getText())
        val lines = mutableListOf<StatmentLine>()
        while (matcher.find()) {
            lines.add(StatmentLine.parse((1..matcher.groupCount()).map { matcher.group(it) }.toList()))
        }
//        outputFile.printWriter().use { writer ->
//            lines.filter { it.amount != "0.00" }.map { it.toCSV() }.forEach { writer.println(it) }
//        }
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
                val transactionDate = line[0]
                val description = line[1]
                val amount = line[2]
                return StatmentLine(transactionDate, description, amount)
            }
        }
    }
}