package parser.input

import pdf.extractText
import java.io.File

class PDFParserInput(val inputFile: File) : ParserInput {
    override fun getText() = extractText(inputFile)
}