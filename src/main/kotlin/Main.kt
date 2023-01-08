import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import java.io.File

fun main(args: Array<String>) {
    val cmd = DefaultParser().parse(options(), args)
    val inputFile = File(cmd.getOptionValue("i"))
    val outputFile = File(cmd.getOptionValue("o"))
    when (val statementType = cmd.getOptionValue("t")) {
        "synchrony" -> SynchronyCardStatementParser().parse(inputFile, outputFile)
        "synchrony_old" -> SynchronyOldCardStatementParser().parse(inputFile, outputFile)
        "bofa" -> BankOfAmericaStatementParser().parse(inputFile, outputFile)
        "citi" -> CitiStatementParser().parse(inputFile, outputFile)
        "amex" -> AmexStatementParser().parse(inputFile, outputFile)
        "discover" -> DiscoverStatementParser().parse(inputFile, outputFile)
        "capitalone360" -> CapitalOne360StatementParser().parse(inputFile)
        "betterment" -> BettermentStatementParser().parse(inputFile)
        "robinhood" -> RobinhoodStatementParser().parse(inputFile, outputFile)
        "paypal" -> PayPalStatementParser().parse(inputFile, outputFile)
        "chase" -> ChaseStatementParser().parse(inputFile, outputFile)
        "vanguard" -> VanguardStatementParser.parse(inputFile, outputFile)
        "kplan" -> KPlanStatementParser.parse(inputFile, outputFile)
        else -> println("Unknown statement type $statementType")
    }
}

fun options(): Options {
    return Options().apply {
        addOption("i", "input", true, "input file name")
        addOption("o", "output", true, "output file name")
        addOption("t", "type", true, "statement type")
    }
}