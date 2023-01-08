package pdf

import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

fun extractText(file: File, block: PDFTextStripper.() -> Unit = {}): String = RandomAccessFile(file, "r").use {
    PDFParser(it).apply {
        parse()
    }.pdDocument
}.use { document ->
    val stripper = PDFTextStripper().apply(block)
    stripper.getText(document)
}