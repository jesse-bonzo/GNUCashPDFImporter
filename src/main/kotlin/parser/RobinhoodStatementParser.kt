package parser

import parser.input.ParserInput
import parser.output.ParserOutput
import java.util.regex.Pattern

class RobinhoodStatementParser : StatementParser {
    private val linePattern =
        Pattern.compile("(ACH Deposit|([A-Z]{1,4})) Margin ([A-Za-z]*) (\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d) (\\d+ )*(\\\$[\\d,]+\\.\\d\\d)(\\s\\\$[\\d,]+\\.\\d\\d)*")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val matcher = linePattern.matcher(input.getText())
        val lines = mutableListOf<StatmentLine>()
        while (matcher.find()) {
            lines.add(StatmentLine.parse((1..matcher.groupCount()).mapNotNull { matcher.group(it) }.toList()))
        }
//        outputFile.printWriter().use { writer ->
//            lines.filter { it.amount != "\$0.00" }.map { it.toCSV() }.forEach { writer.println(it) }
//        }
    }

    data class StatmentLine(
        val symbol: String,
        val transaction: String,
        val date: String,
        val quantity: String,
        val price: String,
        val amount: String
    ) {
        fun toCSV(): String {
            return "$symbol|$transaction|$date|$quantity|$price|$amount"
        }

        companion object {
            fun parse(line: List<String>): StatmentLine {
                if (line.size == 4 && line[1] == "ACH") {
                    return StatmentLine(line[1], line[0], line[2], "", "", line[3])
                } else {
                    return StatmentLine(line[1], line[2], line[3], line[4], line[5], line[6])
                }
            }
        }
    }
}
