package parser

import parser.input.ParserInput
import parser.output.ParserOutput
import java.util.regex.Pattern

class BankOfAmericaStatementParser : StatementParser {
    private val linePattern = Pattern.compile("""(\d\d/\d\d)\s(\d\d/\d\d)\s(.*)\s(\d+)\s(\d+)\s(-*\d+\.\d\d)""")

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
        val postDate: String,
        val referenceNumber: String,
        val description: String,
        val amount: String
    ) {

        fun toCSV(): String {
            return "$transactionDate|$referenceNumber|$description|$amount"
        }

        companion object {
            fun parse(line: List<String>): StatmentLine {
                val transactionDate = line[0]
                val postDate = line[1]
                val description = line[2]
                val referenceNumber = line[3]
                val amount = line[5]
                return StatmentLine(transactionDate, postDate, referenceNumber, description, amount)
            }
        }
    }
}