package parser.output

import java.io.File
import java.io.PrintWriter

class CSVParserOutput(outputFile: File) : ParserOutput {

    private val writer: PrintWriter = outputFile.printWriter()

    override fun write(outputTransaction: OutputTransaction) {
        writer.println(outputTransaction.toCSV())
    }

    override fun close() {
        writer.close()
    }

    private fun OutputTransaction.toCSV() = listOfNotNull(
        debitAccount,
        creditAccount,
        symbol,
        postDate,
        description,
        reference,
        price,
        quantity,
        amount,
    ).joinToString("|")
}