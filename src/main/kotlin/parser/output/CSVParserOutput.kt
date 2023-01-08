package parser.output

import java.io.File
import java.io.PrintWriter

class CSVParserOutput(val outputFile: File) : ParserOutput {

    private val writer: PrintWriter = outputFile.printWriter()

    override fun write(outputTransaction: OutputTransaction) {
        writer.println(outputTransaction.toCSV())
    }

    override fun close() {
        writer.close()
    }

    fun OutputTransaction.toCSV(): String {
        return "${fromAccount.name}|${toAccount.name}|${date}|${description}|${amount}"
    }
}