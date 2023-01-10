package parser.input

import pdf.extractText
import java.io.File

class PDFParserInput(private val inputFile: File) : ParserInput {
    override fun getText() = extractText(inputFile)
    override fun close() {
        // nothing to close
    }
}