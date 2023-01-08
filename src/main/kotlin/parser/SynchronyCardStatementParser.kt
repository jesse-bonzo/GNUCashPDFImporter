package parser

import parser.input.ParserInput
import parser.output.ParserOutput
import java.util.regex.Pattern

class SynchronyCardStatementParser : StatementParser {
    private val linePattern = Pattern.compile("""(\d\d/\d\d)\s(\w+)\s(.*)\s(-?\(*\$[\d,]+\.\d\d\)*)""")

    override fun parse(input: ParserInput, output: ParserOutput) {
        val matcher = linePattern.matcher(input.getText())
        val lines = mutableListOf<StatmentLine>()
        while (matcher.find()) {
            lines.add(StatmentLine.parse((1..matcher.groupCount()).map { matcher.group(it) }.toList()))
        }
//        outputFile.printWriter().use { writer ->
//            lines.filter { it.amount != "\$0.00" }.map { it.toCSV() }.forEach { writer.println(it) }
//        }
    }

    data class StatmentLine(
        val transactionDate: String,
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
                // if the first part is not just letters, the first part is a reference number
                val split = line[1].split(' ')
                val referenceNumber = if (split.isNotEmpty() && !split[0].matches(letters)) {
                    split[0]
                } else {
                    ""
                }
                val description = line[2]
                val amount = line[3]
                return StatmentLine(transactionDate, referenceNumber, description, amount)
            }
        }
    }
}