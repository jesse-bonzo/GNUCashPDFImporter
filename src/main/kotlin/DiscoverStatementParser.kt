import java.io.File
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class DiscoverStatementParser {

    // I don't remember why I put this here...
    // private val linePattern = Pattern.compile("(\\d\\d/\\d\\d)\\s(.*)\\s(-?\\\$[\\d,]+\\.\\d\\d)")

    fun parse(inputFile: File, outputFile: File) {
        val pdfText = extractText(inputFile)
        val lines = parse(pdfText)
        //+ gatherLines(pdfText, linePattern)
        outputFile.printWriter().use { writer ->
            lines.filter {
                it.amount != "\$0.00"
            }.map {
                it.toCSV()
            }.forEach {
                writer.println(it)
            }
        }
    }

    private fun parse(file: String): List<StatmentLine> {
        val lines = mutableListOf<StatmentLine>()
        var index = -1
        while (++index < file.length) {
            val c = file[index]
            if (c.isDigit()) {
                parseDate(file, index)?.let { date ->
                    // find everything between the date and a $
                    // that will be the description
                    // hopefully descriptions never have $ in them :\
                    var amountStartIndex = file.indexOf('$', index + date.length)
                    if (amountStartIndex >= 0) {
                        if (amountStartIndex > 0 && file[amountStartIndex - 1] == '-') {
                            // backup one for the negative sign
                            amountStartIndex--
                        }
                        parseAmount(file, amountStartIndex)?.let { amount ->
                            val description =
                                file.substring(index + date.length, amountStartIndex)
                                    .replace(System.lineSeparator(), " ").trim()
                            lines.add(StatmentLine(date, description, amount))
                        }
                    }
                }
            }
        }
        return lines
    }

    private fun parseDate(file: String, index: Int): String? {
        if (index + 5 < file.length) {
            // \d\d/\d\d
            if (file[index].isDigit()
                && file[index + 1].isDigit()
                && file[index + 2] == '/'
                && file[index + 3].isDigit()
                && file[index + 4].isDigit()
            ) {
                return file.substring(index, index + 5)
            }
        }
        return null
    }

    private fun parseAmount(file: String, index: Int): String? {
        val negativeFound = file[index] == '-'
        var dollarFound = file[index] == '$'
        if (dollarFound || negativeFound) {
            var curIndex = index
            var decimalFound = false
            var anyDigitsFound = false
            while (curIndex++ < file.length - 1) {
                val c = file[curIndex]
                if (!dollarFound && c == '$') {
                    dollarFound = true
                } else if (!decimalFound && c == '.') {
                    decimalFound = true
                } else if (c.isDigit()) {
                    anyDigitsFound = true
                } else {
                    break
                }
            }
            if (dollarFound && decimalFound && anyDigitsFound) {
                return file.substring(index, curIndex)
            }
        }
        return null
    }

    fun gatherLines(pdfText: String, pattern: Pattern): List<StatmentLine> {
        val matcher = pattern.matcher(pdfText)
        val lines = mutableListOf<StatmentLine>()
        while (matcher.find()) {
            lines.add(StatmentLine.parse((1..matcher.groupCount()).map { matcher.group(it) }.toList()))
        }
        return lines
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
                when (line.size) {
                    3 -> {
                        val transactionDate = line[0]
                        val description = line[1]
                        val amount = line[2]
                        return StatmentLine(transactionDate, description, amount)
                    }
                    4 -> {
                        val transactionDate =
                            MonthDay.parse(line[0], DateTimeFormatter.ofPattern("MMM dd")).atYear(LocalDate.now().year)
                                .toString()
                        val description = line[2]
                        val amount = line[3]
                        return StatmentLine(transactionDate, description, amount)
                    }
                    else -> {
                        throw RuntimeException("Unknown line!")
                    }
                }
            }
        }
    }
}
