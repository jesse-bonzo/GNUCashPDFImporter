import configuration.Configuration
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.MissingArgumentException
import org.apache.commons.cli.Options
import parser.*
import parser.input.PDFParserInput
import parser.output.CSVParserOutput
import parser.output.SQLiteParserOutput
import parser.output.XMLParserOutput
import java.io.File
import java.util.zip.GZIPInputStream

fun main(args: Array<String>) {
    try {
        val cmd = DefaultParser().parse(options(), args)
        val inputFile = File(cmd.getOptionValue("i"))
        val outputFile = File(cmd.getOptionValue("o"))
        val statementType = cmd.getOptionValue("t")
        val accountsYml = cmd.getOptionValue("a")

        val configuration = configuration(accountsYml)

        input(inputFile).use { input ->
            output(outputFile, configuration).use { output ->
                parser(statementType).parse(input, output)
            }
        }
    } catch (missingArgumentException: MissingArgumentException) {
        // TODO: be more helpful
        println("-i inputfile -o outputfile -t type")
    }
}

fun options(): Options {
    return Options().apply {
        addRequiredOption("i", "input", true, "input file name")
        addRequiredOption("o", "output", true, "output file name")
        addRequiredOption("t", "type", true, "statement type")
        addRequiredOption("a", "accounts-yml", true, "Accounts yaml configuration file")
    }
}

fun configuration(accountsYmlFile: String) = Configuration(accountsYmlFile)

fun parser(statementType: String) = when (statementType) {
    "synchrony" -> SynchronyCardStatementParser()
    "bofa" -> BankOfAmericaStatementParser()
    "citi" -> CitiStatementParser()
    "amex" -> AmexStatementParser()
    "discover" -> DiscoverStatementParser
    "capitalone360" -> CapitalOne360StatementParser
    "betterment" -> BettermentStatementParser()
    "robinhood" -> RobinhoodStatementParser()
    "paypal" -> PayPalStatementParser()
    "chase" -> ChaseStatementParser
    "vanguard" -> VanguardStatementParser
    "kplan" -> KPlanStatementParser
    else -> throw Exception("Unknown statement type: \"$statementType\"")
}

fun input(inputFile: File) = if (inputFile.exists()) {
    when (inputFile.extension) {
        "pdf" -> PDFParserInput(inputFile)
        else -> throw Exception("Unknown input type: \"$inputFile\"")
    }
} else {
    throw Exception("Input file \"$inputFile\" does not exist")
}

fun output(outputFile: File, configuration: Configuration) = when (outputFile.extension) {
    "csv" -> CSVParserOutput(outputFile)
    "gnucash" -> if (isGzipped(outputFile)) {
        XMLParserOutput(outputFile)
    } else {
        SQLiteParserOutput(outputFile, configuration.accountsConfiguration)
    }

    else -> throw Exception("Unknown output type: \"$outputFile\"")
}

fun isGzipped(file: File): Boolean = file.inputStream().use { inputStream ->
    val firstByte = inputStream.read().toUInt()
    val secondByte = inputStream.read().toUInt()
    val magic = firstByte.or(secondByte.shl(8))
    return magic.toInt() == GZIPInputStream.GZIP_MAGIC
}