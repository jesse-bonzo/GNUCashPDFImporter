import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.MissingArgumentException
import org.apache.commons.cli.Options
import parser.*
import parser.input.PDFParserInput
import parser.output.CSVParserOutput
import java.io.File

fun main(args: Array<String>) {
    try {
        val cmd = DefaultParser().parse(options(), args)
        val inputFile = File(cmd.getOptionValue("i"))
        val outputFile = File(cmd.getOptionValue("o"))
        val parser = when (val statementType = cmd.getOptionValue("t")) {
            "synchrony" -> SynchronyCardStatementParser()
            "bofa" -> BankOfAmericaStatementParser()
            "citi" -> CitiStatementParser()
            "amex" -> AmexStatementParser()
            "discover" -> DiscoverStatementParser
            "capitalone360" -> CapitalOne360StatementParser()
            "betterment" -> BettermentStatementParser()
            "robinhood" -> RobinhoodStatementParser()
            "paypal" -> PayPalStatementParser()
            "chase" -> ChaseStatementParser()
            "vanguard" -> VanguardStatementParser
            "kplan" -> KPlanStatementParser
            else -> throw Exception("Unknown statement type: \"$statementType\"")
        }

        val input = if (inputFile.exists()) {
            when (inputFile.extension) {
                "pdf" -> PDFParserInput(inputFile)
                else -> throw Exception("Unknown input type: \"$inputFile\"")
            }
        } else {
            throw Exception("Input file \"$inputFile\" does not exist")
        }

        val output = when (outputFile.extension) {
            "csv" -> CSVParserOutput(outputFile)
            "gnucash" -> throw Exception("Not supported yet") // TODO: Could be xml or sqlite
            else -> throw Exception("Unknown output type: \"$outputFile\"")
        }

        parser.parse(input, output)
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
    }
}